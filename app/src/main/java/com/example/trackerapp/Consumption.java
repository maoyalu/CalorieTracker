package com.example.trackerapp;

import java.util.Date;

public class Consumption {
    private Integer id;
    private Date date;
    private Integer quantity;
    private Food foodId;
    private Users userId;

    public Consumption(Integer id, Date date, Integer qty, Food food, Users user){
        this.id = id;
        this.date = date;
        quantity = qty;
        foodId = food;
        userId = user;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer qty){
        quantity = qty;
    }

    public Food getFoodId(){
        return foodId;
    }

    public void setFoodId(Food food){
        foodId = food;
    }

    public Users getUserId(){
        return userId;
    }

    public void setUserId(Users user){
        userId = user;
    }
}
