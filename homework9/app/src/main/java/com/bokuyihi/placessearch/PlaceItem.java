package com.bokuyihi.placessearch;


public class PlaceItem {


    private String address;
    private String name;
    private String icon;
    private String placeId;


    public PlaceItem(String name, String address, String image, String placeId){
        this.name = name;
        this.address = address;
        this.icon = image;
        this.placeId = placeId;

    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public String getIcon(){
        return icon;
    }

    public String getPlaceId(){
        return placeId;
    }



}
