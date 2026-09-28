package com.classroom.interceptor;

import com.classroom.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 后台接口权限拦截器：
 * - 未登录 → 401
 * - 学员（无管理员/老师角色）→ 403
 * - 老师：可访问教学类接口，但权限管理类接口（角色/功能/用户角色分配）仅管理员可用
 */
public class AdminPermissionInterceptor implements HandlerInterceptor {

    /** 系统管理员角色 ID */
    private static final int ROLE_ADMIN = 1;
    /** 老师角色 ID */
    private static final int ROLE_TEACHER = 2;

    /** 仅管理员可访问的路径前缀 */
    private static final String[] ADMIN_ONLY_PATHS = {
            "/api/admin/roles",
            "/api/admin/nodes",
            "/api/admin/rolenodes"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Object uid = session == null ? null : session.getAttribute("loginUserId");
        if (uid == null) {
            response.sendError(401, "未登录");
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Integer> roleIds = session == null ? null : (List<Integer>) session.getAttribute("loginRoleIds");
        boolean isAdmin = contains(roleIds, ROLE_ADMIN);
        boolean isTeacher = contains(roleIds, ROLE_TEACHER);
        if (!isAdmin && !isTeacher) {
            response.sendError(403, "无权访问后台");
            return false;
        }
        if (!isAdmin && matchAdminOnly(request.getRequestURI())) {
            response.sendError(403, "该操作仅限系统管理员");
            return false;
        }
        return true;
    }

    private boolean contains(List<Integer> roleIds, int roleId) {
        if (roleIds == null) {
            return false;
        }
        for (Integer id : roleIds) {
            if (id != null && id == roleId) {
                return true;
            }
        }
        return false;
    }

    private boolean matchAdminOnly(String uri) {
        for (String prefix : ADMIN_ONLY_PATHS) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        // 用户管理写操作：角色分配 / 禁用启用 / 重置密码，仅管理员
        if (uri.matches(".*/api/admin/users/\\d+/(roles|status|password)")) {
            return true;
        }
        return false;
    }
}
