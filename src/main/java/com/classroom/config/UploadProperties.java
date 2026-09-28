package com.classroom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 上传目录配置（读取 jdbc.properties 的 upload.path）
 */
@Component
public class UploadProperties {

    @Value("${upload.path}")
    private String path;

    public String getPath() {
        return path;
    }
}
