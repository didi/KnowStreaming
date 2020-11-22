package com.xiaojukeji.kafka.manager.account.common.entry;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public class N9eUserData {
    private Long id;

    private String uuid;

    private String username;

    private String dispname;

    private String phone;

    private String email;

    private String im;

    private String portrait;

    private Integer is_root;

    private Integer leader_id;

    private String leader_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDispname() {
        return dispname;
    }

    public void setDispname(String dispname) {
        this.dispname = dispname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public Integer getIs_root() {
        return is_root;
    }

    public void setIs_root(Integer is_root) {
        this.is_root = is_root;
    }

    public Integer getLeader_id() {
        return leader_id;
    }

    public void setLeader_id(Integer leader_id) {
        this.leader_id = leader_id;
    }

    public String getLeader_name() {
        return leader_name;
    }

    public void setLeader_name(String leader_name) {
        this.leader_name = leader_name;
    }

    @Override
    public String toString() {
        return "EPRIResult{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", dispname='" + dispname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", im='" + im + '\'' +
                ", portrait='" + portrait + '\'' +
                ", is_root=" + is_root +
                ", leader_id=" + leader_id +
                ", leader_name='" + leader_name + '\'' +
                '}';
    }
}