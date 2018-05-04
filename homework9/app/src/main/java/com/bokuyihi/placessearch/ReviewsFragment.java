package com.bokuyihi.placessearch;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ReviewsFragment extends Fragment {

    View view;
    JSONObject object;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ReviewItem> allReviews = new ArrayList<>();
    private List<ReviewItem> googleReviews = new ArrayList<>();
    private List<ReviewItem> yelpReviews = new ArrayList<>();
    private List<ReviewItem> originGoogleReviews = new ArrayList<>();
    private List<ReviewItem> originYelpReviews = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reviews_fragment, container, false);
//        handleReviews();


//        Spinner spinner = (Spinner)view.findViewById(R.id.googleOrYelpSpinner);
//        final String[] googleyelp = getResources().getStringArray(R.array.googleOrYelp);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_spinner_item,googleyelp);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = googleyelp[position];
//                if (item.equals("Google Reviews")){
//                    allReviews = googleReviews;
//                } else if (item.equals("Yelp Reviews")) {
//                    allReviews = yelpReviews;
//                }
//                if (allReviews.size() == 0) {
//                    view.findViewById(R.id.noReview).setVisibility(View.VISIBLE);
//                    view.findViewById(R.id.allReviews).setVisibility(View.GONE);
//                } else {
//                    view.findViewById(R.id.noReview).setVisibility(View.GONE);
//                    view.findViewById(R.id.allReviews).setVisibility(View.VISIBLE);
//                    ReviewsAdapter adapter = new ReviewsAdapter(allReviews, getContext());
//                    recyclerView.setAdapter(adapter);
//                }
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        Spinner spinner1 = (Spinner)view.findViewById(R.id.orderSpinner);
//        final String[] order = getResources().getStringArray(R.array.orderMethod);
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_spinner_item,order);
//        spinner1.setAdapter(adapter1);
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = order[position];
//                if (item.equals("Default Order")){
//                    googleReviews.removeAll(googleReviews);
//                    yelpReviews.removeAll(yelpReviews);
//                    for(int i = 0; i < originGoogleReviews.size(); i++) {
//                        googleReviews.add(originGoogleReviews.get(i));
//                    }
//                    for(int i = 0; i < originYelpReviews.size(); i++) {
//                        yelpReviews.add(originYelpReviews.get(i));
//                    }
//                } else if (item.equals("Highest Rating")) {
//                    Collections.sort(googleReviews, new MostRate());
//                    Collections.sort(yelpReviews, new MostRate());
//                } else if (item.equals("Lowest rating")) {
//                    Collections.sort(googleReviews, new LeastRate());
//                    Collections.sort(yelpReviews, new LeastRate());
//                } else if (item.equals("Most Recent")) {
//                    Collections.sort(googleReviews, new MostRecent());
//                    Collections.sort(yelpReviews, new MostRecent());
//                } else if (item.equals("Least Recent")) {
//                    Collections.sort(googleReviews, new LeastRecent());
//                    Collections.sort(yelpReviews, new LeastRecent());
//                }
//
//                if (allReviews == googleReviews) {
//                    allReviews = googleReviews;
//                } else {
//                    allReviews = yelpReviews;
//                }
//                ReviewsAdapter adapter = new ReviewsAdapter(allReviews, getContext());
//                recyclerView.setAdapter(adapter);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleReviews();
        Spinner spinner = (Spinner)view.findViewById(R.id.googleOrYelpSpinner);
        final String[] googleyelp = getResources().getStringArray(R.array.googleOrYelp);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,googleyelp);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = googleyelp[position];
                if (item.equals("Google Reviews")){
                    allReviews = googleReviews;
                } else if (item.equals("Yelp Reviews")) {
                    allReviews = yelpReviews;
                }
                if (allReviews.size() == 0) {
                    getView().findViewById(R.id.noReview).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.allReviews).setVisibility(View.GONE);
                } else {
                    getView().findViewById(R.id.noReview).setVisibility(View.GONE);
                    getView().findViewById(R.id.allReviews).setVisibility(View.VISIBLE);
                    ReviewsAdapter adapter = new ReviewsAdapter(allReviews, getContext());
                    recyclerView.setAdapter(adapter);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner1 = (Spinner)view.findViewById(R.id.orderSpinner);
        final String[] order = getResources().getStringArray(R.array.orderMethod);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,order);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = order[position];
                if (item.equals("Default Order")){
                    googleReviews.removeAll(googleReviews);
                    yelpReviews.removeAll(yelpReviews);
                    for(int i = 0; i < originGoogleReviews.size(); i++) {
                        googleReviews.add(originGoogleReviews.get(i));
                    }
                    for(int i = 0; i < originYelpReviews.size(); i++) {
                        yelpReviews.add(originYelpReviews.get(i));
                    }
                } else if (item.equals("Highest Rating")) {
                    Collections.sort(googleReviews, new MostRate());
                    Collections.sort(yelpReviews, new MostRate());
                } else if (item.equals("Lowest rating")) {
                    Collections.sort(googleReviews, new LeastRate());
                    Collections.sort(yelpReviews, new LeastRate());
                } else if (item.equals("Most Recent")) {
                    Collections.sort(googleReviews, new MostRecent());
                    Collections.sort(yelpReviews, new MostRecent());
                } else if (item.equals("Least Recent")) {
                    Collections.sort(googleReviews, new LeastRecent());
                    Collections.sort(yelpReviews, new LeastRecent());
                }

                if (allReviews == googleReviews) {
                    allReviews = googleReviews;
                } else {
                    allReviews = yelpReviews;
                }
                ReviewsAdapter adapter = new ReviewsAdapter(allReviews, getContext());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void handleReviews() {
        recyclerView = (RecyclerView)view.findViewById(R.id.allReviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        object = ((DetailActivity) getActivity()).getInfo();
        try {
            JSONArray reviewArray = object.getJSONArray("reviews");
            googleReviews = new ArrayList<>();
            originGoogleReviews = new ArrayList<>();
            for(int i = 0; i < reviewArray.length(); i++) {
                JSONObject object = reviewArray.getJSONObject(i);
                int rating = object.getInt("rating");
                String ratingStar = "";
                for (int j = 0; j < rating; j++) {
                    ratingStar += "★";
                }
                SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                String time = object.getString("time");
                Long longtime = Long.parseLong(time);
                String d = format.format(longtime*1000);
                ReviewItem item = new ReviewItem(
                        object.getString("author_name"),
                        ratingStar,
                        d,
                        object.getString("profile_photo_url"),
                        object.getString("text"),
                        object.getString("author_url")
                );
                googleReviews.add(item);
                originGoogleReviews.add(item);
            }
            allReviews = googleReviews;
            if (allReviews.size() == 0) {
                getView().findViewById(R.id.noReview).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.allReviews).setVisibility(View.GONE);
            } else {
                getView().findViewById(R.id.noReview).setVisibility(View.GONE);
                getView().findViewById(R.id.allReviews).setVisibility(View.VISIBLE);
                ReviewsAdapter adapter = new ReviewsAdapter(allReviews, getContext());
                recyclerView.setAdapter(adapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // hamdle yelp review

        RequestQueue queue = Volley.newRequestQueue(getContext());


        try {
            JSONArray address_components = object.getJSONArray("address_components");

            String name = object.getString("name");
            String address1 = object.getString("vicinity").split(",")[0];
            String city = "Los Angeles";
            String state = "CA";

            for (int i = 0; i < address_components.length(); i++) {
                JSONObject components = address_components.getJSONObject(i);
                JSONArray types = components.getJSONArray("types");
                String type = types.getString(0);
                if (type.equals("administrative_area_level_1")) {
                    state = components.getString("short_name");
                } else if (type.equals("administrative_area_level_2")) {
                    city = components.getString("long_name");
                }
            }


            String url = "http://Csci571hw8-env.cxn4etsqhn.us-west-1.elasticbeanstalk.com/getYelpReviews?name="+name+"&address1="+address1+"&city="+city+"&state="+state;
            url = url.replace(" ","%20");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject yelpObject = response;
                    try {
                        if (yelpObject.getString("hasReview").equals("true")){
                            JSONArray reviewArray = yelpObject.getJSONArray("reviews");
                            yelpReviews = new ArrayList<>();
                            originYelpReviews = new ArrayList<>();
                            for(int i = 0; i < reviewArray.length(); i++) {
                                JSONObject object = reviewArray.getJSONObject(i);
                                int rating = object.getInt("rating");
                                String ratingStar = "";
                                for (int j = 0; j < rating; j++) {
                                    ratingStar += "★";
                                }
                                JSONObject user = object.getJSONObject("user");
                                ReviewItem item = new ReviewItem(
                                        user.getString("name"),
                                        ratingStar,
                                        object.getString("time_created"),
                                        user.getString("image_url"),
                                        object.getString("text"),
                                        object.getString("url")
                                );
                                yelpReviews.add(item);
                                originYelpReviews.add(item);
                            }
                        } else {
                            // did not get yelp review;
                        }
                    } catch (JSONException e) {
                        // did not get yelp review;
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            // cannot get yelp review;
            e.printStackTrace();
        }
    }

    public class MostRate implements Comparator<ReviewItem>{
        @Override
        public int compare(ReviewItem o1, ReviewItem o2) {
            return o2.getReviewerRating().compareTo(o1.getReviewerRating());
        }
    }

    public class LeastRate implements Comparator<ReviewItem>{
        @Override
        public int compare(ReviewItem o1, ReviewItem o2) {
            return o1.getReviewerRating().compareTo(o2.getReviewerRating());
        }
    }

    public class MostRecent implements Comparator<ReviewItem>{
        @Override
        public int compare(ReviewItem o1, ReviewItem o2) {
            return o2.getReviewerTime().compareTo(o1.getReviewerTime());
        }
    }

    public class LeastRecent implements Comparator<ReviewItem>{
        @Override
        public int compare(ReviewItem o1, ReviewItem o2) {
            return o1.getReviewerTime().compareTo(o2.getReviewerTime());
        }
    }
}
