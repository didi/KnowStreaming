package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;


public class OrderDetailGatewayConfigData extends AbstractOrderDetailData {
    private Long id;

    private String type;

    private String name;

    private String value;

    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OrderDetailGatewayConfigData{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", version=" + version +
                '}';
    }
}
