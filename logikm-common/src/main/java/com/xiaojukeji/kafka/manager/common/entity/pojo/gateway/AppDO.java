package com.xiaojukeji.kafka.manager.common.entity.pojo.gateway;

import java.util.Date;
import java.util.Random;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class AppDO {
    private static final String ALPHA_NUM = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";

    private Long id;

    private String appId;

    private String name;

    private String password;

    private Integer type;

    private String applicant;

    private String principals;

    private String description;

    private Date createTime;

    private Date modifyTime;

    public static String getAlphaNum() {
        return ALPHA_NUM;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "AppDO{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", applicant='" + applicant + '\'' +
                ", principals='" + principals + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }

    public void generateAppIdAndPassword(Long orderId, String idc) {
        this.appId = AppDO.generateAppId(orderId, idc);

        StringBuffer stringBuffer = new StringBuffer(15);
        Random random = new Random();
        for(int i = 0; i < 12; i++) {
            int index = random.nextInt(ALPHA_NUM.length());
            stringBuffer.append(ALPHA_NUM.charAt(index));
        }
        this.password = stringBuffer.toString();
    }

    public static String generateAppId(Long orderId, String idc) {
        return String.format("appId_%06d_%s", orderId, idc);
    }
}