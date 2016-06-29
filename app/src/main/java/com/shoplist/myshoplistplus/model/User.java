package com.shoplist.myshoplistplus.model;

import java.util.HashMap;

/**
 * Created by TSE on 24/06/2016.
 */
public class User {
    private String name;
    private String email;
    private HashMap<String, Object> timestampJoined;
    private boolean hasLoggedInWithPassword;

    public User() {
    }

    public User(String name, String email, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithPassword = false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public boolean isHasLoggedInWithPassword() {
        return hasLoggedInWithPassword;
    }
}
