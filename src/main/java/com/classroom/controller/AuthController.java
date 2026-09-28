package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.config.UploadProperties;
import com.classroom.service.UserService;
import com.classroom.vo.LoginResultVO;
import com.classroom.vo.LoginVO;
import com.classroom.vo.RegisterVO;
import com.classroom.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 认证接口：注册 / 登录 / 退出 / 当前用户 / 头像
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final List<String> IMAGE_EXT = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    @Autowired
    private UserService userService;

    @Autowired
    private UploadProperties uploadProperties;

    /** 注册（成功后跳转登录页） */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterVO vo) {
        return Result.ok(userService.register(vo));
    }

    /** 登录：账号为邮箱或手机号，密码为 MD5 存储 */
    @PostMapping("/login")
    public Result<LoginResultVO> login(@RequestBody LoginVO vo, HttpSession session) {
        LoginResultVO result = userService.login(vo);
        session.setAttribute("loginUserId", result.getUser().getUid());
        session.setAttribute("loginUserName", result.getUser().getUserName());
        session.setAttribute("loginUserRoles", result.getRoles());
        session.setAttribute("loginRoleIds", result.getRoleIds());
        return Result.ok(result);
    }

    /** 退出登录 */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    /** 当前登录用户信息 */
    @GetMapping("/current")
    public Result<LoginResultVO> current(HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            return Result.error(com.classroom.common.ResultCode.UNAUTHORIZED, "未登录");
        }
        return Result.ok(userService.currentUser((Integer) uid));
    }

    /** 修改密码（校验原密码） */
    @PostMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            return Result.error(com.classroom.common.ResultCode.UNAUTHORIZED, "未登录");
        }
        userService.changePassword((Integer) uid, body.get("oldPassword"), body.get("newPassword"));
        return Result.ok();
    }

    /** 上传个人头像（登录用户）：保存图片并更新 users.Avatar，返回访问 URL */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        Object uid = session.getAttribute("loginUserId");
        if (uid == null) {
            return Result.error(com.classroom.common.ResultCode.UNAUTHORIZED, "未登录");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "未选择文件");
        }
        String original = file.getOriginalFilename();
        String ext = original == null ? "" : original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        if (!IMAGE_EXT.contains(ext)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的文件类型：" + ext);
        }
        File targetDir = new File(uploadProperties.getPath(), "avatar");
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new BusinessException(ResultCode.ERROR, "上传目录创建失败");
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        File target = new File(targetDir, filename);
        try {
            file.transferTo(target.toPath());
        } catch (Exception e) {
            throw new BusinessException(ResultCode.ERROR, "文件保存失败：" + e.getMessage());
        }
        String url = "/upload/avatar/" + filename;
        userService.updateAvatar((Integer) uid, url);
        return Result.ok(url);
    }
}
