package com.example.trackerapp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class RestClient {
    final static String BASE_URL = "http://192.168.0.32:8080/CalorieTracker/webresources/";


    // GET
    public static String AuthenticateUser(String username, String password){
        final String methodPath = "calorie.credential/findByUsernameAndPassword/" + username + "/" + password;
        final String type = "application/json";
        return getRestMethod(BASE_URL + methodPath, type);
    }

    // GET
    public static boolean isUsernameExist(String username){
        final String methodPath = "calorie.credential/isUsernameExist/" + username;
        final String type = "text/plain";
        return getRestMethod(BASE_URL + methodPath, type).equals("1");
    }

    public static Integer getNextFoodId(){
        final String methodPath = "calorie.food/count";
        final String type = "text/plain";
        return Integer.parseInt(getRestMethod(BASE_URL + methodPath, type)) + 1;
    }

    // GET
    public static String findFoodByCategory(String category){
        final String methodPath = "calorie.food/findByCategory/" + category;
        final String type = "application/json";
        return getRestMethod(BASE_URL + methodPath, type);
    }

    // GET
    public static Food findFoodByName(String name) {
        final String methodPath = "calorie.food/findByFoodName/" + name;
        final String type = "application/json";
        String foodJson = getRestMethod(BASE_URL + methodPath, type);
        try{
            JSONArray array = new JSONArray(foodJson);
            JSONObject obj = array.getJSONObject(0);
            Gson gson = new Gson();
            Food food = gson.fromJson(obj.toString(), Food.class);
            return food;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // GET
    public static Integer getNextUserId(){
        final String methodPath = "calorie.users/count";
        final String type = "text/plain";
        return Integer.parseInt(getRestMethod(BASE_URL + methodPath, type)) + 1;
    }

    // GET
    public static boolean isEmailExist(String email){
        final String methodPath = "calorie.users/findByEmail/" + email;
        final String type = "application/json";
        return !getRestMethod(BASE_URL + methodPath, type).equals("[]");
    }

    // GET
    public static Users findUserById(Integer userId){
        final String methodPath = "calorie.users/" + userId;
        final String type = "application/json";
        String userJson = getRestMethod(BASE_URL + methodPath, type);
        try{
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
            return gson.fromJson(userJson, Users.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // GET
    public static double getTotalCaloriesPerStep(Integer userId){
        final String methodPath = "calorie.users/getCaloriesBurnedPerStep/" + userId;
        final String type = "application/json";
        return Double.parseDouble(getRestMethod(BASE_URL + methodPath, type));
    }

    // GET
    public static double getCaloriesBurnedAtRest(Integer userId){
        final String methodPath = "calorie.users/getTotalDailyCaloriesBurnedAtRest/" + userId;
        final String type = "application/json";
        return Double.parseDouble(getRestMethod(BASE_URL + methodPath, type));
    }

    // GET
    public static Integer getNextConsumptionId(){
        final String methodPath = "calorie.consumption/count";
        final String type = "text/plain";
        return Integer.parseInt(getRestMethod(BASE_URL + methodPath, type)) + 1;
    }

    // GET
    public static String getTotalCaloriesConsumed(Integer userId){
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        final String methodPath = "calorie.consumption/getTotalCaloriesConsumedByUserIdAndDate/" + userId + "/" + date;
        final String type = "application/json";
        return getRestMethod(BASE_URL + methodPath, type);
    }

    // GET
    public static Integer getNextReportId(){
        final String methodPath = "calorie.report/count";
        final String type = "text/plain";
        return Integer.parseInt(getRestMethod(BASE_URL + methodPath, type)) + 1;
    }

    // GET
    public static Report getReportByIdAndDate(Integer userId, Date date){
        String reportDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
        final String methodPath = "calorie.report/getCaloriesInfoByUserIdAndDate/" + userId + "/" + reportDate;
        final String type = "application/json";
        String reportJson = RestClient.getRestMethod(BASE_URL + methodPath, type);
        if(!reportJson.equals("[]")){
            try{
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
                return gson.fromJson(reportJson, Report.class);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<Report> getReportByIdAndPeriod(Integer userId, Date start, Date end){
        String startDate = new SimpleDateFormat("MM-dd-yyyy").format(start);
        String endDate = new SimpleDateFormat("MM-dd-yyyy").format(end);
        final String methodPath = "calorie.report/getCaloriesInfoByUserIdAndPeriod/" + userId + "/" + startDate + "/" + endDate;
        final String type = "application/json";
        String reportsJson = RestClient.getRestMethod(BASE_URL + methodPath, type);
        List<Report> reports = new ArrayList<>();
        try{
            JSONArray reportArray = new JSONArray(reportsJson);
            for(int i = 0; i < reportArray.length(); i++){
                JSONObject reportObj = reportArray.getJSONObject(i);
                String json = reportObj.toString();
                Log.i("Result: ", json);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
                reports.add(gson.fromJson(json, Report.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reports;

    }

    // POST
    public static void createUser(Users user){
        final String methodPath = "calorie.users/";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        String stringUserJson = gson.toJson(user);
        postRestMethod(BASE_URL + methodPath, stringUserJson);
    }

    // POST
    public static void createCredential(Credential c){
        final String methodPath = "calorie.credential/";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        String stringCredentialJson = gson.toJson(c);
        postRestMethod(BASE_URL + methodPath, stringCredentialJson);
    }

    // POST
    public static void createFood(Food food){
        final String methodPath = "calorie.food/";
        Gson gson = new Gson();
        String stringFoodJson = gson.toJson(food);
        postRestMethod(BASE_URL + methodPath, stringFoodJson);
    }

    // POST
    public static void createReport(Report report){
        final String methodPath = "calorie.report/";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        String stringReportJson = gson.toJson(report);
        postRestMethod(BASE_URL + methodPath, stringReportJson);
    }

    // POST
    public static void createConsumption(Consumption c){
        final String methodPath = "calorie.consumption/";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
        String stringConsumptionJson = gson.toJson(c);
        postRestMethod(BASE_URL + methodPath, stringConsumptionJson);
    }


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
