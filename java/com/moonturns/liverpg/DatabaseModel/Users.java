package com.moonturns.liverpg.DatabaseModel;

//User informations
public class Users {

    private String username;
    private String user_email;
    private String user_password;
    private String user_id;
    private UserLevel userLevel;
    private UserPlans userPlans;

    public Users() {
    }

    public Users(String username, String user_email, String user_password, String user_id, UserLevel userLevel, UserPlans userPlans) {
        this.username = username;
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_id = user_id;
        this.userLevel = userLevel;
        this.userPlans = userPlans;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public UserPlans getUserPlans() {
        return userPlans;
    }

    public void setUserPlans(UserPlans userPlans) {
        this.userPlans = userPlans;
    }
}
