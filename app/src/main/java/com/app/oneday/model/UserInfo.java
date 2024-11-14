package com.app.oneday.model;

public class UserInfo {
    private String email;
    private String name;
    private String status;

    public UserInfo() {}

    // 생성자
    public UserInfo(String email, String name, String status) {
        this.email = email;
        this.name = name;
        this.status = status;

    }

    // Getter 및 Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
