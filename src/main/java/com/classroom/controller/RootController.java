package com.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 根路径默认页：访问 http://localhost:8080/ 显示服务说明，避免 404/500
 */
@Controller
public class RootController {

    @GetMapping(value = "/", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String index() {
        return "<!DOCTYPE html>"
                + "<html lang=\"zh\">"
                + "<head><meta charset=\"utf-8\"><title>酷云课堂 · 后端服务</title></head>"
                + "<body style=\"font-family:'Microsoft YaHei',Arial,sans-serif;background:#f5f7fa;margin:0;display:flex;align-items:center;justify-content:center;min-height:100vh;\">"
                + "<div style=\"background:#fff;border-radius:10px;padding:40px 56px;box-shadow:0 4px 16px rgba(0,0,0,.08);text-align:center;max-width:520px;\">"
                + "<h1 style=\"margin:0 0 8px;color:#1f2d3d;\">酷云课堂 · 后端服务已启动</h1>"
                + "<p style=\"color:#606266;font-size:14px;line-height:1.9;\">接口根路径：<code>/api</code><br>"
                + "前端地址：<code>http://localhost:5173/</code>（先运行 <code>npm run dev</code>）<br>"
                + "接口测试：<code>POST /api/auth/login</code>、<code>GET /api/home</code></p>"
                + "<p style=\"color:#909399;font-size:12px;\">未匹配的路径将返回 JSON 格式的 404 提示</p>"
                + "</div></body></html>";
    }
}
