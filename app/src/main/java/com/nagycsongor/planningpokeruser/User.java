package com.nagycsongor.planningpokeruser;

public class User {
    public String username;
    public String id;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String id) {
        this.username = username;
        this.id = id;
    }
}
