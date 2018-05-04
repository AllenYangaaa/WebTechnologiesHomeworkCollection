package com.bokuyihi.placessearch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {


    MapView mMapView;
    private GoogleMap googleMap;
    JSONObject object;

    double startLatitude;
    double startLongitude;

    double destionationLat = 34.0266;
    double destionationLng = -118.2831;

    String[] mode = {TransportMode.DRIVING,TransportMode.BICYCLING,TransportMode.TRANSIT,TransportMode.WALKING};
    String travelMode = mode[0];
    String currentSpinner = TransportMode.DRIVING;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        object = ((DetailActivity) getActivity()).getInfo();
        try {
            JSONObject geometry = object.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            startLatitude = location.getDouble("lat");
            startLongitude = location.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((com.seatgeek.placesautocomplete.PlacesAutocompleteTextView) view.findViewById(R.id.from)).setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(@NonNull com.seatgeek.placesautocomplete.model.Place place) {
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+place.place_id+"&key=AIzaSyCvMtC4NgCW7MnLtqHC54Gm4VOVoXp5e08";
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        googleMap.clear();
                                        try {
                                            JSONObject address = new JSONObject(response);
                                            JSONObject result = address.getJSONObject("result");
                                            JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                                            destionationLat = location.getDouble("lat");
                                            destionationLng = location.getDouble("lng");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getContext(), "fails", Toast.LENGTH_SHORT).show();
                                        }
                                        LatLng start = new LatLng(startLatitude, startLongitude);
                                        googleMap.addMarker(new MarkerOptions().position(start).title("Location").snippet("Location"));
                                        LatLng destination = new LatLng(destionationLat, destionationLng);
                                        googleMap.addMarker(new MarkerOptions().position(destination).title("Location").snippet("Location"));
                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(destination).zoom(12).build();
                                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                        LatLng origin = new LatLng(startLatitude, startLongitude);
                                        LatLng des = new LatLng(destionationLat, destionationLng);


                                        GoogleDirection.withServerKey("AIzaSyAHJ7WwmFlpVwuRrYqKqY0b6pT68R-JRLI")
                                                .from(origin)
                                                .to(des)
                                                .transportMode(travelMode)
                                                .execute(new DirectionCallback() {
                                                    @Override
                                                    public void onDirectionSuccess(Direction direction, String rawBody) {
                                                        if(direction.isOK()) {
                                                            Route route = direction.getRouteList().get(0);
                                                            Leg leg = route.getLegList().get(0);
                                                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED);
                                                            googleMap.addPolyline(polylineOptions);
                                                        } else {
                                                            Toast.makeText(getContext(),"direction fail",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onDirectionFailure(Throwable t) {
                                                        Toast.makeText(getContext(),"direction fail44444",Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //progressDialog.dismiss();
                                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        queue.add(stringRequest);
                    }
                }
        );



        Spinner spinner = view.findViewById(R.id.travelMode);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                travelMode = mode[position];
                if (currentSpinner == travelMode) {
                    return;
                } else {
                    currentSpinner = travelMode;
                    drawMap();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }



        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                //googleMap.setMyLocationEnabled(true);
                LatLng sydney = new LatLng(startLatitude, startLongitude);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        return view;
    }



    public void drawMap(){
        googleMap.clear();
        LatLng start = new LatLng(startLatitude, startLongitude);
        googleMap.addMarker(new MarkerOptions().position(start).title("Location").snippet("Location"));
        LatLng destination = new LatLng(destionationLat, destionationLng);
        googleMap.addMarker(new MarkerOptions().position(destination).title("Location").snippet("Location"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(destination).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        GoogleDirection.withServerKey("AIzaSyAHJ7WwmFlpVwuRrYqKqY0b6pT68R-JRLI")
                .from(new LatLng(startLatitude, startLongitude))
                .to(new LatLng(destionationLat, destionationLng))
                .transportMode(travelMode)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED);
                            googleMap.addPolyline(polylineOptions);
                        } else {
                            Toast.makeText(getContext(),"direction fail",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(getContext(),"direction fail44444",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
