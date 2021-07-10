package com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public class N9eTaskStdoutLog {
    private String host;

    private String stdout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    @Override
    public String toString() {
        return "N9eTaskStdoutDTO{" +
                "host='" + host + '\'' +
                ", stdout='" + stdout + '\'' +
                '}';
    }
}