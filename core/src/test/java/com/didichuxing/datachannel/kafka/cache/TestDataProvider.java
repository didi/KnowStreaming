package com.didichuxing.datachannel.kafka.cache;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataProvider implements DataProvider {

    private final  Random random = new Random();

    private List<Element> data = new ArrayList<>();

    public TestDataProvider(boolean create) {
        if (create)
            createElements(); {
        }
        loadElements();;
    }

    private void createElements() {
        List<Element> elements = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        for (int i= -1800; i < 3600*2; i++) {
            Element element = new Element();
            element.key = String.format("appid_%04d", random.nextInt(3000));
            element.value = random.nextInt(100000);
            element.timestamp = timestamp + (i + 3-random.nextInt(6))*1000;
            elements.add(element);
        }

        elements.sort((f1, f2)->{ return (int)(f1.timestamp - f2.timestamp); });
        data = elements;
    }

    private void loadElements() {
    }

    @Override
    public Dataset fetchData(long startTime, long endTime) throws Exception {
        if (startTime == 0) {
            List<DataRecord> entries = new ArrayList<>();
            for (Element element : data) {
                if (element.timestamp < endTime) {
                    DataRecord record = new DataRecord<String, Integer>(element.key, element.value,
                            DataRecord.Operation.update, element.timestamp);
                    entries.add(record);
                } else {
                    break;
                }
            }
            Dataset dataset = new Dataset(entries, endTime);
            return dataset;
        }else {
            long now = System.currentTimeMillis();
            long timestamp = startTime;
            List<DataRecord> entries = new ArrayList<>();
            for (Element element : data) {
                if (element.timestamp < startTime) {
                    continue;
                } else if (element.timestamp >= now) {
                    break;
                } else {
                    timestamp = element.timestamp;
                    DataRecord record = new DataRecord<String, Integer>(element.key, element.value,
                            DataRecord.Operation.update, element.timestamp);
                    entries.add(record);
                }
            }
            Dataset dataset = new Dataset(entries, timestamp);
            return dataset;
        }
    }
    public String getRandomKey() {
        String key = String.format("appid_%04d", random.nextInt(3000));
        return key;
    }

    public Element getElement(String key, long timestamp) {
        Element el = null;
        for (Element element : data) {
            if (element.timestamp < timestamp) {
                if (element.key.equals(key)) {
                    el = element;
                }
            } else {
               break;
            }
        }
        return el;
    }

    public long getLastTimestamp() {
        long now = System.currentTimeMillis();
        Element el = null;
        for (Element element : data) {
            if (element.timestamp >= now) {
                break;
            }
            el = element;
        }
        return el.timestamp;
    }

    public class Element {
        long timestamp;
        String key;
        int value;
    }
}
