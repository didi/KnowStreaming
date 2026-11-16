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

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.vo.permission.PermissionTreeVO;
import com.didiglobal.logi.security.service.RoleService;
import com.didiglobal.logi.security.service.UserRoleService;
import com.didiglobal.logi.security.service.UserService;
import com.didiglobal.logi.security.util.HttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 服务端权限校验拦截器。
 *
 * KnowStreaming 的角色/权限模型原本只被前端 UI 用来决定显示哪些菜单和按钮，
 * 服务端唯一的 {@link PermissionInterceptor} 只校验了登录态，从不校验调用方
 * 的角色或权限点。任何已登录用户——哪怕只被授予了一个只读权限点——都可以直接
 * 调用身份管理接口（新增用户、创建角色、分配用户角色等）把自己提升为平台管理员。
 *
 * 本拦截器在 {@link PermissionInterceptor} 完成登录校验之后执行，对会改变
 * 平台状态的身份/配置/资源赋权类接口按"HTTP 方法 + URL 后缀 -> 权限点"的映射
 * 做服务端权限校验；权限点 id 与 km-persistence 中 dml-logi.sql 初始化的
 * logi_security_permission 表保持一致。只读接口不受影响。
 *
 * @see <a href="https://github.com/didi/KnowStreaming/issues/1263">issue #1263</a>
 */
@Component
public class IdentityAuthorizationInterceptor implements HandlerInterceptor {
    private static final ILog LOGGER = LogFactory.getLog(IdentityAuthorizationInterceptor.class);

    private static final String LOGI_API_PREFIX = "/logi-security/api/v1";

    private static final int PERMISSION_SYSTEM_MANAGEMENT = 1595;
    private static final int PERMISSION_USER_ADD         = 1629;
    private static final int PERMISSION_USER_EDIT        = 1631;
    private static final int PERMISSION_USER_DELETE      = 1635;
    private static final int PERMISSION_ROLE_CREATE      = 1637;
    private static final int PERMISSION_ROLE_UPDATE      = 1639;
    private static final int PERMISSION_ROLE_ASSIGN      = 1641;
    private static final int PERMISSION_ROLE_DELETE      = 1643;
    private static final int PERMISSION_CONFIG_ADD       = 1623;
    private static final int PERMISSION_CONFIG_EDIT      = 1625;
    private static final int PERMISSION_CONFIG_DELETE    = 1627;

    /**
     * 受保护接口的"HTTP 方法 + URL 后缀 -> 所需权限点"映射，按声明顺序匹配。
     */
    private static final List<ProtectedEndpoint> PROTECTED_ENDPOINTS = Arrays.asList(
            // 用户管理
            protectedEndpoint(HttpMethod.PUT,    "/user/add", PERMISSION_USER_ADD),
            protectedEndpoint(HttpMethod.POST,   "/user/edit", PERMISSION_USER_EDIT),
            protectedEndpoint(HttpMethod.DELETE, "/user/.*", PERMISSION_USER_DELETE),
            // 角色管理（更具体的后缀必须放在前缀规则之前）
            protectedEndpoint(HttpMethod.PUT,    "/role", PERMISSION_ROLE_UPDATE),
            protectedEndpoint(HttpMethod.POST,   "/role/assign", PERMISSION_ROLE_ASSIGN),
            protectedEndpoint(HttpMethod.POST,   "/role", PERMISSION_ROLE_CREATE),
            protectedEndpoint(HttpMethod.DELETE, "/role/[^/]+/user/[^/]+", PERMISSION_ROLE_ASSIGN),
            protectedEndpoint(HttpMethod.DELETE, "/role/.*", PERMISSION_ROLE_DELETE),
            // 配置管理
            protectedEndpoint(HttpMethod.PUT,    "/config/add", PERMISSION_CONFIG_ADD),
            protectedEndpoint(HttpMethod.POST,   "/config/edit", PERMISSION_CONFIG_EDIT),
            protectedEndpoint(HttpMethod.POST,   "/config/switch", PERMISSION_CONFIG_EDIT),
            protectedEndpoint(HttpMethod.DELETE, "/config/del", PERMISSION_CONFIG_DELETE),
            // 部门导入
            protectedEndpoint(HttpMethod.POST,   "/dept/import", PERMISSION_USER_ADD),
            // 资源赋权：把自己授权给自己是另一条提权路径，仅系统管理员可用
            protectedEndpoint(HttpMethod.PUT,    "/resource/vpc/switch", PERMISSION_SYSTEM_MANAGEMENT),
            protectedEndpoint(HttpMethod.POST,   "/resource/permission/.*", PERMISSION_SYSTEM_MANAGEMENT),
            protectedEndpoint(HttpMethod.POST,   "/resource/type/import", PERMISSION_SYSTEM_MANAGEMENT)
    );

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (HttpMethod.GET.matches(request.getMethod()) || HttpMethod.HEAD.matches(request.getMethod())) {
            // 只读接口保持原来的登录即可访问的行为，避免影响页面的正常浏览
            return true;
        }

        final Integer requiredPermissionId = resolveRequiredPermission(request);
        if (requiredPermissionId == null) {
            return true;
        }

        final Set<Integer> callerPermissionIds = collectCallerPermissionIds(getOperator(request));
        if (callerPermissionIds.contains(requiredPermissionId)) {
            return true;
        }

        LOGGER.warn(
                "method=preHandle||uri={}||msg=caller has no required permission, requiredPermissionId={}",
                request.getRequestURI(), requiredPermissionId);
        deny(response);
        return false;
    }

    /**************************************************** private method ****************************************************/

    private static ProtectedEndpoint protectedEndpoint(HttpMethod method, String urlSuffix, int permissionId) {
        return new ProtectedEndpoint(method, Pattern.compile("^" + urlSuffix + "$"), permissionId);
    }

    private Integer resolveRequiredPermission(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.startsWith(LOGI_API_PREFIX)) {
            return null;
        }
        String suffix = uri.substring(LOGI_API_PREFIX.length());
        for (ProtectedEndpoint endpoint : PROTECTED_ENDPOINTS) {
            if (endpoint.method.matches(request.getMethod()) && endpoint.urlSuffixPattern.matcher(suffix).matches()) {
                return endpoint.permissionId;
            }
        }
        return null;
    }

    /**
     * 查询当前登录用户，独立成方法以便单元测试覆写。
     */
    protected String getOperator(HttpServletRequest request) {
        return HttpRequestUtil.getOperator(request);
    }

    private Set<Integer> collectCallerPermissionIds(String operator) {
        final Set<Integer> permissionIds = new HashSet<>();
        if (operator == null) {
            return permissionIds;
        }
        final com.didiglobal.logi.security.common.entity.user.User user = userService.getUserByUserName(operator);
        if (user == null) {
            return permissionIds;
        }
        final List<Integer> roleIds = userRoleService.getRoleIdListByUserId(user.getId());
        if (roleIds == null) {
            return permissionIds;
        }
        for (Integer roleId : roleIds) {
            if (roleId == null) {
                continue;
            }
            final com.didiglobal.logi.security.common.vo.role.RoleVO role =
                    roleService.getRoleDetailByRoleId(roleId);
            if (role == null) {
                continue;
            }
            collectPermissionIds(role.getPermissionTreeVO(), permissionIds);
        }
        return permissionIds;
    }

    private void collectPermissionIds(PermissionTreeVO node, Set<Integer> permissionIds) {
        if (node == null || node.getId() == null) {
            return;
        }
        permissionIds.add(node.getId());
        if (node.getChildList() != null) {
            for (PermissionTreeVO child : node.getChildList()) {
                collectPermissionIds(child, permissionIds);
            }
        }
    }

    private void deny(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        final String body = "{\"code\":" + HttpServletResponse.SC_FORBIDDEN
                + ",\"message\":\"no permission to perform this operation\",\"data\":null,\"success\":false}";
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

    private static final class ProtectedEndpoint {
        private final HttpMethod method;
        private final Pattern urlSuffixPattern;
        private final int permissionId;

        private ProtectedEndpoint(HttpMethod method, Pattern urlSuffixPattern, int permissionId) {
            this.method = method;
            this.urlSuffixPattern = urlSuffixPattern;
            this.permissionId = permissionId;
        }
    }
}
