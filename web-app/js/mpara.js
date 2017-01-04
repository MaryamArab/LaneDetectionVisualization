// Parallel Polylines
//
// Original Google Maps V2 awesome code by: Bill Chadwick March 2008
// Released as Free for any use @ http://wtp2.appspot.com/ParallelLines.htm
//
// Modified for use with GMaps V3 by: Matthew Schwartz (schwartz.matthew@schwartzlink.net)
// Also released as free for any use

function BDCCParallelLines(points, color1, color2, weight, opacity, gapPx, lanes) {

    this.gapPx = gapPx;
    this.points = points;
    this.color1 = color1;
    this.color2 = color2;
    this.weight = weight;
    this.opacity = opacity;
    this.prj = null;
    this.line1 = null;
    this.line2 = null;
    this.zoomListener = null;
    this.lanes = lanes
}


BDCCParallelLines.prototype = new google.maps.OverlayView();

// BDCCParallelLines implements the OverlayView interface
// Methods that need to be implemented in GMaps 3 = onAdd(), draw(), and onRemove()



BDCCParallelLines.prototype.onAdd = function() {
    this.setProjection();
    var foo = this;
    var zoomRecalc = function() {
        foo.onRemove();
        foo.setProjection();
    };

    this.zoomListener = google.maps.event.addListener(map, 'zoom_changed', zoomRecalc);
}

BDCCParallelLines.prototype.setProjection = function() {
    this.map = this.getMap();
    var overlay = new google.maps.OverlayView();
    overlay.draw = function() {};
    overlay.setMap(map);
    this.prj = overlay.getProjection();
}

BDCCParallelLines.prototype.onRemove = function() {
    if(this.line2) {
        this.line2.setMap(null);
        this.line2 = null;
    }
    if(this.line1) {
        this.line1.setMap(null);
        this.line1 = null;
    }
    if (this.prj) {
        this.prj = null;
    }
    if(this.zoomListener != null) {
        google.maps.event.removeListener(this.zoomListener);
    }
}
BDCCParallelLines.prototype.draw = function(map) {
    if(this.line2) {
        this.line2.setMap(null);
        this.line2 = null;
    }
    if(this.line1) {
        this.line1.setMap(null);
        this.line1 = null;
    }
    this.recalc();
    return;
}

BDCCParallelLines.prototype.redraw = function(force) {
    return; //do nothing
}

BDCCParallelLines.prototype.recalc = function() {
    var d = 8;

    var pts1 = new Array();
    var pts2 = new Array();

    var rawPoints = new Array();

    var ppoint = this.prj.fromLatLngToContainerPixel(this.points[0]);
    var cpoint = this.prj.fromLatLngToContainerPixel(this.points[1]);

    // y = ax + b
    var pa = (cpoint.y - ppoint.y) / (cpoint.x - ppoint.x);
    var dx = cpoint.x - ppoint.x;
    var dy = cpoint.y - ppoint.y;

    var da = d * dy / Math.sqrt(dy * dy + dx * dx);
    var db = d * dx / Math.sqrt(dy * dy + dx * dx);
    var sign = dx / Math.abs(dx); // * dy / Math.abs(dy);

    var dp1 = new google.maps.Point(ppoint.x - sign * da, ppoint.y + sign * db);
    pts1.push(this.prj.fromContainerPixelToLatLng(dp1));
    var dp2 = new google.maps.Point(ppoint.x + sign * da, ppoint.y - sign * db);
    pts2.push(this.prj.fromContainerPixelToLatLng(dp2));

    var sign = dx / Math.abs(dx);

    var pb1 = cpoint.y - pa * cpoint.x + sign * d * Math.sqrt(pa * pa + 1);
    var pb2 = cpoint.y - pa * cpoint.x - sign * d * Math.sqrt(pa * pa + 1);

    ppoint = cpoint;

    for (var i = 1; i < this.points.length; i++) {
        cpoint = this.prj.fromLatLngToContainerPixel(this.points[i]);
        if (cpoint.x != ppoint.x) {
            var ca = (cpoint.y - ppoint.y) / (cpoint.x - ppoint.x);

            var dx = cpoint.x - ppoint.x;
            sign = dx / Math.abs(dx)

            var cb1 = cpoint.y - ca * cpoint.x + sign * d * Math.sqrt(ca * ca + 1);
            var cb2 = cpoint.y - ca * cpoint.x - sign * d * Math.sqrt(ca * ca + 1);

            if (pa != ca) {
                var p1 = new google.maps.Point((cb1 - pb1) / (pa - ca), (pa * cb1 - ca * pb1) / (pa - ca));
                pts1.push(this.prj.fromContainerPixelToLatLng(p1));
                var p2 = new google.maps.Point((cb2 - pb2) / (pa - ca), (pa * cb2 - ca * pb2) / (pa - ca));
                pts2.push(this.prj.fromContainerPixelToLatLng(p2));
            }

            ppoint = cpoint;
            pa = ca;
            pb1 = cb1;
            pb2 = cb2;
        }
    }

    ppoint = this.prj.fromLatLngToContainerPixel(this.points[this.points.length - 2]);
    dx = cpoint.x - ppoint.x;
    dy = cpoint.y - ppoint.y;

    da = d * dy / Math.sqrt(dy * dy + dx * dx);
    db = d * dx / Math.sqrt(dy * dy + dx * dx);
    sign = dx / Math.abs(dx); // * dy / Math.abs(dy);

    dp1 = new google.maps.Point(cpoint.x - sign * da, cpoint.y + sign * db);
    pts1.push(this.prj.fromContainerPixelToLatLng(dp1));
    dp2 = new google.maps.Point(cpoint.x + sign * da, cpoint.y - sign * db);
    pts2.push(this.prj.fromContainerPixelToLatLng(dp2));


    if (this.lanes.indexOf(1) != -1) {
        if (this.line1)
            this.line1.setMap(null);
        this.line1 = new google.maps.Polyline({
            path: pts1,
            strokeColor: this.color1,
            strokeOpacity: this.opacity,
            strokeWeight: this.weight
        });
        this.line1.setMap(map);
    }

    if (this.lanes.indexOf(3) != -1) {
        if (this.line2)
            this.line1.setMap(null);
        this.line2 = new google.maps.Polyline({
            path: pts2,
            strokeColor: this.color2,
            strokeOpacity: this.opacity,
            strokeWeight: this.weight
        });
        this.line2.setMap(map);
    }
}

BDCCParallelLines.prototype.intersect = function(p0,p1,p2,p3)
{
// this function computes the intersection of the sent lines p0-p1 and p2-p3
// and returns the intersection point,

    var a1,b1,c1, // constants of linear equations
        a2,b2,c2,
        det_inv,  // the inverse of the determinant of the coefficient matrix
        m1,m2;    // the slopes of each line

    var x0 = p0.x;
    var y0 = p0.y;
    var x1 = p1.x;
    var y1 = p1.y;
    var x2 = p2.x;
    var y2 = p2.y;
    var x3 = p3.x;
    var y3 = p3.y;

// compute slopes, note the cludge for infinity, however, this will
// be close enough

    if ((x1-x0)!=0)
        m1 = (y1-y0)/(x1-x0);
    else
        m1 = 1e+10;   // close enough to infinity

    if ((x3-x2)!=0)
        m2 = (y3-y2)/(x3-x2);
    else
        m2 = 1e+10;   // close enough to infinity

// compute constants

    a1 = m1;
    a2 = m2;

    b1 = -1;
    b2 = -1;

    c1 = (y0-m1*x0);
    c2 = (y2-m2*x2);

// compute the inverse of the determinate

    det_inv = 1/(a1*b2 - a2*b1);

// use Kramers rule to compute xi and yi

    var xi=((b1*c2 - b2*c1)*det_inv);
    var yi=((a2*c1 - a1*c2)*det_inv);

    return new google.maps.Point(Math.round(xi),Math.round(yi));

}
