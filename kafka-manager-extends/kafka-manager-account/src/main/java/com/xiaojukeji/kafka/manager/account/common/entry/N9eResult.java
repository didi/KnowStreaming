package com.xiaojukeji.kafka.manager.account.common.entry;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public class N9eResult<T> {
    private T dat;

    private String err;

    public T getDat() {
        return dat;
    }

    public void setDat(T dat) {
        this.dat = dat;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    @Override
    public String toString() {
        return "N9eResult{" +
                "dat=" + dat +
                ", err='" + err + '\'' +
                '}';
    }
}