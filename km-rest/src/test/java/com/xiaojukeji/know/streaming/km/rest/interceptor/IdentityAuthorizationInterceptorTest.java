/*
 * Copyright contributors to KnowStreaming.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaojukeji.know.streaming.km.rest.interceptor;

import com.didiglobal.logi.security.common.entity.user.User;
import com.didiglobal.logi.security.common.vo.permission.PermissionTreeVO;
import com.didiglobal.logi.security.common.vo.role.RoleVO;
import com.didiglobal.logi.security.service.RoleService;
import com.didiglobal.logi.security.service.UserRoleService;
import com.didiglobal.logi.security.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 服务端权限校验拦截器单元测试，对应 issue #1263
 * （身份管理接口缺少服务端鉴权导致的自我提权）。
 */
public class IdentityAuthorizationInterceptorTest {
    private static final int PERMISSION_SYSTEM_MANAGEMENT = 1595;
    private static final int PERMISSION_USER_ADD = 1629;
    private static final int PERMISSION_ROLE_ASSIGN = 1641;
    private static final String CALLER = "alice";

    private UserService userService;
    private UserRoleService userRoleService;
    private RoleService roleService;
    private IdentityAuthorizationInterceptor interceptor;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userRoleService = mock(UserRoleService.class);
        roleService = mock(RoleService.class);
        interceptor = newInterceptor(CALLER);
        ReflectionTestUtils.setField(interceptor, "userService", userService);
        ReflectionTestUtils.setField(interceptor, "userRoleService", userRoleService);
        ReflectionTestUtils.setField(interceptor, "roleService", roleService);
    }

    private IdentityAuthorizationInterceptor newInterceptor(final String operator) {
        return new IdentityAuthorizationInterceptor() {
            @Override
            protected String getOperator(HttpServletRequest request) {
                return operator;
            }
        };
    }

    private void mockCaller(int userId, List<Integer> roleIds, List<Integer> permissionIds) {
        final User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(userService.getUserByUserName(CALLER)).thenReturn(user);
        when(userRoleService.getRoleIdListByUserId(userId)).thenReturn(roleIds);
        final PermissionTreeVO permissionTree = buildPermissionTree(permissionIds);
        for (Integer roleId : roleIds) {
            final RoleVO role = mock(RoleVO.class);
            when(role.getPermissionTreeVO()).thenReturn(permissionTree);
            when(roleService.getRoleDetailByRoleId(roleId)).thenReturn(role);
        }
    }

    private PermissionTreeVO buildPermissionTree(List<Integer> permissionIds) {
        if (permissionIds.isEmpty()) {
            return null;
        }
        final PermissionTreeVO node = mock(PermissionTreeVO.class);
        when(node.getId()).thenReturn(permissionIds.get(0));
        when(node.getChildList()).thenReturn(Collections.emptyList());
        return node;
    }

    private MockHttpServletRequest request(String method, String uri) {
        final MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.setRequestURI(uri);
        return request;
    }

    @Test
    public void shouldAllowUserManagementWriteForCallerWithPermission() throws Exception {
        mockCaller(7, Arrays.asList(1), Arrays.asList(PERMISSION_USER_ADD));

        final MockHttpServletRequest request =
                request("PUT", "/logi-security/api/v1/user/add");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertTrue(interceptor.preHandle(request, response, new Object()));
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldDenyUserManagementWriteForCallerWithoutPermission() throws Exception {
        // 仅授予一个与本次操作无关的只读权限点，正是 issue #1263 中的提权场景
        mockCaller(7, Arrays.asList(1), Arrays.asList(3000));

        final MockHttpServletRequest request =
                request("PUT", "/logi-security/api/v1/user/add");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertFalse(interceptor.preHandle(request, response, new Object()));
        Assertions.assertEquals(403, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("no permission"));
    }

    @Test
    public void shouldDenyRoleAssignForCallerWithoutAssignPermission() throws Exception {
        mockCaller(7, Arrays.asList(1), Arrays.asList(PERMISSION_USER_ADD));

        final MockHttpServletRequest request =
                request("POST", "/logi-security/api/v1/role/assign");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertFalse(interceptor.preHandle(request, response, new Object()));
        Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    public void shouldAllowRoleAssignForCallerWithAssignPermission() throws Exception {
        mockCaller(7, Arrays.asList(1), Arrays.asList(PERMISSION_ROLE_ASSIGN));

        final MockHttpServletRequest request =
                request("POST", "/logi-security/api/v1/role/assign");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    public void shouldDenyUnknownUser() throws Exception {
        final IdentityAuthorizationInterceptor unknownCallerInterceptor = newInterceptor("ghost");
        ReflectionTestUtils.setField(unknownCallerInterceptor, "userService", userService);
        ReflectionTestUtils.setField(unknownCallerInterceptor, "userRoleService", userRoleService);
        ReflectionTestUtils.setField(unknownCallerInterceptor, "roleService", roleService);
        when(userService.getUserByUserName("ghost")).thenReturn(null);

        final MockHttpServletRequest request =
                request("DELETE", "/logi-security/api/v1/role/3");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertFalse(unknownCallerInterceptor.preHandle(request, response, new Object()));
        Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    public void shouldAllowReadEndpointsRegardlessOfPermissions() throws Exception {
        // 只读接口保持登录即可访问
        final MockHttpServletRequest request =
                request("GET", "/logi-security/api/v1/role/page");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    public void shouldAllowNonIdentityEndpoints() throws Exception {
        // KM 自身的 /api/v3/** 接口不在本拦截器的职责范围内
        final MockHttpServletRequest request =
                request("DELETE", "/api/v3/cluster/1");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    public void shouldAllowCallerWithSystemManagementPermissionForResourceAssign() throws Exception {
        mockCaller(7, Arrays.asList(1), Arrays.asList(PERMISSION_SYSTEM_MANAGEMENT));

        final MockHttpServletRequest request =
                request("POST", "/logi-security/api/v1/resource/permission/mbr/assign");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    public void shouldDenyResourceAssignForCallerWithoutSystemManagementPermission() throws Exception {
        mockCaller(7, Arrays.asList(1), Arrays.asList(PERMISSION_USER_ADD));

        final MockHttpServletRequest request =
                request("POST", "/logi-security/api/v1/resource/permission/mbr/assign");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertFalse(interceptor.preHandle(request, response, new Object()));
        Assertions.assertEquals(403, response.getStatus());
    }
}
