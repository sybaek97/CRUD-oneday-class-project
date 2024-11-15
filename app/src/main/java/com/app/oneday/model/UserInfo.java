package com.app.oneday.model;

public class UserInfo {
    private static UserInfo instance;

    private String email;
    private String name;
    private String status;

    private UserInfo() {}
    public static synchronized UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }
        return instance;
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
