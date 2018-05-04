package com.bokuyihi.placessearch;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;


public class SearchFragment extends Fragment implements OnMapReadyCallback{

    private Spinner spinner;
    private String[] categories = new String[]{"default", "airport", "amusement_park", "aquarium", "art_gallery", "bakery",
            "bar", "beauty_salon", "bowling_alley", "bus_station", "cafe", "campground", "car_rental", "casino", "lodging",
            "movie_theater", "museum", "night_club", "park", "parking", "restaurant", "shopping_mall", "stadium", "subway_station",
            "taxi_stand", "train_station", "transit_station", "travel_agency", "zoo"};
    private ArrayAdapter<String> adapter;


    private Context mContext;
    Button btnToInterest;
    String keyword;
    String detailLocation;
    double hostLat;
    double hostLng;
    String hostCoordinate = "34.0266,-118.2831";


    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

//    private static final String TAG = "MapActivity";




    public static final String TAG = "AutoCompleteActivity";
    private static final int AUTO_COMP_REQ_CODE = 2;

    protected GeoDataClient geoDataClient;
    private GoogleApiClient mGoogleApiClient;

    public static final String TYPE = "type";


    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -170), new LatLng(71, 136)
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        getLocationPermission();

        btnToInterest = (Button) view.findViewById(R.id.search);
        btnToInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formSubmit(v);
            }
        });

        Button clear = (Button) view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear(v);
            }
        });

        RadioButton radioButton1 = (RadioButton) view.findViewById(R.id.radioButton1);
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        RadioButton radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });


        ((com.seatgeek.placesautocomplete.PlacesAutocompleteTextView) view.findViewById(R.id.detailLocation)).setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {

                    @Override
                    public void onPlaceSelected(@NonNull com.seatgeek.placesautocomplete.model.Place place) {

                    }
                }
        );


        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();


        return view;
    }


    public void formSubmit(View view) {
        EditText keywordView = (EditText) getView().findViewById(R.id.keyword);
        keyword = keywordView.getText().toString().trim();

        com.seatgeek.placesautocomplete.PlacesAutocompleteTextView detailLocationView = (com.seatgeek.placesautocomplete.PlacesAutocompleteTextView) getView().findViewById(R.id.detailLocation);
        detailLocation = detailLocationView.getText().toString().trim();


        if (((RadioButton) getActivity().findViewById(R.id.radioButton2)).isChecked()) {
            if (keyword.length() == 0) {
                getActivity().findViewById(R.id.keywordError).setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Plaese fix all fields with errors", Toast.LENGTH_SHORT).show();
            }
            if (detailLocation.length() == 0) {
                getActivity().findViewById(R.id.detailLocationError).setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Plaese fix all fields with errors", Toast.LENGTH_SHORT).show();
            }
            if (keyword.length() != 0 && detailLocation.length() != 0) {
                getActivity().findViewById(R.id.keywordError).setVisibility(View.GONE);
                getActivity().findViewById(R.id.detailLocationError).setVisibility(View.GONE);
                send();
            }
        } else {
            if (keyword.length() == 0) {
                getActivity().findViewById(R.id.keywordError).setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Plaese fix all fields with errors", Toast.LENGTH_SHORT).show();
            } else {
                getActivity().findViewById(R.id.keywordError).setVisibility(View.GONE);
                getActivity().findViewById(R.id.detailLocationError).setVisibility(View.GONE);
                send();
            }
        }

    }

    public void send() {
        final Intent intent = new Intent(mContext, PlacesActivity.class);

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Fetching Results");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://csci571hw8-env.cxn4etsqhn.us-west-1.elasticbeanstalk.com/getPlaces?";

        EditText distanceView = (EditText) getView().findViewById(R.id.distance);
        String distance = distanceView.getText().toString().trim();
        if (distance.equals("")) {
            distance = "10";
        }

        Spinner spinner = (Spinner) getView().findViewById(R.id.category);
        int pos = spinner.getSelectedItemPosition();
        String category = categories[pos];
        url += "keyword=" + keyword;
        url += "&distance=" + distance;
        url += "&category=" + category;
        url += "&detailLocation=" + detailLocation;
        url += "&hostCoordinates=" + hostCoordinate;
        url = url.replace(" ", "%20");

        //"34.0266,-118.2831"

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                intent.putExtra("places", response.toString());
                Log.i("places", response.toString());
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButton1:
                getActivity().findViewById(R.id.detailLocation).setEnabled(false);
                getActivity().findViewById(R.id.detailLocationError).setVisibility(View.GONE);
                com.seatgeek.placesautocomplete.PlacesAutocompleteTextView detailLocationView = (com.seatgeek.placesautocomplete.PlacesAutocompleteTextView) getView().findViewById(R.id.detailLocation);
                detailLocationView.setText("");
                break;
            case R.id.radioButton2:
                getActivity().findViewById(R.id.detailLocation).setEnabled(true);
                break;

        }
    }

    public void clear(View view) {
        getActivity().findViewById(R.id.keywordError).setVisibility(View.GONE);
        getActivity().findViewById(R.id.detailLocationError).setVisibility(View.GONE);
        EditText keywordView = (EditText) getView().findViewById(R.id.keyword);
        keywordView.setText("");
        com.seatgeek.placesautocomplete.PlacesAutocompleteTextView detailLocationView = (com.seatgeek.placesautocomplete.PlacesAutocompleteTextView) getView().findViewById(R.id.detailLocation);
        detailLocationView.setText("");
        RadioButton radioButton1 = (RadioButton) getView().findViewById(R.id.radioButton1);
        radioButton1.callOnClick();
        radioButton1.setChecked(true);
        EditText distanceView = (EditText) getView().findViewById(R.id.distance);
        distanceView.setText("");
        ((Spinner) getView().findViewById(R.id.category)).setSelection(0, true);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current locationOriginal");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            hostLat = currentLocation.getLatitude();
                            hostLng = currentLocation.getLongitude();
                            hostCoordinate = currentLocation.getLatitude() + "" + currentLocation.getLongitude();
                            Log.d(TAG, currentLocation.getLatitude() + "" + currentLocation.getLongitude() + "");


                        } else {
                            Log.d(TAG, "onComplete: current locationOriginal is null");
                            hostCoordinate = "34.0266,-118.2831";
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
            hostCoordinate = "34.0266,-118.2831";
        }
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                getDeviceLocation();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}