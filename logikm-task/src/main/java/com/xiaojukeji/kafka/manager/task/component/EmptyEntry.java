package com.xiaojukeji.kafka.manager.task.component;

/**
 * @author zengqiao
 * @date 20/8/11
 */
public class EmptyEntry implements Comparable<EmptyEntry> {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "EmptyEntry{" +
                "id=" + id +
                '}';
    }

    @Override
    public int compareTo(EmptyEntry emptyEntry) {
        return this.id.compareTo(emptyEntry.id);
    }

}