package com.classroom.controller;

import com.classroom.common.BusinessException;
import com.classroom.common.Result;
import com.classroom.common.ResultCode;
import com.classroom.config.UploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传接口（管理员/老师）：
 * 视频上传（章节视频）、图片上传（课程封面）
 */
@RestController
@RequestMapping("/api/admin/upload")
public class UploadAdminController {

    private static final List<String> VIDEO_EXT = Arrays.asList("mp4", "webm", "ogg", "mov");
    private static final List<String> IMAGE_EXT = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> FILE_EXT = Arrays.asList(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "zip", "rar", "md", "csv");

    @Autowired
    private UploadProperties uploadProperties;

    /** 上传章节视频 */
    @PostMapping("/video")
    public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        return Result.ok(save(file, "video", VIDEO_EXT));
    }

    /** 上传课程封面图片 */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.ok(save(file, "image", IMAGE_EXT));
    }

    /** 上传章节课件文件（pdf/office/文本/压缩包等） */
    @PostMapping("/file")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return Result.ok(save(file, "file", FILE_EXT));
    }

    private String save(MultipartFile file, String dir, List<String> allowedExt) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "未选择文件");
        }
        String original = file.getOriginalFilename();
        String ext = original == null ? "" : original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        if (!allowedExt.contains(ext)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的文件类型：" + ext);
        }
        String baseDir = uploadProperties.getPath();
        File targetDir = new File(baseDir, dir);
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
        return "/upload/" + dir + "/" + filename;
    }
}
