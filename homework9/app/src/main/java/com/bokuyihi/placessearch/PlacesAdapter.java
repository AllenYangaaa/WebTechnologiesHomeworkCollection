package com.bokuyihi.placessearch;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bokuyihi on 18/04/2018.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<PlaceItem> allPlaces = new ArrayList<>();
    private Context context;

    private OnItemClickListener mListener;
    public List<PlaceItem> allFavoritePLaces = new ArrayList<PlaceItem>();


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public PlacesAdapter(List<PlaceItem> allPlaces, Context context) {
        this.allPlaces = allPlaces;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        loadFavorite();
        final PlaceItem placeItem = allPlaces.get(position);
        holder.name.setText(placeItem.getName());
        holder.address.setText(placeItem.getAddress());
        Picasso.get().load(placeItem.getIcon()).into(holder.icon);

        boolean exist = false;
        for (int i = 0; i < allFavoritePLaces.size(); i++) {
            if (allFavoritePLaces.get(i).getPlaceId().equals(placeItem.getPlaceId())){
                exist = true;
                break;
            } else {
                exist = false;
            }
        }

        if (exist) {
            holder.placeFavorite.setImageResource(R.drawable.heart_fill_red);
        } else {
            holder.placeFavorite.setImageResource(R.drawable.heart_outline_black);
        }



        holder.placeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFavorite();
                boolean exist = false;
                for (int i = 0; i < allFavoritePLaces.size(); i++) {
                    if (allFavoritePLaces.get(i).getPlaceId().equals(placeItem.getPlaceId())){
                        exist = true;
                        break;
                    } else {
                        exist = false;
                    }
                }
                if (exist) {
                    holder.placeFavorite.setImageResource(R.drawable.heart_outline_black);
                    for (int i = 0; i < allFavoritePLaces.size(); i++) {
                        if (allFavoritePLaces.get(i).getPlaceId().equals(placeItem.getPlaceId())){
                            allFavoritePLaces.remove(i);
                            Toast.makeText(context,placeItem.getName()+" has been removed",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } else {
                    allFavoritePLaces.add(placeItem);
                    Toast.makeText(context,placeItem.getName()+" has been added to favorite list",Toast.LENGTH_SHORT).show();
                    holder.placeFavorite.setImageResource(R.drawable.heart_fill_red);
                }
                saveFavorite();
            }
        });


        holder.placeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(context, DetailActivity.class);
//                final ProgressDialog progressDialog = new ProgressDialog(context);
//                progressDialog.setMessage("Fetching Details");
//                progressDialog.show();

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
        if (allPlaces == null) {
            return 0;
        } else {
            return allPlaces.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView address;
        public ImageView icon;
        public LinearLayout placeItem;
        public ImageButton placeFavorite;

        public ViewHolder(View itemView , final OnItemClickListener listener) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.name);
            address = (TextView)itemView.findViewById(R.id.address);
            icon = (ImageView)itemView.findViewById(R.id.icon);

            placeItem = (LinearLayout)itemView.findViewById(R.id.placeItem);

            placeFavorite = (ImageButton)itemView.findViewById(R.id.placeFavorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null ) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public void saveFavorite(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allFavoritePLaces);
        editor.putString("favorite", json);
        editor.apply();
    }

    public void loadFavorite(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favorite", null);
        Type type = new TypeToken<ArrayList<PlaceItem>>(){}.getType();
        allFavoritePLaces = gson.fromJson(json, type);
        if (allFavoritePLaces == null) {
            allFavoritePLaces = new ArrayList<PlaceItem>();
        }
    }

}
