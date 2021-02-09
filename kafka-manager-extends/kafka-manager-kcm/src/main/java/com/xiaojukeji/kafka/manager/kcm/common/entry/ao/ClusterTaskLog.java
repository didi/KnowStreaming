package com.xiaojukeji.kafka.manager.kcm.common.entry.ao;

public class ClusterTaskLog {
    private String stdout;

    public ClusterTaskLog(String stdout) {
        this.stdout = stdout;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    @Override
    public String toString() {
        return "AgentOperationTaskLog{" +
                "stdout='" + stdout + '\'' +
                '}';
    }
}
