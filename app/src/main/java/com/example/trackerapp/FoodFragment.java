package com.example.trackerapp;

import android.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.Arrays;
import java.util.Date;
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

                SearchInfoAsyncTask searchInfoAsyncTask = new SearchInfoAsyncTask();
                searchInfoAsyncTask.execute(query);

                searchView.clearFocus();
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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

                final String name = foodObject.getString("label");
                final String category = foodObject.getString("category");
                final double calorie = foodObject.getJSONObject("nutrients").getDouble("ENERC_KCAL");
                final String unit = "gram";
                final double amount = 100;
                final double fat = foodObject.getJSONObject("nutrients").getDouble("FAT");

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

                final EditText qtyText = (EditText) vFood.findViewById(R.id.food_quantity);
                qtyText.setVisibility(View.VISIBLE);

                Button addButton = (Button) vFood.findViewById(R.id.food_add);
                addButton.setVisibility(View.VISIBLE);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String qty = qtyText.getText().toString();
                        if(!qty.equals("")){
                            AddFoodAsyncTask addTask = new AddFoodAsyncTask();
                            String newCat = "other";
                            List<String> catagories = Arrays.asList("Drink", "Meal", "Meat", "Snack",
                                    "Bread", "Cake", "Fruit", "Vegetable", "Dessert");
                            if(catagories.contains(category)){
                                newCat = category.toLowerCase();
                            }

                            SessionManagement session = new SessionManagement(getContext());
                            String userId = Integer.toString(session.getCurrentUserId());

                            addTask.execute(name, newCat, Double.toString(calorie), unit, Double.toString(amount),
                                    Double.toString(fat), qty, userId);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.add_food_error_empty_qty), Toast.LENGTH_LONG).show();
                        }

                    }
                });

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class AddFoodAsyncTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            Integer id = RestClient.getNextFoodId();
            String name = params[0];
            String category = params[1];
            double calorie = Double.parseDouble(params[2]);
            String unit = params[3];
            double serving = Double.parseDouble(params[4]);
            double fat = Double.parseDouble(params[5]);
            Food food = new Food(id, name, category, calorie, unit, serving, fat);
            RestClient.createFood(food);

            Date date = new Date();
            int qty = Integer.parseInt(params[6]);
            int userId = Integer.parseInt(params[7]);
            Users user = RestClient.findUserById(userId);

            Consumption c = new Consumption(id, date, qty, food, user);
            RestClient.createConsumption(c);
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            Toast.makeText(getActivity(), getString(R.string.add_consumption_success), Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new DailyDietFragment()).commit();
        }
    }

    private class SearchInfoAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            return GoogleSearchAPI.search(params[0], new String[]{"num"}, new String[]{"1"});
        }

        @Override
        protected void onPostExecute(String result){
            TextView descripText = (TextView) vFood.findViewById(R.id.food_description);
            String descrip = GoogleSearchAPI.getSnippet(result);
            descrip.replace(",", ":");
            if (descrip.endsWith("...")){
                descrip = descrip.substring(0, descrip.lastIndexOf(",")) + ".";
            }
            descripText.setText(descrip);
        }
    }



}
