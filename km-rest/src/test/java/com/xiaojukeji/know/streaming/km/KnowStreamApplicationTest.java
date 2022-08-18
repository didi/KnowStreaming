package com.xiaojukeji.know.streaming.km;

import com.xiaojukeji.know.streaming.km.rest.KnowStreaming;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author d06679
 * @date 2019/4/11
 *
 * 得使用随机端口号，这样行执行单元测试的时候，不会出现端口号占用的情况
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = KnowStreaming.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KnowStreamApplicationTest {

    protected HttpHeaders headers;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    public void setUp() {
        // 获取 springboot server 监听的端口号
        // port = applicationContext.getWebServer().getPort();
        System.out.println( String.format("port is : [%d]", port));
//
//        headers = new HttpHeaders();
//        headers.add("X-SSO-USER", "zengqiao");
    }

    @Test
    public void test() {
        Assertions.assertNotNull(port);
    }
}