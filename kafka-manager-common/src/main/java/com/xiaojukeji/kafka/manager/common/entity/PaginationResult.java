package com.xiaojukeji.kafka.manager.common.entity;

import java.util.List;

public class PaginationResult<T> extends Result<List<T>> {

  private Pagination pagination;

  public PaginationResult(List<T> data, long pageNo, long pageSize, long total) {
    super(data);
    this.pagination = new Pagination(pageNo, pageSize, total);
  }

  public Pagination getPagination() {
    return pagination;
  }

  public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }

  protected class Pagination {
    private long pageNo;
    private long pageSize;
    private long total;

    public Pagination(long pageNo, long pageSize, long total) {
      this.pageNo = pageNo;
      this.pageSize = pageSize;
      this.total = total;
    }

    public long getPageNo() {
      return pageNo;
    }

    public void setPageNo(long pageNo) {
      this.pageNo = pageNo;
    }

    public long getPageSize() {
      return pageSize;
    }

    public void setPageSize(long pageSize) {
      this.pageSize = pageSize;
    }

    public long getTotal() {
      return total;
    }

    public void setTotal(long total) {
      this.total = total;
    }
  }
}
