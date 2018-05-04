const express = require('express');
const app = express();
const path = require('path');
const request = require('request');
const cors = require('cors');
const yelp = require('yelp-fusion');
const client = yelp.client('D-PxlWt3b2pASrxZVjaFNYC2g3U935oZZniEn_82V-sQGn9eYC32l-ddPFly4DzfKpGWEpQwYVFUF2OkavLuVr7U0IPzuMH_TOLhuMRwPNUjVtJ-p7-aLQRLWlzBWnYx');

app.use(cors({origin: '*'}));

app.use(express.static(__dirname + '/dist'));

app.get('/getPlaces',(req, res) => {
    let coordinate;
    if (req.query.detailLocation) {
        const detailLocation = req.query.detailLocation.replace(/' '/g,'+');
        console.log(req.query.detailLocation);
        const geocode_url = 'https://maps.googleapis.com/maps/api/geocode/json?address='+ detailLocation +'&key=AIzaSyDn5wyrop8W1b5c0-x-BXhZsBKU59vloNc';
        request(geocode_url, (err, response, data) => {
            if (err) {
                res.json({success: false});
                console.log(err);
            } else {
                try{
                    if (JSON.parse(data).status !== 'OK') {
                        res.json({success: false});
                    } else {
                        coordinate = JSON.parse(data).results[0].geometry.location;
                        coordinate = coordinate.lat+','+coordinate.lng;
                        console.log("status ok");
                        searchPlaces();
                    }
                } catch(e) {
                    res.json({success: false});
                    console.log("catch error");
                }
            }
        });
    } else {
        coordinate = req.query.hostCoordinates;
        searchPlaces();
    }

    function searchPlaces(){
        let place_url = '';
        let distance = req.query.distance * 1609;
        if (req.query.category === 'default') {
            place_url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='+coordinate+'&radius='+distance+'&keyword='+req.query.keyword+'&key=AIzaSyBB__CBGBQD1czAnNFGu9jEEvZIfZpy6as';
        } else {
            place_url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='+coordinate+'&radius='+distance+'&type='+req.query.category+'&keyword='+req.query.keyword+'&key=AIzaSyBB__CBGBQD1czAnNFGu9jEEvZIfZpy6as';
        }
        request(place_url, (err,response, data) => {
            if (err) {
                res.json({success: false});
                console.log(err);
            } else {
                try{
                    const start = coordinate.split(',');
                    console.log(start[0]);
                    const coor = {lat:start[0], lng: start[1]};
                    console.log(coor);
                    if (data === '') {
                        console.log('No data');
                        res.json({success: false});
                    } else {
                        const returnData = JSON.parse(data);
                        returnData.startLocation = coor;
                        res.json({success: true, places: returnData});
                        console.log(returnData.startLocation);
                    }
                }catch(e){
                    res.json({success: false});
                }               
            }
        });
    };
});

app.get('/getNextPage', (req, res) => {
    const nextPage_url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken='+req.query.token+'&key=AIzaSyCWOZwD2k-NkgeVmHPpWvvAFvVsYZRDLHo';
    request(nextPage_url, (err, response,data) => {
        if (err) {
            res.json({success: false});
        } else {
            try{
                res.json(JSON.parse(data));
            }catch(e){
                res.json({success: false});
            }           
        }
    });
});

app.get('/getDetails', (req, res) => {
    const detail_url = 'https://maps.googleapis.com/maps/api/place/details/json?placeid='+req.body.placeId+'&key=AIzaSyCvMtC4NgCW7MnLtqHC54Gm4VOVoXp5e08';
    request(detail_url, (err,response, data) => {
        if (err) {
            console.log(err);
        } else {
            console.log(req.body.placeId);
            console.log(JSON.parse(data));
            res.json(JSON.parse(data));
        }
    });
});

app.get('/getYelpReviews', (req, res) => {
    console.log('have not match');
    client.businessMatch('best', {
        name: req.query.name,
        address1: req.query.address1,
        city: req.query.city,
        state: req.query.state,
        country: 'US'
    }).then(response => {
        console.log("has match");
        //console.log(response.jsonBody);
        if (!response.jsonBody.businesses[0]) {
            console.log("No Yelp Reviews");
            res.json({hasReview: false});
        } else {
            client.reviews(response.jsonBody.businesses[0].id).then(response => {
                res.json({hasReview: true, reviews: response.jsonBody.reviews});
                console.log(response.jsonBody);
            }).catch(e => {
                res.json({hasReview: false});
            });
        }
    }).catch(e => {
        res.json({hasReview: false});
        console.log(e);
        console.log("did not match");
    });
});

app.get('/getStartLoc',(req,res) => {
    let info = req.query.info.replace(/' '/g,'+');;

    const startLoc_url = 'https://maps.googleapis.com/maps/api/geocode/json?address='+ info +'&key=AIzaSyDohnUv5sH4jjVO3vn6FmXIL-D-IoIMzmg';
    request(startLoc_url, (err, response,data) => {
        if (err) {
            res.json({success: false});
        } else {
            try{
                res.json(JSON.parse(data));
            } catch(e){
                res.json({success: false});
                console.log("catch error");
            }
            
        }
    });
});

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname + '/dist/index.html'));
});

app.listen(8081, (req, res) => {
    console.log('Listening');
});