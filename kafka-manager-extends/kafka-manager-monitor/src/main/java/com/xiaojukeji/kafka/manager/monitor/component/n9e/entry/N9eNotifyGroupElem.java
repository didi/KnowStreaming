package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

/**
 * @author zengqiao
 * @date 20/10/19
 */
public class N9eNotifyGroupElem {
    private Integer creator;

    private Integer id;

    private String ident;

    private String last_updated;

    private Integer mgmt;

    private String name;

    private String note;

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public Integer getMgmt() {
        return mgmt;
    }

    public void setMgmt(Integer mgmt) {
        this.mgmt = mgmt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "N9eNotifyGroupElem{" +
                "creator=" + creator +
                ", id=" + id +
                ", ident='" + ident + '\'' +
                ", last_updated='" + last_updated + '\'' +
                ", mgmt=" + mgmt +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}