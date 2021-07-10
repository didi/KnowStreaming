package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

/**
 * @author zhongyuankai
 * @date 20/5/19
 */
public class OrderDetailApplyClusterDTO extends AbstractOrderDetailData {
    private String idc;

    private Long bytesIn;

    private String appId;

    private Integer mode;

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public Long getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Long bytesIn) {
        this.bytesIn = bytesIn;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String toString() {
        return "OrderDetailApplyClusterDTO{" +
                "idc='" + idc + '\'' +
                ", bytesIn=" + bytesIn +
                ", appId='" + appId + '\'' +
                ", mode=" + mode +
                '}';
    }
}