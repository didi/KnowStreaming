结论：按照时间的过期策略，时间是按照时间索引文件的最后一条数据里面记录的时间作为是否过期的判断标准。那么另外一个问题，这个时间是怎么写入的，继续看下面

```java
  /**
   * If topic deletion is enabled, delete any log segments that have either expired due to time based retention
   * or because the log size is > retentionSize.
   * Whether or not deletion is enabled, delete any log segments that are before the log start offset
   */
  // 清理策略
  def deleteOldSegments(): Int = {
    if (config.delete) {
      // 清理策略：保存时间、保存大小、开始offset
      deleteRetentionMsBreachedSegments() + deleteRetentionSizeBreachedSegments() + deleteLogStartOffsetBreachedSegments()
    } else {
      deleteLogStartOffsetBreachedSegments()
    }
  }

  // 调用按照时间的清理策略
  private def deleteRetentionMsBreachedSegments(): Int = {
    if (config.retentionMs < 0) return 0
    val startMs = time.milliseconds
    // segment按照 startMs - segment.largestTimestamp > config.retentionMs 的策略进行清理.
    // 那么我们再看一下 segment.largestTimestamp 的时间是怎么获取的
    deleteOldSegments((segment, _) => startMs - segment.largestTimestamp > config.retentionMs,
      reason = s"retention time ${config.retentionMs}ms breach")
  }

  /**
   * The largest timestamp this segment contains.
   */
  // LogSegment的代码，可以发现如果maxTimestampSoFar>0时，就是maxTimestampSoFar，否则是最近一次修改时间
  // 那么maxTimestampSoFar是怎么获取的呢
  def largestTimestamp = if (maxTimestampSoFar >= 0) maxTimestampSoFar else lastModified


  // maxTimestampSoFar相当于是时间索引的最后一个entry的时间，那么我们继续看一下timeIndex.lastEntry是什么时间
  def maxTimestampSoFar: Long = {
    if (_maxTimestampSoFar.isEmpty)
      _maxTimestampSoFar = Some(timeIndex.lastEntry.timestamp)
    _maxTimestampSoFar.get
  }

  // 获取时间索引的最后一个entry里面的时间&offset
  // 在同一个时间索引文件里面，时间字段是单调递增的，因此这里获取到的是时间索引里面最大的那个时间。
  // 那么这个时间是怎么写入的呢？我们继续往下看，看时间索引的写入这块
  private def lastEntryFromIndexFile: TimestampOffset = {
    inLock(lock) {
      _entries match {
        case 0 => TimestampOffset(RecordBatch.NO_TIMESTAMP, baseOffset)
        case s => parseEntry(mmap, s - 1)
      }
    }
  }
```


时间索引文件这个时间是如何写入的？
结论：如果配置了LOG_APPEND_TIME，那么就是写入服务器的时间。如果是配置CREATE_TIME，那么就是record时间里面的最大的那一个。

```java
// 从TimeIndex类的maybeAppend方法一步一步的向上查找，查看里面的时间数据的写入，我们可以发现
// 这个时间是在 LogValidator.validateMessagesAndAssignOffsets 这个方法里面生成的
    // 遍历records
    for (batch <- records.batches.asScala) {
      validateBatch(topicPartition, firstBatch, batch, origin, toMagicValue, brokerTopicStats)
      val recordErrors = new ArrayBuffer[ApiRecordError](0)
      for ((record, batchIndex) <- batch.asScala.view.zipWithIndex) {
        validateRecord(batch, topicPartition, record, batchIndex, now, timestampType,
          timestampDiffMaxMs, compactedTopic, brokerTopicStats).foreach(recordError => recordErrors += recordError)
        // we fail the batch if any record fails, so we stop appending if any record fails
        if (recordErrors.isEmpty)
          // 拼接offset，这里还会计算那个时间戳
          builder.appendWithOffset(offsetCounter.getAndIncrement(), record)
      }
      processRecordErrors(recordErrors)
    }
    // 
    private long appendLegacyRecord(long offset, long timestamp, ByteBuffer key, ByteBuffer value, byte magic) throws IOException {
        ensureOpenForRecordAppend();
        if (compressionType == CompressionType.NONE && timestampType == TimestampType.LOG_APPEND_TIME)
            // 定义了LOG_APPEND_TIME，则使用logAppendTime，
            timestamp = logAppendTime;

        int size = LegacyRecord.recordSize(magic, key, value);
        AbstractLegacyRecordBatch.writeHeader(appendStream, toInnerOffset(offset), size);

        if (timestampType == TimestampType.LOG_APPEND_TIME)
            timestamp = logAppendTime;
        long crc = LegacyRecord.write(appendStream, magic, timestamp, key, value, CompressionType.NONE, timestampType);
        // 时间计算
        recordWritten(offset, timestamp, size + Records.LOG_OVERHEAD);
        return crc;
    }

    // 最值的计算
    private void recordWritten(long offset, long timestamp, int size) {
        if (numRecords == Integer.MAX_VALUE)
            throw new IllegalArgumentException("Maximum number of records per batch exceeded, max records: " + Integer.MAX_VALUE);
        if (offset - baseOffset > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Maximum offset delta exceeded, base offset: " + baseOffset +
                    ", last offset: " + offset);
        numRecords += 1;
        uncompressedRecordsSizeInBytes += size;
        lastOffset = offset;
        if (magic > RecordBatch.MAGIC_VALUE_V0 && timestamp > maxTimestamp) {
            // 时间更新，最后时间索引记录的是maxTimestamp这个字段
            maxTimestamp = timestamp;
            offsetOfMaxTimestamp = offset;
        }
    }
```