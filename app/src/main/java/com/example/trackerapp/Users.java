package com.example.trackerapp;

import java.util.Date;

public class Users {
    private Integer userId;
    private String name;
    private String surname;
    private String email;
    private Date dob;
    private double height;
    private double weight;
    private Character gender;
    private String address;
    private String postcode;
    private int levelOfActivity;
    private int stepsPerMile;

    public Users(Integer userId, String name, String surname, String email, Date dob, double height, double weight,
                Character gender, String address, String postcode, int levelOfActivity, int stepsPerMile){
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.address = address;
        this.postcode = postcode;
        this.levelOfActivity = levelOfActivity;
        this.stepsPerMile = stepsPerMile;
    }

    public Users(){}

    public Integer getUserId(){
        return userId;
    }

    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSurname(){
        return surname;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public Date getDob(){
        return dob;
    }

    public void setDob(Date dob){
        this.dob = dob;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height){
        this.height = height;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public Character getGender(){
        return gender;
    }

    public void setGender(Character gender){
        this.gender = gender;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getPostcode(){
        return postcode;
    }

    public void setPostcode(String postcode){
        this.postcode = postcode;
    }

    public int getLevelOfActivity(){
        return levelOfActivity;
    }

    public void setLevelOfActivity(int levelOfActivity){
        this.levelOfActivity = levelOfActivity;
    }

    public int getStepsPerMile(){
        return stepsPerMile;
    }

    public void setStepsPerMile(int stepsPerMile){
        this.stepsPerMile = stepsPerMile;
    }
}
