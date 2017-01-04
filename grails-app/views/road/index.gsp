<%--
  Created by IntelliJ IDEA.
  User: marab
  Date: 10/31/2015
  Time: 4:34 PM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <style type="text/css">
    html { height: 100% }
    body { height: 100%; margin: 0px; padding: 0px }
    #map_canvas { height: 100% }
    </style>


    <script type="text/javascript"
            src="http://maps.google.com/maps/api/js?sensor=false">
    </script>
    <g:javascript src="mpara.js"/>

    <script type="text/javascript">
    var map;
    var cPoly = null;
    var cPolyM = null;
    var cPts;

    function initialize(){


        var myOptions = {
            zoom: 18,
            center: new google.maps.LatLng(${pathParts[0].points[0].lat}, ${pathParts[0].points[0].lng}),
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);



        <g:each in="${pathParts}" var="part" status="i">
            var pts${i} = new Array();

            <g:each in="${part.points}" var="point">
            <g:if test="${point.newPoint}">pts${i}.push (new google.maps.LatLng(${point.lat}, ${point.lng}));</g:if>
            </g:each>

            var test_line${i} = new google.maps.Polyline({
                path: pts${i},
                strokeColor: "#FF0000",
                strokeOpacity: 1.0,
                strokeWeight: 4
            });

            <g:if test="${part.possibleLanes.contains(2)}">
            test_line${i}.setMap(map);
            </g:if>
            var poly${i} = new BDCCParallelLines(pts${i}, "#0000FF","#86B951",4,1,7, ${part.possibleLanes as grails.converters.JSON});
            poly${i}.setMap(map);

        </g:each>

        var cPts = new Array();
    }
    </script>
</head>
<body onload="initialize()">
<div id="map_canvas" style="width:100%; height:100%"></div>
</body>
</html>