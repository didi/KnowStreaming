package com.xiaojukeji.know.streaming.km;

import com.xiaojukeji.know.streaming.km.rest.KnowStreaming;
import com.xiaojukeji.know.streaming.test.KMTestEnvService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author d06679
 * @date 2019/4/11
 * <p>
 * 得使用随机端口号，这样行执行单元测试的时候，不会出现端口号占用的情况
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = KnowStreaming.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KnowStreamApplicationTest extends KMTestEnvService {
    @LocalServerPort
    private Integer port;

//    @Test
//    public void test() {
//        Assertions.assertNotNull(port);
//    }
}