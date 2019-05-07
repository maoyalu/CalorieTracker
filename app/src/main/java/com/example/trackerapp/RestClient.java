package com.example.trackerapp;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RestClient {
    final static String BASE_URL = "http://192.168.0.172:8080/CalorieTracker/webresources/";


    public static String AuthenticateUser(String username, String password){
        final String methodPath = "calorie.credential/findByUsernameAndPassword/" + username + "/" + password;
        return getRestMethod(BASE_URL + methodPath);
    }


    public static String findFoodByCategory(String category){
        final String methodPath = "calorie.food/findByCategory/" + category;
        return getRestMethod(BASE_URL + methodPath);
    }

    public static Integer getNextUserId(){
        final String methodPath = "calorie.users/count";
        return Integer.parseInt(getRestMethod(BASE_URL + methodPath)) + 1;
    }

    public static void createUser(User user){
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath = "calorie.users/";
        try{
            Gson gson = new Gson();
            String stringUserJson = gson.toJson(user);
            url = new URL(BASE_URL + methodPath);

            conn = (HttpURLConnection)url.openConnection();

            conn.setReadTimeout(100000);
            conn.setConnectTimeout(15000);

            conn.setRequestMethod("POST");

            conn.setDoOutput(true);

            conn.setFixedLengthStreamingMode(stringUserJson.getBytes().length);

            conn.setRequestProperty("Content-Type", "application/json");

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringUserJson);
            out.close();
            Log.i("error", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }


    private static String getRestMethod(String urlStr){
        URL url = null;
        HttpURLConnection conn = null;
        StringBuilder textResult = new StringBuilder();

        try{
            url = new URL(urlStr);

            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            Scanner inStream = new Scanner(conn.getInputStream());

            while(inStream.hasNextLine()){

                textResult.append(inStream.nextLine());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        System.out.print(textResult.toString());

        return textResult.toString();
    }

}
