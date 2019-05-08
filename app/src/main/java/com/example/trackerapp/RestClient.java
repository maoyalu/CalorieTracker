package com.example.trackerapp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class RestClient {
    final static String BASE_URL = "http://192.168.0.172:8080/CalorieTracker/webresources/";


    public static String AuthenticateUser(String username, String password){
        final String methodPath = "calorie.credential/findByUsernameAndPassword/" + username + "/" + password;
        final String type = "application/json";
        return getRestMethod(BASE_URL + methodPath, type);
    }


    public static String findFoodByCategory(String category){
        final String methodPath = "calorie.food/findByCategory/" + category;
        final String type = "application/json";
        return getRestMethod(BASE_URL + methodPath, type);
    }

    public static Integer getNextUserId(){
        final String methodPath = "calorie.users/count";
        final String type = "text/plain";
        return Integer.parseInt(getRestMethod(BASE_URL + methodPath, type)) + 1;
    }

    public static boolean isUsernameExist(String username){
        final String methodPath = "calorie.credential/isUsernameExist/" + username;
        final String type = "text/plain";
        return getRestMethod(BASE_URL + methodPath, type).equals("1");
    }

    public static void createUser(Users user){
        final String methodPath = "calorie.users/";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        String stringUserJson = gson.toJson(user);
        postRestMethod(BASE_URL + methodPath, stringUserJson);
    }


//    public static void createCredential(Credential c){
//        final String methodPath = "calorie.users/";
//        Gson gson = new Gson();
//        String stringCredentialJson = gson.toJson(c);
//        postRestMethod(BASE_URL + methodPath, stringCredentialJson);
//    }


    private static void postRestMethod(String urlStr, String jsonStr){
        URL url = null;
        HttpURLConnection conn = null;
        try{
            url = new URL(urlStr);

            conn = (HttpURLConnection)url.openConnection();

            conn.setReadTimeout(100000);
            conn.setConnectTimeout(15000);

            conn.setRequestMethod("POST");

            conn.setDoOutput(true);

            conn.setFixedLengthStreamingMode(jsonStr.getBytes().length);

            conn.setRequestProperty("Content-Type", "application/json");

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(jsonStr);
            out.close();
            Log.i("error", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }


    private static String getRestMethod(String urlStr, String type){
        URL url = null;
        HttpURLConnection conn = null;
        StringBuilder textResult = new StringBuilder();

        try{
            url = new URL(urlStr);

            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type", type);
            conn.setRequestProperty("Accept", type);

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
