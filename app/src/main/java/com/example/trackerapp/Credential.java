package com.example.trackerapp;

import java.util.Date;

public class Credential {
    private Integer id;
    private String username;
    private String passwordHash;
    private Date signupDate;
    private Users userId;

    public Credential(Integer id, String username, String passwordHash, Users userId){
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        signupDate = new Date();
        this.userId = userId;
    }

    public Credential(){}

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

    public Date getSignupDate(){
        return signupDate;
    }

    public void setSignupDate(Date signupDate){
        this.signupDate = signupDate;
    }

    public Users getUserId(){
        return userId;
    }

    public void setUserId(Users userId){
        this.userId = userId;
    }
}
