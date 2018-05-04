package com.bokuyihi.placessearch;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private List<String> titles;

    private JSONObject detail;


    private InfoFragment info_fragment;
    private PhotosFragment photos_fragment;
    private MapFragment map_fragment;
    private ReviewsFragment reviews_fragment;


    private JSONObject object;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private List<PlaceItem> allFavoritePlaces = new ArrayList<>();
    private String placeId = "";

    private MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            JSONObject obj =  new JSONObject(getIntent().getStringExtra("detail"));
            object = obj.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String title = "";
        try {
            title = object.getString("name");
            placeId = object.getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle(title);


        initView();
        initValue();

    }

    public JSONObject getInfo(){
        return object;
    }


    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.fourviewpager);
        tabLayout = (TabLayout) findViewById(R.id.fourtabs);
    }


    private void initValue() {
        fragments = new ArrayList<>();

        info_fragment = new InfoFragment();
        fragments.add(info_fragment);

        photos_fragment = new PhotosFragment();
        fragments.add(photos_fragment);

        map_fragment = new MapFragment();
        fragments.add(map_fragment);

        reviews_fragment = new ReviewsFragment();
        fragments.add(reviews_fragment);


        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setCustomView(getView(0));
        tabLayout.getTabAt(1).setCustomView(getView(1));
        tabLayout.getTabAt(2).setCustomView(getView(2));
        tabLayout.getTabAt(3).setCustomView(getView(3));

    }

    public View getView(int i){
        View view = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        if (i == 0) {
            ImageView img = (ImageView) view.findViewById(R.id.tabIcon);
            img.setImageResource(R.drawable.info_outline);
            TextView text = (TextView) view.findViewById(R.id.tabText);
            text.setText("INFO");
        } else if (i == 1) {
            ImageView img = (ImageView)view.findViewById(R.id.tabIcon);
            img.setImageResource(R.drawable.photos);
            TextView text = (TextView)view.findViewById(R.id.tabText);
            text.setText("PHOTOS");
        } else if (i == 2) {
            ImageView img = (ImageView)view.findViewById(R.id.tabIcon);
            img.setImageResource(R.drawable.maps);
            TextView text = (TextView)view.findViewById(R.id.tabText);
            text.setText("MAP");
        } else if (i == 3) {
            ImageView img = (ImageView)view.findViewById(R.id.tabIcon);
            img.setImageResource(R.drawable.review);
            TextView text = (TextView)view.findViewById(R.id.tabText);
            text.setText("REVIEWS");
        }
        return view;
    }

    public View getViews(){
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar, null);
        ImageView img1 = (ImageView) view.findViewById(R.id.shared);
        img1.setImageResource(R.drawable.share);
        ImageView img2 = (ImageView) view.findViewById(R.id.shared);
        img2.setImageResource(R.drawable.heart_fill_white);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareButton:
                String tempName = "";
                String tempAddress = "";
                String tempWebsite = "";
                try {
                    tempName = object.getString("name");
                    tempAddress = object.getString("formatted_address");
                    tempWebsite = object.getString("website");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String text = "Check out "+tempName+".Location at "+tempAddress+".Website:"+tempWebsite;
                String url = "https://twitter.com/intent/tweet?text="+text;
                url = url.replace(" ", "+");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else{
                    //Page not found
                }
                return true;

            case R.id.favoriteButton:

                loadFavorite();
                boolean exist = false;
                for (int i = 0; i < allFavoritePlaces.size(); i++) {
                    if (allFavoritePlaces.get(i).getPlaceId().equals(placeId)){
                        exist = true;
                        break;
                    } else {
                        exist = false;
                    }
                }
                if (exist) {
                    menuItem.setIcon(R.drawable.heart_outline_white);
                    for (int i = 0; i < allFavoritePlaces.size(); i++) {
                        if (allFavoritePlaces.get(i).getPlaceId().equals(placeId)){
                            Toast.makeText(getApplicationContext(),allFavoritePlaces.get(i).getName()+" has been removed from favorite list",Toast.LENGTH_SHORT).show();
                            allFavoritePlaces.remove(i);
                            break;
                        }
                    }
                } else {
                    String tpName = "";
                    String tpAdd = "";
                    String tpIcon = "";
                    String tpPlaceId = "";
                    try {
                        tpName = object.getString("name");
                        tpAdd = object.getString("vicinity");
                        tpIcon = object.getString("icon");
                        tpPlaceId = object.getString("place_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PlaceItem placeItem = new PlaceItem(tpName, tpAdd, tpIcon, tpPlaceId);
                    allFavoritePlaces.add(placeItem);
                    menuItem.setIcon(R.drawable.heart_fill_white);
                    Toast.makeText(getApplicationContext(),placeItem.getName()+" has been added to favorite list",Toast.LENGTH_SHORT).show();
                }
                saveFavorite();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        menuItem = menu.getItem(1);
        loadFavorite();
        boolean exist = false;
        for (int i = 0; i < allFavoritePlaces.size(); i++) {
            if (allFavoritePlaces.get(i).getPlaceId().equals(placeId)){
                exist = true;
                break;
            } else {
                exist = false;
            }
        }
        if (exist) {
            menuItem.setIcon(R.drawable.heart_fill_white);
        } else {
            menuItem.setIcon(R.drawable.heart_outline_white);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveFavorite(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allFavoritePlaces);
        editor.putString("favorite", json);
        editor.apply();
    }

    public void loadFavorite(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favorite", null);
        Type type = new TypeToken<ArrayList<PlaceItem>>(){}.getType();
        allFavoritePlaces = gson.fromJson(json, type);
        if (allFavoritePlaces == null) {
            allFavoritePlaces = new ArrayList<PlaceItem>();
        }
    }


}
