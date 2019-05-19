package com.example.trackerapp;

import java.util.Date;

public class Report {
    private Integer id;
    private Date reportDate;
    private double totalCaloriesConsumed;
    private double totalCaloriesBurned;
    private int totalStepsTaken;
    private double calorieGoal;
    private Users userId;

    public Report(Integer id, double consumed, double burned, int steps, double goal, Users usr){
        this.id = id;
        reportDate = new Date();
        totalCaloriesConsumed = consumed;
        totalCaloriesBurned = burned;
        totalStepsTaken = steps;
        calorieGoal = goal;
        userId = usr;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Date getReportDate(){
        return reportDate;
    }

    public void setReportDate(Date date){
        this.reportDate = date;
    }

    public double getTotalCaloriesConsumed(){
        return totalCaloriesConsumed;
    }

    public void setTotalCaloriesConsumed(double consumed){
        totalCaloriesConsumed = consumed;
    }

    public double getTotalCaloriesBurned(){
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double burned){
        totalCaloriesBurned = burned;
    }

    public int getTotalStepsTaken(){
        return totalStepsTaken;
    }

    public void setTotalStepsTaken(int steps){
        totalStepsTaken = steps;
    }

    public double getCalorieGoal(){
        return calorieGoal;
    }

    public void setCalorieGoal(double goal){
        calorieGoal = goal;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users usr){
        userId = usr;
    }
}
