package com.bokuyihi.placessearch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;


public class PhotosFragment extends Fragment {

    JSONObject object;

    private RecyclerView recyclerView;

    private GoogleApiClient googleApiClient;
    private ArrayList photoArray = new ArrayList();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        object = ((DetailActivity) getActivity()).getInfo();

        googleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), new OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        return;
                    }
                })
                .build();
        placePhotosAsync();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_fragment, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (photoArray.size() == 0) {
            getView().findViewById(R.id.noPhoto).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.allPhotos).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.noPhoto).setVisibility(View.GONE);
            getView().findViewById(R.id.allPhotos).setVisibility(View.VISIBLE);
            recyclerView = (RecyclerView)getView().findViewById(R.id.allPhotos);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            PhotoAdapter adapter = new PhotoAdapter(photoArray, getContext());
            recyclerView.setAdapter(adapter);
        }
    }



    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            photoArray.add(placePhotoResult.getBitmap());
        }
    };

    private void placePhotosAsync() {
        String placeId = "";
        try {
            placeId = object.getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Places.GeoDataApi.getPlacePhotos(googleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            for (int i = 0; i < photoMetadataBuffer.getCount(); i++) {
                                photoMetadataBuffer.get(i)
                                        .getScaledPhoto(googleApiClient, 1600,
                                                1600)
                                        .setResultCallback(mDisplayPhotoResultCallback);
                            }
                        }
                        photoMetadataBuffer.release();
                    }
                });

    }
}

