package com.bokuyihi.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private List<String> titles;

    private SharedPreferences favorites;
    private List<PlaceItem> wordList = new ArrayList<PlaceItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Places Search");
        initView();
        initValue();
        getSharedPreferences("favorites",MODE_PRIVATE);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }


    private void initValue() {
        fragments = new ArrayList<>();
        fragments.add(new SearchFragment());
        fragments.add(new FavoritesFragment());


        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setCustomView(getView(0));
        tabLayout.getTabAt(1).setCustomView(getView(1));
    }


    public View getView(int i){
        View view = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        if (i == 0) {
            ImageView img = (ImageView) view.findViewById(R.id.tabIcon);
            img.setImageResource(R.drawable.search);
            TextView text = (TextView)view.findViewById(R.id.tabText);
            text.setText("Search");
        } else {
            ImageView img = (ImageView)view.findViewById(R.id.tabIcon);
            img.setImageResource(R.drawable.heart_fill_white);
            TextView text = (TextView)view.findViewById(R.id.tabText);
            text.setText("Favorites");
        }
        return view;
    }



}
