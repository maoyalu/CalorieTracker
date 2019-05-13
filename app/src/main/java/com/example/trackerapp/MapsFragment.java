package com.example.trackerapp;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class MapsFragment extends Fragment implements OnMapReadyCallback{
    View vMap;
    private GoogleMap mMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vMap = inflater.inflate(R.layout.fragment_map, container, false);

        return vMap;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        SessionManagement session = new SessionManagement(getContext());
        int userId = session.getCurrentUserId();

        GetAddressAsyncTask addrTask = new GetAddressAsyncTask();
        addrTask.execute(userId);
    }

    private class GetAddressAsyncTask extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... params) {
            int userId = params[0];
            try{
                Users user = RestClient.findUserById(userId);
                return user.getAddress();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String addr){
            Log.i("Address: ", addr);
            LatLng address = getLocationFromAddress(getContext(), addr);
            if(address == null){
                LatLng sydney = new LatLng(-34, 151);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            } else {
                mMap.addMarker(new MarkerOptions().position(address).title("Home"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 12));

                try {
                    ApplicationInfo app = getContext().getPackageManager()
                            .getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
                    String key = app.metaData.getString("com.google.android.geo.API_KEY");
                    String searchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + address.latitude
                            + "," + address.longitude + "&radius=5000&type=park&keyword=park&key=" + key;

                    GetParksAsyncTask parkTask = new GetParksAsyncTask();
                    parkTask.execute(searchUrl);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GetParksAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection conn = null;
            StringBuilder textResult = new StringBuilder();

            try{
                url = new URL(params[0]);

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
            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray placeArray = resultObject.getJSONArray("results");
                for(int i = 0; i < placeArray.length(); i++){
                    JSONObject place = placeArray.getJSONObject(i);
                    String name = place.getString("name");
                    JSONObject geo = place.getJSONObject("geometry");
                    JSONObject location = geo.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private LatLng getLocationFromAddress(Context context, String strAddress){
        Geocoder geocoder = new Geocoder(context);
        List<Address> address;
        LatLng p = null;

        try{
            address = geocoder.getFromLocationName(strAddress, 1);
            while(address.size() == 0){
                address = geocoder.getFromLocationName(strAddress, 1);
            }
            Address location = address.get(0);
            p = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e){
            e.printStackTrace();;
        }

        return p;
    }


}

