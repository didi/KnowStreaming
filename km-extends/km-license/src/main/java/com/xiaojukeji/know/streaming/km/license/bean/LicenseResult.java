package com.xiaojukeji.know.streaming.km.license.bean;

import lombok.Data;

/**
 * @author didi
 */
@Data
public class LicenseResult<T> {
    String err;
    T reply;
}
