package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/7
 */
@ApiModel(value = "Topic请求耗时信息")
public class TopicRequestTimeVO {
    @ApiModelProperty(value = "produce请求平均耗时")
    private Object produceRequestTimeMean;

    @ApiModelProperty(value = "produce请求50分位耗时")
    private Object produceRequestTime50thPercentile;

    @ApiModelProperty(value = "produce请求75分位耗时")
    private Object produceRequestTime75thPercentile;

    @ApiModelProperty(value = "produce请求95分位耗时")
    private Object produceRequestTime95thPercentile;

    @ApiModelProperty(value = "produce请求99分位耗时")
    private Object produceRequestTime99thPercentile;

    @ApiModelProperty(value = "fetch请求平均耗时")
    private Object fetchRequestTimeMean;

    @ApiModelProperty(value = "fetch请求50分位耗时")
    private Object fetchRequestTime50thPercentile;

    @ApiModelProperty(value = "fetch请求75分位耗时")
    private Object fetchRequestTime75thPercentile;

    @ApiModelProperty(value = "fetch请求95分位耗时")
    private Object fetchRequestTime95thPercentile;

    @ApiModelProperty(value = "fetch请求99分位耗时")
    private Object fetchRequestTime99thPercentile;

    @ApiModelProperty(value = "创建时间")
    private Object gmtCreate;

    public Object getProduceRequestTimeMean() {
        return produceRequestTimeMean;
    }

    public void setProduceRequestTimeMean(Object produceRequestTimeMean) {
        this.produceRequestTimeMean = produceRequestTimeMean;
    }

    public Object getProduceRequestTime50thPercentile() {
        return produceRequestTime50thPercentile;
    }

    public void setProduceRequestTime50thPercentile(Object produceRequestTime50thPercentile) {
        this.produceRequestTime50thPercentile = produceRequestTime50thPercentile;
    }

    public Object getProduceRequestTime75thPercentile() {
        return produceRequestTime75thPercentile;
    }

    public void setProduceRequestTime75thPercentile(Object produceRequestTime75thPercentile) {
        this.produceRequestTime75thPercentile = produceRequestTime75thPercentile;
    }

    public Object getProduceRequestTime95thPercentile() {
        return produceRequestTime95thPercentile;
    }

    public void setProduceRequestTime95thPercentile(Object produceRequestTime95thPercentile) {
        this.produceRequestTime95thPercentile = produceRequestTime95thPercentile;
    }

    public Object getProduceRequestTime99thPercentile() {
        return produceRequestTime99thPercentile;
    }

    public void setProduceRequestTime99thPercentile(Object produceRequestTime99thPercentile) {
        this.produceRequestTime99thPercentile = produceRequestTime99thPercentile;
    }

    public Object getFetchRequestTimeMean() {
        return fetchRequestTimeMean;
    }

    public void setFetchRequestTimeMean(Object fetchRequestTimeMean) {
        this.fetchRequestTimeMean = fetchRequestTimeMean;
    }

    public Object getFetchRequestTime50thPercentile() {
        return fetchRequestTime50thPercentile;
    }

    public void setFetchRequestTime50thPercentile(Object fetchRequestTime50thPercentile) {
        this.fetchRequestTime50thPercentile = fetchRequestTime50thPercentile;
    }

    public Object getFetchRequestTime75thPercentile() {
        return fetchRequestTime75thPercentile;
    }

    public void setFetchRequestTime75thPercentile(Object fetchRequestTime75thPercentile) {
        this.fetchRequestTime75thPercentile = fetchRequestTime75thPercentile;
    }

    public Object getFetchRequestTime95thPercentile() {
        return fetchRequestTime95thPercentile;
    }

    public void setFetchRequestTime95thPercentile(Object fetchRequestTime95thPercentile) {
        this.fetchRequestTime95thPercentile = fetchRequestTime95thPercentile;
    }

    public Object getFetchRequestTime99thPercentile() {
        return fetchRequestTime99thPercentile;
    }

    public void setFetchRequestTime99thPercentile(Object fetchRequestTime99thPercentile) {
        this.fetchRequestTime99thPercentile = fetchRequestTime99thPercentile;
    }

    public Object getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Object gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "TopicRequestTimeVO{" +
                "produceRequestTimeMean=" + produceRequestTimeMean +
                ", produceRequestTime50thPercentile=" + produceRequestTime50thPercentile +
                ", produceRequestTime75thPercentile=" + produceRequestTime75thPercentile +
                ", produceRequestTime95thPercentile=" + produceRequestTime95thPercentile +
                ", produceRequestTime99thPercentile=" + produceRequestTime99thPercentile +
                ", fetchRequestTimeMean=" + fetchRequestTimeMean +
                ", fetchRequestTime50thPercentile=" + fetchRequestTime50thPercentile +
                ", fetchRequestTime75thPercentile=" + fetchRequestTime75thPercentile +
                ", fetchRequestTime95thPercentile=" + fetchRequestTime95thPercentile +
                ", fetchRequestTime99thPercentile=" + fetchRequestTime99thPercentile +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
