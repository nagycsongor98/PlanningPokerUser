package com.nagycsongor.planningpokeruser;

public class Group {
    private String key ;
    private String admin;

    public Group() {
    }

    public Group(String key, String admin) {
        this.key = key;
        this.admin = admin;
    }

    public String getKey() {
        return key;
    }

    public String getAdmin() {
        return admin;
    }
}
