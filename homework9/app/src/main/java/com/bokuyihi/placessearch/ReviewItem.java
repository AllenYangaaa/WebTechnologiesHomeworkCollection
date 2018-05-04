package com.bokuyihi.placessearch;



public class ReviewItem {

    private String reviewerPhoto;
    private String reviewerTime;
    private String reviewerName;
    private String reviewerRating;
    private String reviewerText;
    private String link;

    public ReviewItem(String reviewerName, String reviewerRating, String reviewerTime, String reviewerPhoto, String reviewerText, String link){
        this.reviewerName = reviewerName;
        this.reviewerTime = reviewerTime;
        this.reviewerPhoto = reviewerPhoto;
        this.reviewerText = reviewerText;
        this.reviewerRating = reviewerRating;
        this.link = link;
    }

    public String getReviewerName(){
        return reviewerName;
    }

    public String getReviewerTime(){
        return reviewerTime;
    }

    public String getReviewerPhoto(){
        return reviewerPhoto;
    }

    public String getReviewerText(){
        return reviewerText;
    }

    public String getReviewerRating(){
        return reviewerRating;
    }

    public String getLink() {
        return link;
    }



}
