/**
 * @author DarkLiKally
 */

var origin = new google.maps.Point(1.0, 0.99998987);
var xAxis = new google.maps.Point(0.11785114, 0.08333337);
var yAxis = new google.maps.Point(0.0, -0.11785114);
var zAxis = new google.maps.Point(-0.11785114, 0.08333337);

var mapXUnit = new google.maps.Point(4.25, -4.25);
var mapYUnit = new google.maps.Point(3.0, 3.0);

var mapXMin = -512;
var mapYMin = -512;

var mapWidth = 1024;
var mapHeight = 1024;

var lattitudeRange = 10;

function getQueryParams() {
    var urlParams = {};
    var e,
    a = /\+/g,  // Regex for replacing addition symbol with a space
    r = /([^&;=]+)=?([^&;]*)/g,
    d = function (s) { return decodeURIComponent(s.replace(a, " ")); },
    q = window.location.search.substring(1);

    while (e = r.exec(q))
            urlParams[d(e[1])] = d(e[2]);

    return urlParams;
}

function getUrlWithoutParams() {
    parts = window.location.href.split('?')
    return parts[0];
}

function Location(x, y, z, world) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world;
}

function worldToMap(worldX, worldY, worldZ) {
    var point = new google.maps.Point(origin.x, origin.y);

    point.x += xAxis.x * worldX;
    point.y += xAxis.y * worldX;

    point.x += yAxis.x * worldY;
    point.y += yAxis.y * worldY;

    point.x += zAxis.x * worldZ;
    point.y += zAxis.y * worldZ;

    return point;
}

function mapToWorld(point) {
    var world = new WorldCoord(0, 0, 0);

    adjusted = new google.maps.Point(point.x-origin.x, point.y-origin.y);

    world.x += (mapXUnit.x * adjusted.x);
    world.z += (mapXUnit.y * adjusted.x);

    var xx = mapYUnit.x * adjusted.y;
    var zz = mapYUnit.y * adjusted.y;
    world.x += xx*2; // hmmm....
    world.z += zz*2;

    return world;
}

function dot(x0, y0, x1, y1) {
    return x0 * x1 + y0 * y1;
}

function dot(lhs, rhs) {
    return lhs.x * rhs.x + lhs.y * rhs.y;
}

function length(vec) {
    return Math.sqrt(dot(vec, vec));
}

function normalise(vec) {
    var len = length(vec);
    var out = new google.maps.Point(vec.x/len, vec.y/len);
    return out;
}

function WorldCoord(xx, yy, zz) {
    this.x = xx;
    this.y = yy;
    this.z = zz;
}

function MinecraftMapProjection() {}
MinecraftMapProjection.prototype.fromLatLngToPoint = function(latLng, opt_point) {
    // Convert from lat-long to map coord

    var point = opt_point || new google.maps.Point(latLng.lat(), latLng.lng());

    // from lat-long to normalised (0, 1) coord
    point.x = (point.x / lattitudeRange) + 0.5;
    point.y = (point.y / 180) + 0.5;

    // from normalised to map coord
    point.x = point.x * mapWidth + mapXMin;
    point.y = point.y * mapHeight + mapYMin;

    return point;
};

MinecraftMapProjection.prototype.fromPointToLatLng = function(point) {

    // from map coord to normalised (0, 1)
    point.x = (point.x - mapXMin) / mapWidth;
    point.y = (point.y - mapYMin) / mapHeight;

    // from normalised to lat-long
    point.x = point.x * lattitudeRange - (lattitudeRange/2);
    point.y = point.y * 180 - 90;

    return new google.maps.LatLng(point.x, point.y, true);
};
