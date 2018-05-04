<?php if (array_key_exists('keyword', $_POST)):?>
<?php
    $keyword = $category = $distance = $hostCoordinates = $detailLocation = $coordinates = $place_info = '';
    if (!empty($_POST["keyword"])) {
        $keyword = $_POST["keyword"];
    }
    if (!empty($_POST["category"])) {
        $category = $_POST["category"];
    }
    if (!empty($_POST["distance"])) {
        $distance = $_POST["distance"]*1609;
        if ($distance > 50000) {
            $distance = 50000;
        }
    }
    if (!empty($_POST["hostCoordinates"])) {
        $hostCoordinates = $_POST["hostCoordinates"];
    }
    if (!empty($_POST["detailLocation"])) {
        $detailLocation = $_POST["detailLocation"];
    }

    if ($detailLocation !== '') {
        $detailLocation = str_replace([' ', ','],'+', $detailLocation);
        $geocode_url = 'https://maps.googleapis.com/maps/api/geocode/json?address='.$detailLocation.'&key=AIzaSyDn5wyrop8W1b5c0-x-BXhZsBKU59vloNc';
        $location_data = json_decode(file_get_contents($geocode_url));

        if ($location_data->status === 'OK') {
            $coordinates = $location_data->results[0]->geometry->location;
            $coordinates = $coordinates->lat.','.$coordinates->lng;
        } else {
            return '';
        }
    }else {
        $coordinates = $hostCoordinates;
    }
    if ($category == 'default') {
        $place_url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='.$coordinates.'&radius='.$distance.'&keyword='.$keyword.'&key=AIzaSyBB__CBGBQD1czAnNFGu9jEEvZIfZpy6as';
    } else {
        $place_url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='.$coordinates.'&radius='.$distance.'&type='.$category.'&keyword='.$keyword.'&key=AIzaSyBB__CBGBQD1czAnNFGu9jEEvZIfZpy6as';
    }
    $place_info = file_get_contents($place_url);

    $start = explode(',',$coordinates);
    $lat = $start[0];
    $lng = $start[1];

    if (!empty($place_info)){
        $place_info = json_decode($place_info);
        $startLocation = array("lat"=> $lat,"lng"=> $lng);
        $place_info -> startLocation = $startLocation;
        $place_info = json_encode($place_info);
    }

    echo $place_info;

?>
<?php elseif (array_key_exists('placeId', $_POST)):?>
<?php
    $placeId = '';
    if (!empty($_POST["placeId"])) {
        $placeId = $_POST["placeId"];
    }
    $detail_url = 'https://maps.googleapis.com/maps/api/place/details/json?placeid='.$placeId.'&key=AIzaSyCvMtC4NgCW7MnLtqHC54Gm4VOVoXp5e08';
    $place_detail = json_decode(file_get_contents($detail_url));
    if (isset($place_detail->result->photos)){
        for($i = 0; $i < count($place_detail->result->photos); $i++) {
            if($i < 5) {
                $photo_url = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth='.$place_detail->result->photos[$i]->width.'&photoreference='.$place_detail->result->photos[$i]->photo_reference.'&key=AIzaSyBShi-yd62eibKRTQIpnEKdwFdNo3wa-2Q';
                $photo = file_get_contents($photo_url);
                file_put_contents($i.'.png',$photo);
//                $place_detail->result->photos[$i]->photo_reference
            }
        }
    }
    echo json_encode($place_detail);
?>
<?php else:?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Google Place</title>
    <style>
        .mainPage{ width: 500px; height: 170px; border: 4px solid #DDDDDD; overflow: auto; margin: auto;}
        #form{padding-left: 10px}
        .title{ font-size: 25px; text-align: center; font-style: italic; padding-top:10px }
        .button{padding-left: 60px}
        .distance {float: left;display:block;}
        table, th, td {border:1px solid black;border-collapse: collapse;}table {overflow: auto; margin: auto;}
        i {border: solid #D3D3D3;border-width: 0 5px 5px 0;display: inline-block;padding: 6px;}
        .down {transform: rotate(45deg);-webkit-transform: rotate(45deg);}
        .up {transform: rotate(-135deg);-webkit-transform: rotate(-135deg);}
        #map{height:275px; width:375px}
        #btn1:hover,#btn2:hover,#btn3:hover{background-color: #D3D3D3}
        .hereOrThere{display:inline-block;}
    </style>
</head>
<body>
	<div class="mainPage">
        <div class="title">
            Travel and Entertainment Search
        </div>
        <hr width="480px"/>
        <div>
            <form id="form">
                <strong>Keyword</strong><input type="text" id="keyword" name="keyword" required><br>
                <strong>Category</strong>
                <select name="category" id="category" value="default">
                    <option value="default">default</option>
                    <option value="cafe">cafe</option>
                    <option value="bakery">bakery</option>
                    <option value="restaurant">restaurant</option>
                    <option value="beauty_salon">beauty salon</option>
                    <option value="casino">casino</option>
                    <option value="movie_theater">movie theater</option>
                    <option value="lodging">lodging</option>
                    <option value="airport">airport</option>
                    <option value="train_station">train station</option>
                    <option value="subway_station">subway station</option>
                    <option value="bus_station">bus station</option>
                </select><br>
                <div>
                    <div class="distance">
                        <strong>Distance(miles)</strong>
                        <input type="text" id="distance" name="distance" placeholder="10" value="10">
                        <strong>from</strong>
                    </div>
                    <div class="hereOrThere">
                        <input type="radio" id="here" name="location" value="here" checked="true" onclick="disableInput(this.form)">Here<br>
                        <input type="radio" id="location" name="location" value="anywhere" onclick="enableInput(this.form)">
                        <input type="text" name="detailLocation" id="detailLocation" placeholder="location" disabled="disabled" required>
                    </div>
                </div>
                <div class="button" style="display: inline">
                    <input type="submit" value="search" id="search" name="search" >
                    <input type="reset" id="reset" value="Clear">
                </div>
            </form>
        </div>
	</div>
    <br>
    <div id="btn" style="display:none;z-index:3;position:absolute;background-color:#E3E3E3;">
        <div id="btn1" method="WALKING" style="height:33px;line-height:33px;padding:0px 5px" onclick="initMap2(this)" destinationLat="" destinationLng="">Walk there</div>
        <div id="btn2" method="BICYCLING" style="height:33px;line-height:33px;padding:0px 5px" onclick="initMap2(this)" destinationLat="" destinationLng="">Bike there</div>
        <div id="btn3" method="DRIVING" style="height:33px;line-height:33px;padding:0px 5px" onclick="initMap2(this)" destinationLat="" destinationLng="">Drive there</div>
    </div>
    <div id="show"></div>
    <div id="map" style="display:none;z-index:2;position:absolute;">
	<script >

        //get the host ip
        function getHostIp() {
            if (window.XMLHttpRequest) {
                var xmlhttp = new XMLHttpRequest();
            } else {
                var xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            xmlhttp.open("GET","http://ip-api.com/json",false);
            xmlhttp.send();
            var hostIp = xmlhttp.responseText;
            if (hostIp != "") {
                document.getElementById('search').disabled = false;
            }
            return hostIp;
        }
        document.getElementById('search').disabled = true;
        var hostIpData = JSON.parse(getHostIp());
        var hostCoordinates = hostIpData.lat + ',' + hostIpData.lon;

        if (hostIpData == null) {
            document.getElementById('search').disabled = true;
        }


        // extra behavior when reset the form
        document.getElementById("reset").addEventListener("click", function (ev) {
            var form = document.getElementById("form");
            form.detailLocation.disabled = true;

            document.getElementById("here").checked = "true";
            document.getElementById('show').innerHTML = "";
            document.getElementById("map").style.display = "none";
            document.getElementById("btn").style.display = "none";
        });

        var startLocationLat;
        var startLocationLng;
        document.getElementById("form").addEventListener("submit", function (ev) {
            ev.preventDefault();
            document.getElementById("map").style.display = "none";
            document.getElementById("btn").style.display = "none";
            var form = document.getElementById("form");
            var category = form.category.value;
            var distance = form.distance.value;
            if (distance > 30) {
                distance = 30;
                document.getElementById("distance").value = 30;
            }
            var keyword = form.keyword.value;
            keyword = keyword.replace(/^\s+|\s+$/g,"");
            keyword = keyword.replace(/\s+|\s+/g,",");
            keyword = keyword.replace("+",",");

            var detailLocation = form.detailLocation.value;
            detailLocation = detailLocation.replace(/^\s+|\s+$/g,"");

            //Ajax提交表单
            var postStr = "keyword="+keyword+"&category="+category+"&distance="+distance+
                "&hostCoordinates="+hostCoordinates+"&detailLocation="+detailLocation;
            if (window.XMLHttpRequest) {
                var xmlhttp = new XMLHttpRequest();
            } else {
                var xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            xmlhttp.open("POST","./place.php",true);
            xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
            xmlhttp.send(postStr);

            // var placeData;

            xmlhttp.onreadystatechange = function(){
                if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                    var placeData = xmlhttp.responseText;
                    //console.log(placeData);
                    if (placeData == ""){
                        document.getElementById('show').innerHTML = "<div style=\"text-align:center;background-color:#E3E3E3; width:650px;overflow: auto; margin: auto\">No Records has been found</div>";
                    } else {
                        var placeInfo = JSON.parse(placeData);
                        console.log("placeInfo");
                        console.log(placeInfo);
                        startLocationLat = parseFloat(placeInfo.startLocation.lat);
                        startLocationLng = parseFloat(placeInfo.startLocation.lng);
                        console.log(startLocationLat);
                        console.log(startLocationLng);
                        handlePlaceInfo(placeInfo);
                    }
                }
            };

        });

        function handlePlaceInfo(data){
            var place = data.results;
            var html = "";
            if (data.status === "OK") {
                html += "<table style=\"min-width: 1000px;\"><tr><th>Category</th><th>Name</th><th>Address</th></tr>";
                for (var i = 0; i < place.length; i++) {
                    html += "<tr>";
                    html += "<td ><div style=\"height:30px;line-height:30px\"><img style=\"width:40px;height:30px;padding:0px;margin:0px;\" src="+place[i].icon+" alt=\"icon\" /></div></td>";
                    html += "<td><div style=\"height:30px;line-height:30px;padding-left:15px\" id=\"placeid\" onclick=\"showDetail(this)\" placeid="+place[i].place_id+">"+place[i].name+"</div></td>";
                    html += "<td><div style=\"height:30px;line-height:30px;padding-left:15px\" id=\""+i+"\" onclick=\"mapOrNot(this)\" map=\"false\" lat=\""+place[i].geometry.location.lat+"\" lng=\""+place[i].geometry.location.lng+"\">"+place[i].vicinity+"</div></td>";
                    html += "</tr>";
                }
                html += "</table>";
            } else {
                html = "<div style=\"text-align:center;background-color:#E3E3E3; width:650px;overflow: auto; margin: auto\">No Records has been found</div>";
            }

            document.getElementById('show').innerHTML = html;
        }



        function showDetail(place) {
            document.getElementById("map").style.display = "none";
            document.getElementById("btn").style.display = "none";
            var placeId = place.attributes.placeid.value;
            console.log(placeId);
            if (window.XMLHttpRequest) {
                var xmlhttp = new XMLHttpRequest();
            } else {
                var xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            placeId = "placeId=" + placeId;
            xmlhttp.open("POST","./place.php",true);
            xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
            xmlhttp.send(placeId);
            console.log(placeId);
            // var detailData;
            xmlhttp.onreadystatechange = function(){
                if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                    var detailData = xmlhttp.responseText;
                    // console.log(detailData);
                    var detailInfo = JSON.parse(detailData);
                    console.log("detailInfo");
                    console.log(detailInfo);
                    handleDetail(detailInfo);
                }
            };
        }

        function handleDetail(detail) {
            console.log(detail);
            var html = "";
            html += "<div style=\"font-size:20px; text-align:center;\"><strong>"+detail.result.name+"</strong></div><br>";
            html += "<div style=\"text-align:center;\" id=\"showReviews\" onclick=\"showReview()\">click to show reviews<br><i class=\"down\"></i></div>";
            html += "<div style=\"text-align:center;display:none;\" id=\"hideReviews\" onclick=\"hideReview()\">click to hide reviews<br><i class=\"up\"></i></div>";
            html += "<div style=\"display:none;\" id=\"reviews\"><table style=\"width:500px;\">";
            if (detail.result.hasOwnProperty('reviews')) {
                for (var i = 0; i < detail.result.reviews.length; i++) {
                    if (i < 5) {
                        html += "<tr style=\"min-height: 30px;\"><td><div style=\"text-align:center;\">";
                        if (detail.result.reviews[i]["profile_photo_url"] != undefined || detail.result.reviews[i].profile_photo_url == "") {
                            html += "<img style=\"width:30px;height:30px;padding:0px;margin:0px;\" src="+detail.result.reviews[i].profile_photo_url+" alt=\"photo\" />";
                        }
                        html += "<strong>"+detail.result.reviews[i].author_name+"<strong></div></td></tr>";
                        html += "<tr><td><div>"+detail.result.reviews[i].text+"&nbsp;</div></td></tr>"
                    }
                }
            } else {
                html += "<tr><td><div style=\"text-align:center;width:500px;overflow: auto; margin: auto\"><strong>No Reviews Found</strong></div></td></tr>";
            }
            html += "</table></div><br>";
            html += "<div style=\"text-align:center;\" id=\"showPhotos\" onclick=\"showPhotos()\">click to show photos<br><i class=\"down\"></i></div>";
            html += "<div style=\"text-align:center;display:none;\" id=\"hidePhotos\" onclick=\"hidePhotos()\">click to hide photos<br><i class=\"up\"></i></div>";
            html += "<div style=\"display:none;\" id=\"photos\"><table style=\"width:500px;\">";
            if (detail.result.hasOwnProperty('photos')) {
                for (var i = 0; i < detail.result.photos.length; i++) {
                    if (i < 5) {
                        html += "<tr><td><div><a href=\"./"+i+".png?a="+detail.result.photos[i].photo_reference+"\" target=\"_blank\"><img style=\"width:490px;padding:10px;\" src=\"./"+i+".png?a="+detail.result.photos[i].photo_reference+"\" alt=\"pho\" /></a></div></td></tr>";
                    }
                }
            } else {
                html += "<tr><td><div style=\"text-align:center;width:500px;overflow: auto; margin: auto\"><strong>No Photos Found</strong></div></td></tr>";
            }
            html += "</table></div>";
            document.getElementById('show').innerHTML = html;
        }


        function showReview() {
            document.getElementById("showReviews").style.display = "none";
            document.getElementById("hideReviews").style.display = "";
            document.getElementById("reviews").style.display = "";
            hidePhotos();
        }

        function showPhotos() {
            document.getElementById("showPhotos").style.display = "none";
            document.getElementById("hidePhotos").style.display = "";
            document.getElementById("photos").style.display = "";
            hideReview();
        }

        function hideReview() {
            document.getElementById("showReviews").style.display = "";
            document.getElementById("hideReviews").style.display = "none";
            document.getElementById("reviews").style.display = "none";
        }

        function hidePhotos() {
            document.getElementById("showPhotos").style.display = "";
            document.getElementById("hidePhotos").style.display = "none";
            document.getElementById("photos").style.display = "none";
        }

        function enableInput(form) {
            form.detailLocation.disabled = false;
        }
        function disableInput(form) {
            form.detailLocation.disabled = true;
            form.detailLocation.value = "";
        }

        function getElementLeft(element){
            var actualLeft = element.offsetLeft;
            var current = element.offsetParent;
            if (current != null){
                actualLeft += getElementLeft(current);
            }
            return actualLeft;
        }

        function getElementTop(element){
            var actualTop = element.offsetTop;
            var current = element.offsetParent;
            if (current != null){
                actualTop += getElementTop(current);
            }
            return actualTop;
        }

        function mapOrNot(ziji) {
            if (ziji.attributes.map.value == "false") {
                ziji.attributes.map.value = "true";
                showMap(ziji);
            } else {
                ziji.attributes.map.value = "false";
                hideMap();
            }
        }

        function initMap2(method) {
            console.log(method);
            var directionsDisplay = new google.maps.DirectionsRenderer;
            var directionsService = new google.maps.DirectionsService;
            var map = new google.maps.Map(document.getElementById('map'), {
                zoom: 14,
                center: {lat: startLocationLat, lng: startLocationLng}
            });
            directionsDisplay.setMap(map);

            calculateAndDisplayRoute(directionsService, directionsDisplay,method);
            method.addEventListener('change', function() {
                calculateAndDisplayRoute(directionsService, directionsDisplay,method);
            });
        }

        function calculateAndDisplayRoute(directionsService, directionsDisplay,method) {
            var selectedMode = method.attributes.method.value;
            console.log(method.attributes.method.value);
            console.log(method.attributes.destinationLng.value);
            var destinationLat = parseFloat(method.attributes.destinationLat.value);
            var destinationLng = parseFloat(method.attributes.destinationLng.value);
            console.log(destinationLat);
            directionsService.route({
                origin: {lat: startLocationLat, lng: startLocationLng},
                destination: {lat: destinationLat, lng: destinationLng},
                travelMode: google.maps.TravelMode[selectedMode]
            }, function(response, status) {
                if (status == 'OK') {
                    directionsDisplay.setDirections(response);
                } else {
                    window.alert('Directions request failed due to ' + status);
                }
            });
        }

        function showMap(text) {
            console.log(text);
            var seatX = getElementLeft(text) + 15;
            var seatY = getElementTop(text) + 24;

            document.getElementById("map").style.left = seatX+"px";
            document.getElementById("map").style.top = seatY+"px";
            document.getElementById("map").style.display = "block";

            document.getElementById("btn").style.left = seatX+"px";
            document.getElementById("btn").style.top = seatY+"px";
            document.getElementById("btn").style.display = "block";

            console.log(seatX+","+seatY);

            document.getElementById("btn1").attributes.destinationLat.value = text.attributes.lat.value;
            document.getElementById("btn1").attributes.destinationLng.value = text.attributes.lng.value;
            document.getElementById("btn2").attributes.destinationLat.value = text.attributes.lat.value;
            document.getElementById("btn2").attributes.destinationLng.value = text.attributes.lng.value;
            document.getElementById("btn3").attributes.destinationLat.value = text.attributes.lat.value;
            document.getElementById("btn3").attributes.destinationLng.value = text.attributes.lng.value;

            initMap(text);
        }
        
        function hideMap() {
            document.getElementById("map").style.display = "none";
            document.getElementById("btn").style.display = "none";
        }

        function initMap(text) {
            var uluru = {lat: parseFloat(text.attributes.lat.value), lng: parseFloat(text.attributes.lng.value)};
            var map = new google.maps.Map(document.getElementById('map'), {
                zoom: 13,
                center: uluru
            });
            var marker = new google.maps.Marker({
                position: uluru,
                map: map
            });
        }
    </script>
    <script async defer
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDs-0FF4wjQvpfWBQBAYMaFOjRCxe93A-M">
    </script>
</body>
</html>

<?php endif; ?>