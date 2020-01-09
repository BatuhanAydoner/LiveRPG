package com.moonturns.liverpg.DatabaseModel;

//users level and game informations
public class UserLevel {

    private String user_level;
    private String user_point;
    private String user_completed_xp;
    private String user_failed_xp;

    public UserLevel() {
    }

    public UserLevel(String user_level, String user_point, String user_completed_xp, String user_failed_xp) {
        this.user_level = user_level;
        this.user_point = user_point;
        this.user_completed_xp = user_completed_xp;
        this.user_failed_xp = user_failed_xp;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }

    public String getUser_point() {
        return user_point;
    }

    public void setUser_point(String user_point) {
        this.user_point = user_point;
    }

    public String getUser_completed_xp() {
        return user_completed_xp;
    }

    public void setUser_completed_xp(String user_completed_xp) {
        this.user_completed_xp = user_completed_xp;
    }

    public String getUser_failed_xp() {
        return user_failed_xp;
    }

    public void setUser_failed_xp(String user_failed_xp) {
        this.user_failed_xp = user_failed_xp;
    }
}
