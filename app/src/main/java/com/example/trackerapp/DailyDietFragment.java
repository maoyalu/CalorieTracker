package com.example.trackerapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DailyDietFragment extends Fragment {
    View vDailyDiet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vDailyDiet = inflater.inflate(R.layout.fragment_daily_diet, container, false);


        List<String> clist = new ArrayList<String>();
        clist.addAll(Arrays.asList("Drink", "Meal", "Meat", "Snack", "Bread", "Cake", "Fruit", "Vegetable", "Dessert", "Other"));
        Spinner sCategory = (Spinner) vDailyDiet.findViewById(R.id.categorySpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, clist);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(spinnerAdapter);

        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();


                FindFoodAsyncTask getFood = new FindFoodAsyncTask();
                getFood.execute(selectedCategory.toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button addButton = (Button) vDailyDiet.findViewById(R.id.foodAddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText qtyText = vDailyDiet.findViewById(R.id.foodQuantity);
                String qty = qtyText.getText().toString();
                if(!qty.equals("")){
                    Spinner sFood = (Spinner) vDailyDiet.findViewById(R.id.foodSpinner);
                    String foodName = sFood.getSelectedItem().toString();

                    SessionManagement session = new SessionManagement(getContext());
                    String userId = Integer.toString(session.getCurrentUserId());

                    AddFoodAsyncTask add = new AddFoodAsyncTask();
                    add.execute(qty, foodName, userId);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.add_food_error_empty_qty), Toast.LENGTH_LONG).show();
                }
            }
        });


        return vDailyDiet;
    }

    private class AddFoodAsyncTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            Integer id = RestClient.getNextConsumptionId();
            Date date = new Date();
            int qty = Integer.parseInt(params[0]);
            Food food = RestClient.findFoodByName(params[1]);
            int userId = Integer.parseInt(params[2]);
            Users user = RestClient.findUserById(userId);

            Consumption c = new Consumption(id, date, qty, food, user);
            RestClient.createConsumption(c);
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            Toast.makeText(getActivity(), getString(R.string.add_consumption_success), Toast.LENGTH_LONG).show();
        }
    }

    private class FindFoodAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...params){
            return RestClient.findFoodByCategory(params[0]);
        }

        @Override
        protected void onPostExecute(String result){
            try{

                final JSONArray food = new JSONArray(result);
                List<String> fList = new ArrayList<String>();
                for(int i = 0; i < food.length(); i++){
                    fList.add(food.getJSONObject(i).getString("foodName"));
                }

                Spinner sFood = (Spinner) vDailyDiet.findViewById(R.id.foodSpinner);
                ArrayAdapter<String> foodSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, fList);
                foodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sFood.setAdapter(foodSpinnerAdapter);

                sFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try{
                            JSONObject selectedFood = food.getJSONObject(position);
                            String unit = selectedFood.getString("servingUnit");
                            double num = selectedFood.getDouble("servingAmount");
                            String servingStr = num + " " + unit;
                            TextView serving = (TextView) vDailyDiet.findViewById(R.id.servingText);
                            serving.setText(servingStr);
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            } catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
