package com.bokuyihi.placessearch;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<PlaceItem> allFavoritePlaces = new ArrayList<>();
    private Context context;
    private RecyclerView mRecyclerView;
    private TextView noFavoriteText;


    public FavoritesAdapter(List<PlaceItem> allFavoritePlaces, Context context, RecyclerView mRecyclerView,TextView noFavoriteText) {
        this.allFavoritePlaces = allFavoritePlaces;
        this.context = context;
        this.mRecyclerView = mRecyclerView;
        this.noFavoriteText = noFavoriteText;
    }



    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {

        final PlaceItem placeItem = allFavoritePlaces.get(position);
        holder.name.setText(placeItem.getName());
        holder.address.setText(placeItem.getAddress());
        Picasso.get().load(placeItem.getIcon()).into(holder.icon);
        holder.placeFavorite.setImageResource(R.drawable.heart_fill_red);

        holder.placeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < allFavoritePlaces.size(); i++) {
                    if (allFavoritePlaces.get(i).getPlaceId().equals(placeItem.getPlaceId())){
                        allFavoritePlaces.remove(i);
                        Toast.makeText(context,placeItem.getName()+" has been removed",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (allFavoritePlaces.size() == 0) {
                    noFavoriteText.setVisibility(View.VISIBLE);
                    //noFavoriteText.setVisibility(View.GONE);
                } else {
                    noFavoriteText.setVisibility(View.GONE);
                    //noFavoriteText.setVisibility(View.VISIBLE);
                    FavoritesAdapter adapter = new FavoritesAdapter(allFavoritePlaces, context,mRecyclerView,noFavoriteText);
                    mRecyclerView.setAdapter(adapter);
                }
                saveFavorite();

            }
        });

        holder.placeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(context, DetailActivity.class);
                RequestQueue queue = Volley.newRequestQueue(context);
                String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeItem.getPlaceId()+"&key=AIzaSyCvMtC4NgCW7MnLtqHC54Gm4VOVoXp5e08";


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //progressDialog.dismiss();
                                intent.putExtra("detail",response);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(stringRequest);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (allFavoritePlaces == null) {
            return 0;
        } else {
            return allFavoritePlaces.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView address;
        public ImageView icon;
        public LinearLayout placeItem;
        public ImageButton placeFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            address = (TextView)itemView.findViewById(R.id.address);
            icon = (ImageView)itemView.findViewById(R.id.icon);
            placeItem = (LinearLayout)itemView.findViewById(R.id.placeItem);
            placeFavorite = (ImageButton)itemView.findViewById(R.id.placeFavorite);
        }
    }

    public void saveFavorite(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allFavoritePlaces);
        editor.putString("favorite", json);
        editor.apply();
    }
}
