package com.example.trackerapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FoodFragment extends Fragment{
    View vFood;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vFood = inflater.inflate(R.layout.fragment_food, container, false);

        final SearchView searchView = (SearchView) vFood.findViewById(R.id.search_new_food);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchFoodAsyncTask searchTask = new SearchFoodAsyncTask();
                searchTask.execute(query);

                searchView.clearFocus();
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

//        ListView resultList = (ListView) vFood.findViewById(R.id.food_list_view);
//        List<String> data = new ArrayList<String>();
//        data.add("food 1");
//        data.add("food 2");
//        data.add("food 3");
//        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.food_listview, data);
//        resultList.setAdapter(adapter);

        return vFood;
    }

    private class SearchFoodAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection conn = null;
            StringBuilder textResult = new StringBuilder();

            try {
                ApplicationInfo app = getContext().getPackageManager()
                        .getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
                String id = app.metaData.getString("com.edamam.food.APP_ID");
                String key = app.metaData.getString("com.edamam.food.API_KEY");
                String query = "https://api.edamam.com/api/food-database/parser?ingr=" + params[0].replaceAll(" ", "%20") +
                        "&app_id=" + id + "&app_key=" + key;

                url = new URL(query);

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

        @Override
        protected void onPostExecute(String result){
            try{


                JSONObject resultObject = new JSONObject(result);
                JSONObject foodObject = resultObject.getJSONArray("parsed").getJSONObject(0).getJSONObject("food");

                String name = foodObject.getString("label");
                String category = foodObject.getString("category");
                double calorie = foodObject.getJSONObject("nutrients").getDouble("ENERC_KCAL");
                String unit = "gram";
                double amount = 100;
                double fat = foodObject.getJSONObject("nutrients").getDouble("FAT");

                String img = foodObject.getString("image");
                ImageView imgView = (ImageView) vFood.findViewById(R.id.food_img);
                Picasso.get().load(img).into(imgView);

                TextView nameText = (TextView) vFood.findViewById(R.id.food_name);
                String nameStr = "Name: " + name;
                nameText.setText(nameStr);

                TextView categoryText = (TextView) vFood.findViewById(R.id.food_category);
                String categoryStr = "Category: " + category;
                categoryText.setText(categoryStr);

                TextView servingText = (TextView) vFood.findViewById(R.id.food_serving);
                String servingStr = "Serving: " + amount + " " + unit;
                servingText.setText(servingStr);

                TextView calorieText = (TextView) vFood.findViewById(R.id.food_calorie);
                String calorieStr = "Calorie: " + calorie + " kCal";
                calorieText.setText(calorieStr);

                TextView fatText = (TextView) vFood.findViewById(R.id.food_fat);
                String fatStr = "Fat: " + fat + " g";
                fatText.setText(fatStr);

                TextView descripText = (TextView) vFood.findViewById(R.id.food_description);
//                String
                descripText.setText("Description: ");

                Button addButton = (Button) vFood.findViewById(R.id.food_add);
                addButton.setVisibility(View.VISIBLE);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
