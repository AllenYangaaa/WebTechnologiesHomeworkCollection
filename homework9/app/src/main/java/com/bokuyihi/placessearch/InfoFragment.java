package com.bokuyihi.placessearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class InfoFragment extends Fragment {

    View view;
    JSONObject object;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.info_fragment, container, false);

        object = ((DetailActivity) getActivity()).getInfo();
        handleInfo();

        return view;
    }


    public void handleInfo(){

        try {
            TextView infoAddress = view.findViewById(R.id.infoAddress);
            infoAddress.setText(object.getString("formatted_address"));
        } catch (JSONException e) {
            view.findViewById(R.id.infoAddressLayout).setVisibility(View.GONE);
        }


        try {
            TextView infoPhone = view.findViewById(R.id.infoPhone);
            infoPhone.setText(object.getString("formatted_phone_number"));
        } catch (JSONException e) {
            view.findViewById(R.id.infoPhoneLayout).setVisibility(View.GONE);
        }


        try {
            TextView infoPrice = view.findViewById(R.id.infoPrice);
            int num = object.getInt("price_level");
            String price = "";
            for (int i = 0; i < num; i++) {
                price += "$";
            }
            infoPrice.setText(price);
        } catch (JSONException e) {
            view.findViewById(R.id.infoPriceLayout).setVisibility(View.GONE);
        }

        try {
            RatingBar infoRating = view.findViewById(R.id.infoRating);
            Float rate = new Float(object.getString("rating"));
            infoRating.setRating(rate);
        } catch (JSONException e) {
            view.findViewById(R.id.infoRatingLayout).setVisibility(View.GONE);
        }


        try {
            TextView infoGooglePage = view.findViewById(R.id.infoGooglePage);
            infoGooglePage.setText(object.getString("url"));
        } catch (JSONException e) {
            view.findViewById(R.id.infoGooglePageLayout).setVisibility(View.GONE);
        }


        try {
            TextView infoWebsite = view.findViewById(R.id.infoWebsite);
            infoWebsite.setText(object.getString("website"));
        } catch (JSONException e) {
            view.findViewById(R.id.infoWebsitelayout).setVisibility(View.GONE);
        }

    }


}
