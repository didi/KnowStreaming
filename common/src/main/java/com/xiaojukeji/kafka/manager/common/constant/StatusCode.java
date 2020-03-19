package com.xiaojukeji.kafka.manager.common.constant;

public class StatusCode {
    /*
     * kafka-manager status code: 17000 ~ 17999
     *
     *  正常 - 0
     * 参数错误 - 10000
     * 资源未就绪 - 10001
     */

    /*
     * 已约定的状态码
     */
    public static final Integer SUCCESS = 0;
    public static final Integer PARAM_ERROR = 10000; //参数错误
    public static final Integer RES_UNREADY = 10001; //资源未就绪

    public static final Integer MY_SQL_SELECT_ERROR = 17210; // MySQL 查询数据异常
    public static final Integer MY_SQL_INSERT_ERROR = 17211; // MySQL 插入数据异常
    public static final Integer MY_SQL_DELETE_ERROR = 17212; // MySQL 删除数据异常
    public static final Integer MY_SQL_UPDATE_ERROR = 17213; // MySQL 更新数据异常
    public static final Integer MY_SQL_REPLACE_ERROR = 17214; // MySQL 替换数据异常

    public static final Integer OPERATION_ERROR = 17300; // 请求操作异常


    /**
     * Topic相关的异常
     */
    public static final Integer TOPIC_EXISTED = 17400; //Topic已经存在了

    public static final Integer PARTIAL_SUCESS = 17700; //操作部分成功

}
