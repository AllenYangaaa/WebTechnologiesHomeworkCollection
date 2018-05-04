import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import 'rxjs/add/operator/map';


@Injectable()
export class ApiService {

  domain = 'http://Csci571hw8-env.cxn4etsqhn.us-west-1.elasticbeanstalk.com/';

  constructor(
    private httpClient: HttpClient,
  ) { }

  getLocalCoordinate(){
    return this.httpClient.get('http://ip-api.com/json').map(res => res);
  }

  getPlaces(info){
    return this.httpClient.get(this.domain + 'getPlaces', {params:info}).map(res => res);s
  }

  getNextPage(token){
    return this.httpClient.get(this.domain + 'getNextPage',{params:{token: token}}).map(res => res);
  }

  getYelpReviews(detail){
    let info = {
      name: detail.name,
      address1: detail.vicinity.split(",")[0],
      city: "Los Angeles",
      state: "CA",
    };
    for (let i = 0; i < detail.address_components.length; i++) {
      if (detail.address_components[i].types[0] == "administrative_area_level_1") {
        info.state = detail.address_components[i].short_name;
      } else if (detail.address_components[i].types[0] == "administrative_area_level_2") {
        info.city = detail.address_components[i].short_name;
      }
    }
    return this.httpClient.get(this.domain + 'getYelpReviews', {params:info}).map(res => res);
  }

  getStartLoc(info){
    console.log(info);
    return this.httpClient.get(this.domain + 'getStartLoc', {params:{info: info}}).map(res => res);
  }
  // getDetails(placeId){
  //   const id = {
  //     placeId: placeId
  //   };
  //   return this.httpClient.post(this.domain + 'getDetails', id).map(res => res);
  // }
}
