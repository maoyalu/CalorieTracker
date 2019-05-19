package com.example.trackerapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GoogleSearchAPI {
    private static final String API_KEY = "AIzaSyA23KJ7HRKHjKkwrdu22Dv_nsClNgcH6lQ";
    private static final String SEARCH_ID_cx = "015265908080207407062:vwxwhik3vnm";

    public static String search(String keyword, String[] params, String[] values){
        keyword = keyword.replace(" ", "+");
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        String query_parameter = "";

        if (params != null && values != null){
            for (int i = 0; i < params.length; i++){
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }
        try{
            url = new URL("https://www.googleapis.com/customsearch/v1?key=" + API_KEY
                    + "&cx=" + SEARCH_ID_cx + "&q=" + keyword + query_parameter);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNextLine()){
                textResult += scanner.nextLine();
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    public static String getSnippet(String result){
        String snippet = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            if(jsonArray != null && jsonArray.length() > 0){
                snippet = jsonArray.getJSONObject(0).getString("snippet");
            }
        } catch (Exception e){
            e.printStackTrace();
            snippet = "NO INFO FOUND";
        }
        return snippet;
    }
}
