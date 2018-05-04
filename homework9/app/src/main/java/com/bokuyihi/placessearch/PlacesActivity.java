package com.bokuyihi.placessearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PlacesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<PlaceItem> allPlaces = new ArrayList<>();
    private List<PlaceItem> placesPage1 = new ArrayList<>();
    private List<PlaceItem> placesPage2 = new ArrayList<>();
    private List<PlaceItem> placesPage3 = new ArrayList<>();

    private JSONObject currentPageInfo;
    private JSONObject pageOneInfo;
    private JSONObject pageTwoInfo;
    private JSONObject pageThreeInfo;


    private List<PlaceItem> allFavoritePlaces = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_activity);
        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.place_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Search results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        mRecyclerView = (RecyclerView) findViewById(R.id.allPlaces);
//        mRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        try{
//            JSONObject obj =  new JSONObject(getIntent().getStringExtra("places"));
//            pageOneInfo = obj.getJSONObject("places");
//            currentPageInfo = pageOneInfo;
//            JSONArray results = obj.getJSONObject("places").getJSONArray("results");
//
//            for(int i = 0; i < results.length(); i++) {
//                JSONObject object = results.getJSONObject(i);
//
//                boolean isFavorite = false;
//                for (int j = 0; j < allFavoritePlaces.size(); j++) {
//                    if(object.getString("place_id" ).equals(allFavoritePlaces.get(j).getPlaceId())){
//                        isFavorite = true;
//                        break;
//                    }
//                }
//
//                PlaceItem item = new PlaceItem(
//                        object.getString("name"),
//                        object.getString("vicinity"),
//                        object.getString("icon"),
//                        object.getString("place_id")
//                );
//                placesPage1.add(item);
//            }
//            allPlaces = placesPage1;
//            PlacesAdapter adapter = new PlacesAdapter(allPlaces, getApplicationContext());
//            mRecyclerView.setAdapter(adapter);
//
////            adapter.setOnItemClickListener(new PlacesAdapter.OnItemClickListener() {
////                @Override
////                public void onItemClick(int position) {
////                    PlaceItem place = allPlaces.get(position);
////                    final Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
////
//////                    final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
//////                    progressDialog.setMessage("Fetching Details");
//////                    progressDialog.show();
////
////                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
////                    String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+place.getPlaceId()+"&key=AIzaSyCvMtC4NgCW7MnLtqHC54Gm4VOVoXp5e08";
////
////
////                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
////                            new Response.Listener<String>() {
////                                @Override
////                                public void onResponse(String response) {
////                                    //progressDialog.dismiss();
////                                    intent.putExtra("detail",response);
////                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                    startActivity(intent);
////                                }
////                            }, new Response.ErrorListener() {
////                        @Override
////                        public void onErrorResponse(VolleyError error) {
////                            //progressDialog.dismiss();
////                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
////                        }
////                    });
////
////                    queue.add(stringRequest);
////                }
////            });
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        findViewById(R.id.previousButton).setEnabled(false);
//        try {
//            if (currentPageInfo.getString("next_page_token").equals("")){
//                findViewById(R.id.nextButton).setEnabled(false);
//            } else {
//                findViewById(R.id.nextButton).setEnabled(true);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            findViewById(R.id.nextButton).setEnabled(false);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mRecyclerView = (RecyclerView) findViewById(R.id.allPlaces);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try{
            JSONObject obj =  new JSONObject(getIntent().getStringExtra("places"));
            pageOneInfo = obj.getJSONObject("places");
            currentPageInfo = pageOneInfo;
            JSONArray results = obj.getJSONObject("places").getJSONArray("results");

            for(int i = 0; i < results.length(); i++) {
                JSONObject object = results.getJSONObject(i);

                boolean isFavorite = false;
                for (int j = 0; j < allFavoritePlaces.size(); j++) {
                    if(object.getString("place_id" ).equals(allFavoritePlaces.get(j).getPlaceId())){
                        isFavorite = true;
                        break;
                    }
                }

                PlaceItem item = new PlaceItem(
                        object.getString("name"),
                        object.getString("vicinity"),
                        object.getString("icon"),
                        object.getString("place_id")
                );
                placesPage1.add(item);
            }
            allPlaces = placesPage1;

            if (allPlaces.size() == 0) {
                findViewById(R.id.noResult).setVisibility(View.VISIBLE);
                findViewById(R.id.allPlaces).setVisibility(View.GONE);
            } else {
                findViewById(R.id.noResult).setVisibility(View.GONE);
                findViewById(R.id.allPlaces).setVisibility(View.VISIBLE);
                PlacesAdapter adapter = new PlacesAdapter(allPlaces, getApplicationContext());
                mRecyclerView.setAdapter(adapter);
            }


//            adapter.setOnItemClickListener(new PlacesAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(int position) {
//                    PlaceItem place = allPlaces.get(position);
//                    final Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
//
////                    final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
////                    progressDialog.setMessage("Fetching Details");
////                    progressDialog.show();
//
//                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                    String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+place.getPlaceId()+"&key=AIzaSyCvMtC4NgCW7MnLtqHC54Gm4VOVoXp5e08";
//
//
//                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    //progressDialog.dismiss();
//                                    intent.putExtra("detail",response);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//                            }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            //progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                    queue.add(stringRequest);
//                }
//            });


        } catch (Exception e) {
            findViewById(R.id.noResult).setVisibility(View.VISIBLE);
            findViewById(R.id.allPlaces).setVisibility(View.GONE);
            e.printStackTrace();
        }

        findViewById(R.id.previousButton).setEnabled(false);
        try {
            if (currentPageInfo.getString("next_page_token").equals("")){
                findViewById(R.id.nextButton).setEnabled(false);
            } else {
                findViewById(R.id.nextButton).setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            findViewById(R.id.nextButton).setEnabled(false);
        }
    }

    public void getNextPage(View view){
        RequestQueue queue = Volley.newRequestQueue(PlacesActivity.this);
        if (allPlaces == placesPage1) {
            if (!placesPage2.isEmpty()){
                allPlaces = placesPage2;
                currentPageInfo = pageTwoInfo;
                mRecyclerView.setAdapter(new PlacesAdapter(allPlaces, getApplicationContext()));
                try {
                    if (currentPageInfo.getString("next_page_token").equals("")){
                        findViewById(R.id.nextButton).setEnabled(false);
                    } else {
                        findViewById(R.id.previousButton).setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    findViewById(R.id.nextButton).setEnabled(false);
                }
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(PlacesActivity.this);
                progressDialog.setMessage("Fetching next page");
                progressDialog.show();

                try {
                    String nextPageToken = currentPageInfo.getString("next_page_token");
                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+nextPageToken+"&key=AIzaSyCWOZwD2k-NkgeVmHPpWvvAFvVsYZRDLHo";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            pageTwoInfo = response;
                            currentPageInfo = pageTwoInfo;
                            try {
                                JSONArray results = response.getJSONArray("results");
                                for(int i = 0; i < results.length(); i++) {
                                    JSONObject object = results.getJSONObject(i);

                                    boolean isFavorite = false;
                                    for (int j = 0; j < allFavoritePlaces.size(); j++) {
                                        if(object.getString("place_id" ).equals(allFavoritePlaces.get(j).getPlaceId())){
                                            isFavorite = true;
                                            break;
                                        }
                                    }

                                    PlaceItem item = new PlaceItem(
                                            object.getString("name"),
                                            object.getString("vicinity"),
                                            object.getString("icon"),
                                            object.getString("place_id")
                                    );
                                    placesPage2.add(item);
                                }
                                allPlaces = placesPage2;
                                mRecyclerView.setAdapter(new PlacesAdapter(allPlaces, getApplicationContext()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (currentPageInfo.getString("next_page_token").equals("")){
                                    findViewById(R.id.nextButton).setEnabled(false);
                                } else {
                                    findViewById(R.id.previousButton).setEnabled(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                findViewById(R.id.nextButton).setEnabled(false);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(PlacesActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            findViewById(R.id.previousButton).setEnabled(true);
        } else if (allPlaces == placesPage2) {
            if (!placesPage3.isEmpty()){
                allPlaces = placesPage3;
                currentPageInfo = pageThreeInfo;
                mRecyclerView.setAdapter(new PlacesAdapter(allPlaces, getApplicationContext()));
                try {
                    if (currentPageInfo.getString("next_page_token").equals("")){
                        findViewById(R.id.nextButton).setEnabled(false);
                    } else {
                        findViewById(R.id.previousButton).setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    findViewById(R.id.nextButton).setEnabled(false);
                }
            } else {

                final ProgressDialog progressDialog = new ProgressDialog(PlacesActivity.this);
                progressDialog.setMessage("Fetching next page");
                progressDialog.show();

                try {
                    String nextPageToken = currentPageInfo.getString("next_page_token");
                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+nextPageToken+"&key=AIzaSyCWOZwD2k-NkgeVmHPpWvvAFvVsYZRDLHo";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            pageThreeInfo = response;
                            currentPageInfo = pageThreeInfo;
                            try {
                                JSONArray results = response.getJSONArray("results");
                                for(int i = 0; i < results.length(); i++) {
                                    JSONObject object = results.getJSONObject(i);
                                    boolean isFavorite = false;
                                    for (int j = 0; j < allFavoritePlaces.size(); j++) {
                                        if(object.getString("place_id" ).equals(allFavoritePlaces.get(j).getPlaceId())){
                                            isFavorite = true;
                                            break;
                                        }
                                    }

                                    PlaceItem item = new PlaceItem(
                                            object.getString("name"),
                                            object.getString("vicinity"),
                                            object.getString("icon"),
                                            object.getString("place_id")
                                    );
                                    placesPage3.add(item);
                                }
                                allPlaces = placesPage3;
                                mRecyclerView.setAdapter(new PlacesAdapter(allPlaces, getApplicationContext()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (currentPageInfo.getString("next_page_token").equals("")){
                                    findViewById(R.id.nextButton).setEnabled(false);
                                } else {
                                    findViewById(R.id.previousButton).setEnabled(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                findViewById(R.id.nextButton).setEnabled(false);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(PlacesActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getPrevioPage(View view){
        findViewById(R.id.nextButton).setEnabled(true);
        if (allPlaces == placesPage3){
            allPlaces = placesPage2;
            mRecyclerView.setAdapter(new PlacesAdapter(allPlaces, getApplicationContext()));
            findViewById(R.id.previousButton).setEnabled(true);
        } else {
            allPlaces = placesPage1;
            mRecyclerView.setAdapter(new PlacesAdapter(allPlaces, getApplicationContext()));
            findViewById(R.id.previousButton).setEnabled(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
