package com.classroom.vo;

/**
 * 登录请求体（账号 = 邮箱或手机号）
 */
public class LoginVO {

    private String account;
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
