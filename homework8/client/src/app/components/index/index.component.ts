import { Component, ElementRef, OnInit, ChangeDetectorRef } from '@angular/core';
import { SlicePipe } from '@angular/common';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import * as moment from 'moment';
import { } from '@types/googlemaps';
import { fadeInAnimation } from '../../animation/fade_in_animation';
import { slideInOutAnimation } from '../../animation/slide_in_animation';
import {HttpErrorResponse} from '@angular/common/http';


@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css'],
  animations: [fadeInAnimation,slideInOutAnimation],
})
export class IndexComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private apiService: ApiService,
    private elementRef: ElementRef,
    private changeDetectorRef: ChangeDetectorRef
  ) {

    this.createForm();
    moment().weekday(0);
  }

  //heightlight
  highDetail = {};
  hasHighDetail = false;

  //animation
  detailState = "void";
  placesState = "hidden";

  // form related
  hostCoordinates;
  form: FormGroup;
  inputDisable = true;
  keywordError;
  blurValidates = true;
  
 

  // places related
  allPlaces;
  placePage1;
  placePage2;
  placePage3;

  //favorite related
  favoritePlaces = [];
  favoriteShoeOrHide;
  favoriteStart = 0;
  favoriteEnd = 20;

  //details related over all
  details;
  detailPlace;

  //info related
  priceLevel;
  openOrClosed;
  dayOfWeek;
  weekday_text;
  starWidth;


  //photos related
  photoCol1 = [];
  photoCol2 = [];
  photoCol3 = [];
  photoCol4 = [];

  //map related
  endLocName;
  mapMode = "Pegman";
  startLocation;
  startLocation1;
  endLocation;
  directionsDisplay = new google.maps.DirectionsRenderer;
  directionsService = new google.maps.DirectionsService;
  autocomplete1;


  //reviews related
  googleOrYelp = 'Google Reviews';
  googleReviews;
  yelpReviews;
  googleOriginReviews = [];
  yelpOriginReviews = [];
  sortKind = 'Default Order';
  googleFadeIn = true;
  yelpFadeIn = false;


 //alert related
  errorShow;


//*ngIf control   ----  use *ngIf show and hide element

  favoritesNoRecordMessage = false;
  placesNoRecordMessage = false;
  photoNoRecordMessage = false;
  reviewsNoRecordMessage = false;
  placesWrongMessage = false;
  showGoogleReviews = false;
  showYelpReviews = false;
  showModal = false;
  showBar = false;
  showFavorites = false;
  showPlaces = false;
  showDetails = false;
  showDetailInfo = false;
  showPhotos = false;
  showMap = false;

  //show info details
  showAddress;
  showPhoneNumber;
  showPrice;
  showRating;
  showGooglePage;
  showWebsite;
  showOpenHour;


  // handle form submit and validate, get the places results
  createForm(){
    this.form = this.formBuilder.group({
      keyword: ['', Validators.compose([Validators.required, this.keywordValidate])],
      category: ['default',],
      distance: ['',],
      detailLocation: ['', Validators.compose([Validators.required, this.detailLocationValidate])],
    });
    this.form.controls['detailLocation'].disable();
  }


  keywordValidate(controls){
    let temp = controls.value.trim();
    if (temp == "") {
      return {keywordValidate:true};
    } else {
      return null;
    }
  }

  detailLocationValidate(controls){
    let temp = controls.value.trim();
    if (temp == "") {
      return {detailLocationValidate: true};
    } else {
      return null;
    }
  }

  getLocalCoordinate(){
    this.form.controls['keyword'].disable();
    this.form.controls['distance'].disable();
    this.form.controls['category'].disable();
    this.elementRef.nativeElement.querySelector('#search').disabled = true;
    this.elementRef.nativeElement.querySelector('#reset').disabled = true;
    this.elementRef.nativeElement.querySelector('#here').disabled = true;
    this.elementRef.nativeElement.querySelector('#location').disabled = true;

    this.apiService.getLocalCoordinate().subscribe(data => {
      this.hostCoordinates = data['lat']+","+data['lon'];
      this.form.controls['keyword'].enable();
      this.form.controls['distance'].enable();
      this.form.controls['category'].enable();
      this.elementRef.nativeElement.querySelector('#reset').disabled = false;
      this.elementRef.nativeElement.querySelector('#here').disabled = false;
      this.elementRef.nativeElement.querySelector('#location').disabled = false;
    });
  }

  clear(){
    this.form.controls['detailLocation'].disable();
    this.googleOrYelp = 'Google Reviews';
    this.googleOriginReviews = [];
    this.yelpOriginReviews = [];
    this.sortKind = 'Default Order';
    this.mapMode = "Pegman";
    this.photoCol1 = [];
    this.photoCol2 = [];
    this.photoCol3 = [];
    this.photoCol4 = [];
    this.detailState = "void";
    this.placesState = "hidden";
    this.highDetail = {};
    this.hasHighDetail = false;
    this.favoriteStart = 0;
    this.favoriteEnd = 20;

    this.favoritesNoRecordMessage = false;
    this.placesNoRecordMessage = false;
    this.photoNoRecordMessage = false;
    this.reviewsNoRecordMessage = false;
    this.placesWrongMessage = false;
    this.showGoogleReviews = false;
    this.showYelpReviews = false;
    this.showModal = false;
    this.showBar = false;
    this.showFavorites = false;
    this.showPlaces = false;
    this.showDetails = false;
    this.showDetailInfo = false;
    this.showPhotos = false;
    this.showMap = false;

    this.showAddress = false;
    this.showPhoneNumber = false;
    this.showPrice = false;
    this.showRating = false;
    this.showGooglePage = false;
    this.showWebsite = false;
    this.showOpenHour = false;
    this.googleFadeIn = true;
    this.yelpFadeIn = false;
    this.favoritesList();
    this.elementRef.nativeElement.querySelector('#pills-home-tab').click();
  }

  formSubmit(){
    this.placesWrongMessage = false;
    this.showPlaces = false;
    this.showBar = true;
    this.detailState =  "void";
    this.placesNoRecordMessage = false;
    this.showDetails = false;
    const info = {
      keyword: this.elementRef.nativeElement.querySelector('#keyword').value,
      distance: this.elementRef.nativeElement.querySelector('#distance').value || 10,
      category: this.elementRef.nativeElement.querySelector('#category').value,
      detailLocation: this.elementRef.nativeElement.querySelector('#detailLocation').value,
      hostCoordinates: this.hostCoordinates,
    };
    console.log(info);
    this.elementRef.nativeElement.querySelector('#pills-home-tab').click();
    this.apiService.getPlaces(info).subscribe(data => {
      this.showBar = false;
      console.log(data);
      if (data['success'] != true) {
        console.log("wrong");
        this.placesWrongMessage = true;
        this.showPlaces = false;
        this.placesNoRecordMessage = false;
      } else {
        console.log(data);
        this.placePage1 = data['places'];
        this.allPlaces = this.placePage1;
        if (data['places'].results.length == 0){
          this.placesNoRecordMessage = true;
          this.showPlaces = false;
        } else {
          this.showPlaces = true;
          this.placesNoRecordMessage = false;
        }
        this.placePage2 = '';
        this.placePage3 = '';
        this.startLocation = data['places'].startLocation;
        this.startLocation1 = this.startLocation;
        //this.placesState = "visible";
      }
    });
  }

  disableInput() {
    this.inputDisable = true;
    this.form.controls['detailLocation'].disable();
    this.elementRef.nativeElement.querySelector('#detailLocation').value = '';
    this.form.controls['detailLocation'].clearValidators();
  }

  enableInput() {
    this.inputDisable = false;
    this.form.controls['detailLocation'].enable();
    this.form.controls['detailLocation'].setValidators([Validators.required, this.detailLocationValidate]);
  }

 
  //发出 next_page_token 的时间与其生效时间之间有短暂延迟。
  getNextPage(){
    if (this.allPlaces == this.placePage1) {
      if (this.placePage2 != '') {
        console.log('old page2');
        this.allPlaces = this.placePage2;
      } else {
        this.showBar = true;
        this.apiService.getNextPage(this.placePage1.next_page_token).subscribe(data => {
          if (data['success'] == false) {
            this.placesWrongMessage = true;
            this.showPlaces = false;
            this.placesNoRecordMessage = false;
          } else {
            console.log('new page2');
            this.showBar = false;
            this.placePage2 = data;
            this.allPlaces = this.placePage2;
          }
        });
      }
    }else if (this.allPlaces == this.placePage2) {
      if (this.placePage3 != '') {
        console.log('old page3');
        this.allPlaces = this.placePage3;
      } else {
        this.showBar = true;
        this.apiService.getNextPage(this.placePage2.next_page_token).subscribe(data => {
          if (data['success'] == false) {
            this.placesWrongMessage = true;
            this.showPlaces = false;
            this.placesNoRecordMessage = false;
          } else {
            console.log('new page3');
            this.showBar = false;
            this.placePage3 = data;
            this.allPlaces = this.placePage3;
          }
        });
      }
    }
    this.changeDetectorRef.markForCheck();
    this.changeDetectorRef.detectChanges();
  }

  getPreviousPage(){
    if (this.allPlaces == this.placePage3) {
      console.log('this is page2');
      this.allPlaces = this.placePage2;
    }else if (this.allPlaces == this.placePage2) {
      console.log('this is page1');
      this.allPlaces = this.placePage1;
    }
    this.changeDetectorRef.markForCheck();
    this.changeDetectorRef.detectChanges();

  }

  getNextFavoritePage(){
    this.favoriteStart = this.favoriteStart + 20;
    this.favoriteEnd = this.favoriteEnd + 20;
  }

  getPreviousFavoritePage(){
    this.favoriteStart = this.favoriteStart - 20;
    this.favoriteEnd = this.favoriteEnd - 20;
  }


  // handle the favorite list
  favoritesList(){
    let obj = JSON.parse(localStorage.getItem('favoriteList')) || {'favoriteList': []};
    if(obj.favoriteList.length == 0) {
      this.showFavorites = false;
      this.favoritesNoRecordMessage = true;
    } else {
      this.showFavorites = true;
      this.favoritePlaces = obj.favoriteList;
      this.favoritesNoRecordMessage = false;
    }
  }

  addRemoveFavorite(place){
    let obj = JSON.parse(localStorage.getItem('favoriteList')) || {'favoriteList': []};
    this.favoritePlaces = obj.favoriteList;
    let index = this.favoritePlaces.findIndex(k => k.id == place.id);
    if (index == -1) {
      this.showFavorites = true;
      this.favoritesNoRecordMessage = false;
      this.favoritePlaces.push(place);
    } else {
      this.favoritePlaces.splice(index,1);
      if (this.favoritePlaces.length == 0) {
        this.showFavorites = false;
        this.favoritesNoRecordMessage = true;
      } else {
        this.showFavorites = true;
        this.favoritesNoRecordMessage = false;
      }
      if (this.favoritePlaces.length % 20 == 0 && this.favoriteStart != 0) {
        this.getPreviousFavoritePage();
      }
    }
    localStorage.setItem('favoriteList',JSON.stringify({'favoriteList':this.favoritePlaces}));
    console.log(this.favoritePlaces);

    this.changeDetectorRef.markForCheck();
    this.changeDetectorRef.detectChanges();
  }

  starStyle(place){
    let index = this.favoritePlaces.findIndex(k => k.id == place.id);
    if (index == -1) {
      return true;
    } else {
      return false;
    }
  }

  // back-end get details

  // getDetails(placeId){
  //   this.showBar = true;
  //   this.apiService.getDetails(placeId).subscribe(data => {
  //     this.showDetails = true;
  //     this.showBar = false;
  //     console.log(data);
  //     this.details = data['result'];
  //     this.reviews = this.details.reviews;
  //   });
  // }




  // get details

  getDetails(place){
    //this.prePage = prePage;
    this.detailPlace = place;
    this.showFavorites = false;
    this.showPlaces = false;
    this.highDetail = place;
    this.hasHighDetail = true;
    this.placesWrongMessage = false;
    // this.showAddress = false;
    // this.showPhoneNumber = false;
    // this.showPrice = false;
    // this.showRating = false;
    // this.showGooglePage = false;
    // this.showWebsite = false;
    // this.showOpenHour = false;
    // this.showModal = false;

    //this.showDetails = true;
    this.showMap = false;

    this.endLocation = place.geometry.location;
    const map = new google.maps.Map(this.elementRef.nativeElement.querySelector('#map'), {
      center: {lat: place.geometry.location.lat, lng: place.geometry.location.lng},
      zoom: 15
    });

    //const infowindow = new google.maps.InfoWindow();
    const service = new google.maps.places.PlacesService(map);

    service.getDetails( {placeId: place.place_id}, (place, status) => {
      if (status === google.maps.places.PlacesServiceStatus.OK) {

        this.details = place;
        this.showDetailInfo = true;


        const marker = new google.maps.Marker({
          map: map,
          position: place.geometry.location
        });

        //address
        if (!this.details.formatted_address) {
          this.showAddress = false;
        } else {
          this.showAddress = true;
        }

        //phone number
        if (!this.details.international_phone_number) {
          this.showPhoneNumber = false;
        } else {
          this.showPhoneNumber = true;
        }

        //price level
        if (!this.details.price_level) {
          this.showPrice = false;
        } else {
          this.showPrice = true;
          this.priceLevel = "";
          for(let i = 0; i < this.details.price_level; i++){
            this.priceLevel += "$"
          }
        }

        //rating
        if (!this.details.rating) {
          this.showRating = false;
        } else {
          this.showRating = true;
          this.starWidth = this.details.rating/5.0*100+'%';
          console.log(this.starWidth);
        }

        //googlePage
        if (!this.details.url) {
          this.showGooglePage = false;
        } else {
          this.showGooglePage = true;
        }

        //website
        if (!this.details.website) {
          this.showWebsite = false;
        } else {
          this.showWebsite = true;
        }

        // open hours
        if (this.details.opening_hours && this.details.opening_hours.weekday_text.length != 0) {

          console.log(this.details.opening_hours.weekday_text.length);
          this.dayOfWeek = moment().utcOffset(this.details.utc_offset).day();
          this.showOpenHour = true;
          this.showModal = true;
          console.log(this.dayOfWeek);
          this.weekday_text = this.details.opening_hours.weekday_text;
          if (this.details.opening_hours.open_now = true) {
            this.openOrClosed = "Open now: " + this.weekday_text[(this.dayOfWeek+6)%7].split(" ")[1];
          } else {
            this.openOrClosed = "Closed ";
          }
        } else {
          this.showOpenHour = false;
          this.showModal = false;
        }

        // photo
        this.photoCol1 = [];
        this.photoCol2 = [];
        this.photoCol3 = [];
        this.photoCol4 = [];
        if (!this.details.photos) {
          console.log("No photos");
          this.photoNoRecordMessage = true;
          this.showPhotos = false;
        } else {
          console.log("Have photos");
          this.photoNoRecordMessage = false;
          this.showPhotos = true;
          for (let i = 0; i < this.details.photos.length; i++) {
          if (i%4 == 0) {
            this.photoCol1.push(this.details.photos[i]);
            } else if (i%4 == 1) {
            this.photoCol2.push(this.details.photos[i]);
            } else if (i%4 == 2) {
            this.photoCol3.push(this.details.photos[i]);
            } else {
            this.photoCol4.push(this.details.photos[i]);
            }
          }
        }

        //map input init
        this.showMap = true;
        this.endLocName = this.details.name + ", " + this.details.formatted_address;

        // google or yelp reviews
        this.showGoogleReviews = true;
        this.showYelpReviews = false;

        this.googleReviews = [];
        this.googleReviews = this.details.reviews;
        if(!this.googleReviews) {
          this.reviewsNoRecordMessage = true;
          this.showGoogleReviews = false;
        } else {
          this.reviewsNoRecordMessage = false;
          this.showGoogleReviews = true;
          for(let i = 0; i < this.googleReviews.length; i++) {
            let reviewRating = "";
            for(let j = 0; j < this.googleReviews[i].rating; j++) {
              reviewRating += "★";
            }
            let reviewTime;
            reviewTime = parseInt(this.googleReviews[i].time) * 1000;
            this.googleReviews[i].timeFormat = moment(reviewTime).format("YYYY-MM-DD hh:mm:ss");
            this.googleReviews[i].ratingStar = reviewRating;
          }
        }
        if (this.googleReviews) {
          this.googleOriginReviews = JSON.parse(JSON.stringify(this.googleReviews));
        }
        this.yelpReviews = [];
        this.showBar = true;
        this.apiService.getYelpReviews(this.details).subscribe(data => {
          this.showBar = false;
          if (data['hasReview']) {
            this.yelpReviews = data['reviews'];
            console.log("have got yelpReview");
            console.log(this.yelpReviews);
            this.showYelpReviews = false;
            if(this.yelpReviews) {
              for(let i = 0; i < this.yelpReviews.length; i++) {
                let reviewRating = "";
                for(let j = 0; j < this.yelpReviews[i].rating; j++) {
                  reviewRating += "★";
                }
              this.yelpReviews[i].ratingStar = reviewRating;
              this.yelpReviews[i].time = moment(this.yelpReviews[i].time_created).format("X");
              }
            } else {
              this.showYelpReviews = false;
            }
            if (this.yelpReviews) {
              this.yelpOriginReviews = JSON.parse(JSON.stringify(this.yelpReviews));;
            }
          }else {
            console.log('No yelp review');
          }
        });
        //this.elementRef.nativeElement.querySelector('#favoriteInfo').click();
        this.detailState = "visible";
        this.placesState = 'hidden';

        this.showDetails = true;
        //this.elementRef.nativeElement.querySelector('#favoriteInfo').click();
        //this.showPlaces = false;
        this.showBar = false;

        this.changeDetectorRef.markForCheck();
        this.changeDetectorRef.detectChanges();

        this.autocomplete1 = new google.maps.places.Autocomplete(this.elementRef.nativeElement.querySelector('#startLoc'), {
          types: ["address"]
        });
        //this.elementRef.nativeElement.querySelector('#favoriteReview').click();
        this.elementRef.nativeElement.querySelector('#favoriteInfo').click();
        this.elementRef.nativeElement.querySelector('#googleReviewButton').click();
        this.elementRef.nativeElement.querySelector('#defaultReviewButton').click();
        console.log(this.details);
        console.log(this.details.reviews);
      } else {
        this.placesWrongMessage = true;
      }
    });
  }

  goPrePage(){
    //this.placesState = "hidden";
    console.log("go previous page");


    //fix
    // this.showDetailInfo = false;
    // this.showPhotos = false;
    // this.showMap = false;




    if (this.allPlaces) {
      this.showPlaces = true;
    }
    this.showFavorites = true;
    this.showDetails = false;
    this.detailState = 'void';
    // showAddress
    // showPhoneNumber
    // showPrice
    // showRating
    // showGooglePage
    // showWebsite
    //this.detailState = "void";
    //this.placesState = "visible";
    //console.log("delay");
    //setTimeout(()=>{ console.log("delay") }, 500)
    console.log(this.showFavorites);
    this.placesState = "show";

    this.changeDetectorRef.markForCheck();
    this.changeDetectorRef.detectChanges();
    // if (this.prePage == 'places') {
    //   this.showPlaces = true;
    //   this.showDetails = false;
    //   this.detailState = 'void';
    // } else if(this.prePage == 'favorites') {
    //   this.showPlaces = true;
    //   this.showDetails = false;
    //   this.detailState = 'void';
    // }
    //
    console.log(this.placesState);
    console.log(this.placesState);
  }

  handleTwitter(){
     let text = `Check+out+${this.details.name}+located+at+${this.details.formatted_address}. Website: ${this.details.website || this.details.url}&via=TravelAndEntertainmentSearch`;
     window.open("https://twitter.com/intent/tweet?text="+text,"Tweet","width=400,height=400")
  }

  showHightLightDetail(){
    this.showDetails = true;
    this.detailState = "visible";
    this.showFavorites = false;
    this.showPlaces = false;
    this.placesState = "void";

    this.changeDetectorRef.markForCheck();
    this.changeDetectorRef.detectChanges();
  }


  googleOrYelpReview(googleOrYelp){
    if (googleOrYelp == 1) {
      this.googleOrYelp = "Google Reviews";
      this.showYelpReviews = false;
      //this.googleFadeIn = false;
      this.googleFadeIn = true;

      this.yelpFadeIn = false;

      if (this.googleReviews[0]) {
        this.showGoogleReviews = true;
        //this.reviews = this.googleReviews;
        this.reviewsNoRecordMessage = false;
      } else {
        this.reviewsNoRecordMessage = true;
      }
    } else {
      this.googleOrYelp = "Yelp Reviews";
      this.showGoogleReviews = false;

      this.googleFadeIn = false;
      //this.yelpFadeIn = false;
      this.yelpFadeIn = true;

      if (this.yelpReviews[0]) {
        this.showYelpReviews = true;
        this.reviewsNoRecordMessage = false;
      } else {
        this.reviewsNoRecordMessage = true;
      }
    }
  }

  sorting(code){
    if (code == 1) {
      console.log("1");
      this.sortKind = 'Default Order';
      this.googleReviews = [];
      this.yelpReviews = [];
      console.log(this.yelpOriginReviews.length);
      for(let i = 0; i < this.yelpOriginReviews.length; i++) {
        this.yelpReviews.push(this.yelpOriginReviews[i]);
      }
      for(let i = 0; i < this.googleOriginReviews.length; i++) {
        this.googleReviews.push(this.googleOriginReviews[i]);
      }
    }
    if (code == 2) {
      console.log("2");
      this.sortKind = 'Highest Order';
      console.log(this.showGoogleReviews);
      console.log(this.showYelpReviews);
      this.googleReviews.sort(this.highestRatingOrTime('rating'));
      this.yelpReviews.sort(this.highestRatingOrTime('rating'));
    }
    if (code == 3) {
      console.log("3");
      this.sortKind = 'Lowest Order';
      this.googleReviews.sort(this.lowestRatingOrTime('rating'));
      this.yelpReviews.sort(this.lowestRatingOrTime('rating'));
    }
    if (code == 4) {
      console.log("4");
      this.sortKind = 'Most Recent';
      this.googleReviews.sort(this.highestRatingOrTime('time'));
      this.yelpReviews.sort(this.highestRatingOrTime('time'));
    }
    if (code == 5) {
      console.log("5");
      this.sortKind = 'Least Recent';
      this.googleReviews.sort(this.lowestRatingOrTime('time'));
      this.yelpReviews.sort(this.lowestRatingOrTime('time'));
    }
  }

  highestRatingOrTime(property){
    return function(obj1,obj2){
      var value1 = obj1[property];
      var value2 = obj2[property];
      return value2 - value1;
    }
  }

  lowestRatingOrTime(property){
    return function(obj1,obj2){
      var value1 = obj1[property];
      var value2 = obj2[property];
      return value1 - value2;
    }
  }



  getRoute(){
    console.log(this.endLocation);
    console.log(this.startLocation);
    //let place: google.maps.places.PlaceResult = this.autocomplete1.getPlace();
    //let place = this.autocomplete1.getPlace();
    //console.log(place.geometry);
    // if (place.geometry) {
    //   this.startLocation = place.geometry.location;
    // }
    //console.log(place.geometry);
    //this.startLocation = place.geometry.location;

    let startL = this.elementRef.nativeElement.querySelector('#startLoc').value;
    if (startL != "" && startL != "My Location") {
      console.log(startL);
      this.showBar = true;
      this.apiService.getStartLoc(startL).subscribe(data => {
        if (data['success'] == false) {
          this.placesWrongMessage = true;
          this.showDetails = false;
        }else{
          console.log(data);
          this.showBar = false;
          if (data['status'] == "OK") {
            this.startLocation = data['results'][0].geometry.location;
            console.log(data['results'][0].geometry.location);
            const map = new google.maps.Map(this.elementRef.nativeElement.querySelector('#map'), {
              center: {lat: parseFloat(this.endLocation.lat), lng: parseFloat(this.endLocation.lng)},
              zoom: 15
            });
            this.directionsDisplay.setMap(map);
            this.directionsDisplay.setPanel(this.elementRef.nativeElement.querySelector('#panel'));
            this.calculateAndDisplayRoute(this.directionsService, this.directionsDisplay);
          }
        }
      });
    }else {
      this.startLocation = this.startLocation1;
      const map = new google.maps.Map(this.elementRef.nativeElement.querySelector('#map'), {
        center: {lat: parseFloat(this.endLocation.lat), lng: parseFloat(this.endLocation.lng)},
        zoom: 15
      });
      this.directionsDisplay.setMap(map);
      this.directionsDisplay.setPanel(this.elementRef.nativeElement.querySelector('#panel'));
      this.calculateAndDisplayRoute(this.directionsService, this.directionsDisplay);
    }

  }

  calculateAndDisplayRoute(directionsService, directionsDisplay){
    console.log("display route begin");

    const selectedMode = this.elementRef.nativeElement.querySelector('#method').value;
    console.log(this.startLocation);
    const request = {
      origin: {lat: parseFloat(this.startLocation.lat), lng: parseFloat(this.startLocation.lng)},
      destination: {lat: parseFloat(this.endLocation.lat), lng: parseFloat(this.endLocation.lng)},
      travelMode: google.maps.TravelMode[selectedMode],
      provideRouteAlternatives: true
    };
    directionsService.route(request, function(response, status) {
      if (status == 'OK') {
        directionsDisplay.setDirections(response);
        console.log("display route finish");
      } else {
        window.alert('Directions request failed due to ' + status);
      }
    });
  }

  changeMapMode(){
    if (this.mapMode == "Pegman") {
      this.mapMode = "Map";
      const map = new google.maps.Map(this.elementRef.nativeElement.querySelector('#map'), {
        center: {lat: parseFloat(this.endLocation.lat), lng: parseFloat(this.endLocation.lng)},
        zoom: 14
      });
      const panorama = new google.maps.StreetViewPanorama(
        this.elementRef.nativeElement.querySelector('#map'), {
          position: {lat: parseFloat(this.endLocation.lat), lng: parseFloat(this.endLocation.lng)},
          pov: {
            heading: 34,
            pitch: 10
          }
        });
      map.setStreetView(panorama);
    }else {
      this.mapMode = "Pegman";
      const map = new google.maps.Map(this.elementRef.nativeElement.querySelector('#map'), {
        center: {lat: parseFloat(this.endLocation.lat), lng: parseFloat(this.endLocation.lng)},
        zoom: 15
      });
      const marker = new google.maps.Marker({
          map: map,
          position: {lat: parseFloat(this.endLocation.lat), lng: parseFloat(this.endLocation.lng)}
        });
      this.directionsDisplay.setMap(map);
    }
    this.changeDetectorRef.markForCheck();
    this.changeDetectorRef.detectChanges();
  }


  ngOnInit() {
    let autocomplete = new google.maps.places.Autocomplete(this.elementRef.nativeElement.querySelector('#detailLocation'), {
      types: ["address"]
    });
    this.getLocalCoordinate();
    this.favoritesList();
  }

}












