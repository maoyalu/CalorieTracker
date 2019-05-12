package com.example.trackerapp;

public class Food {

    private Integer foodId;
    private String foodName;
    private String category;
    private double calorieAmount;
    private String servingUnit;
    private double servingAmount;
    private double fat;

    public Food(Integer id, String name, String category, double calorie, String unit, double serving, double fat){
        foodId = id;
        foodName = name;
        this.category = category;
        calorieAmount = calorie;
        servingUnit = unit;
        servingAmount = serving;
        this.fat = fat;
    }

    public Integer getFoodId(){
        return foodId;
    }

    public void setFoodId(Integer id){
        foodId = id;
    }

    public String getFoodName(){
        return foodName;
    }

    public void setFoodName(String name){
        foodName = name;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public double getCalorieAmount(){
        return calorieAmount;
    }

    public void setCalorieAmount(double calorieAmount){
        this.calorieAmount = calorieAmount;
    }

    public String getServingUnit(){
        return servingUnit;
    }

    public void setServingUnit(String unit){
        servingUnit = unit;
    }

    public double getServingAmount(){
        return servingAmount;
    }

    public void setServingAmount(double amount){
        servingAmount = amount;
    }

    public double getFat(){
        return fat;
    }

    public void setFat(double fat){
        this.fat = fat;
    }

}
