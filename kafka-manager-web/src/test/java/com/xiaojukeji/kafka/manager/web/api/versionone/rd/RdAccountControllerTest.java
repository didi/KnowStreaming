package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.AccountDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/2/22
 */
public class RdAccountControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        createAccountSuccess();
    }

    @AfterClass
    public void destroy() {
        deleteAccount();
    }

    private AccountDTO getAccountDTO() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setRole(Integer.parseInt(configMap.get(ConfigConstant.ACCOUNT_ROLE)));
        accountDTO.setUsername(configMap.get(ConfigConstant.ACCOUNT_USERNAME));
        accountDTO.setPassword(configMap.get(ConfigConstant.ACCOUNT_PASSWORD));
        return accountDTO;
    }

    @Test(description = "测试创建账号")
    public void createAccountTest() {

    }

    private void createAccountSuccess() {
        String url = baseUrl + "/api/v1/rd/accounts";
        AccountDTO accountDTO = getAccountDTO();
        HttpEntity<AccountDTO> httpEntity = new HttpEntity<>(accountDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取账号列表")
    public void listAccountsTest() {
        String url = baseUrl + "/api/v1/rd/accounts";
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试删除账号")
    public void deleteAccountTest() {
        Map<String, String> mp = new HashMap<>();
        mp.put(null, null);
    }

    private void deleteAccount() {
        String userName = configMap.get(ConfigConstant.ACCOUNT_USERNAME);
        String url = baseUrl + "/api/v1/rd/accounts?username=" + userName;
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试修改账号")
    public void modifyAccountTest() {
        String url = baseUrl + "/api/v1/rd/accounts";
        AccountDTO accountDTO = getAccountDTO();
        HttpEntity<AccountDTO> httpEntity = new HttpEntity<>(accountDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

}
