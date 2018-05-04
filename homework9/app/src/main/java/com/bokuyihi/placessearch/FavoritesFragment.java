package com.bokuyihi.placessearch;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FavoritesFragment extends Fragment {

    public static final String TYPE = "type";


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    public List<PlaceItem> allFavoritePlaces = new ArrayList<>();

    private TextView noFavoriteText;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_fragment, container, false);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        noFavoriteText = getView().findViewById(R.id.noFavorite);
        loadFavorite();

        if (allFavoritePlaces.size() == 0) {
            getView().findViewById(R.id.noFavorite).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.allFavorites).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.noFavorite).setVisibility(View.GONE);
            getView().findViewById(R.id.allFavorites).setVisibility(View.VISIBLE);

            mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.allFavorites);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            FavoritesAdapter adapter = new FavoritesAdapter(allFavoritePlaces, getContext(),mRecyclerView,noFavoriteText);
            mRecyclerView.setAdapter(adapter);
        }




    }

    public void saveFavorite(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allFavoritePlaces);
        editor.putString("favorite", json);
        editor.apply();
    }

    public void loadFavorite(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favorite", null);
        Type type = new TypeToken<ArrayList<PlaceItem>>(){}.getType();
        allFavoritePlaces = gson.fromJson(json, type);
        if (allFavoritePlaces == null) {
            allFavoritePlaces = new ArrayList<PlaceItem>();
        }
    }

}
