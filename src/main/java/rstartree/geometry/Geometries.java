package rstartree.geometry;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

public final class Geometries {

    private Geometries() {
        // prevent instantiation
    }

    public static Point point(int oid,int iid, double x, double y,float prob) {
        return Point.create(oid, iid, x, y, prob);
    }

    public static Point point(int oid, int iid, float x, float y, float prob) {
        return Point.create(oid, iid,  x,  y, prob);
    }

    public static Rectangle rectangle(int id, double x1, double y1, double x2, double y2, double prob) {
        return RectangleImpl.create(id, x1, y1, x2, y2,prob);
    }

    public static Rectangle rectangle(int id, float x1, float y1, float x2, float y2, float prob) {
        return RectangleImpl.create(id, x1, y1, x2, y2, prob);
    }

    /*public static Circle circle(double x, double y, double radius) {
        return Circle.create(x, y, radius);
    }*/

    /*public static Circle circle(float x, float y, float radius) {
        return Circle.create(x, y, radius);
    }*/

    public static Line line(int id, double x1, double y1, double x2, double y2, double prob) {
        return Line.create(id, x1, y1, x2, y2, prob);
    }

    public static Line line(int id, float x1, float y1, float x2, float y2, float prob) {
        return Line.create(id, x1, y1, x2, y2, prob);
    }

    public static Rectangle rectangleGeographic(double lon1, double lat1, double lon2,
            double lat2) {
        return rectangleGeographic((float) lon1, (float) lat1, (float) lon2, (float) lat2);
    }

    public static Rectangle rectangleGeographic(int id, float lon1, float lat1, float lon2, float lat2, float prob) {
        float x1 = normalizeLongitude(lon1);
        float x2 = normalizeLongitude(lon2);
        if (x2 < x1) {
            x2 += 360;
        }
        return rectangle(id, x1, lat1, x2, lat2, prob);
    }

    //public static Point pointGeographic(double lon, double lat) {
     //   return point(normalizeLongitude(lon), lat);
   // }

    @VisibleForTesting
    static double normalizeLongitude(double d) {
        return normalizeLongitude((float) d);
    }

    private static float normalizeLongitude(float d) {
        if (d == -180.0f)
            return -180.0f;
        else {
            float sign = Math.signum(d);
            float x = Math.abs(d) / 360;
            float x2 = (x - (float) Math.floor(x)) * 360;
            if (x2 >= 180)
                x2 -= 360;
            return x2 * sign;
        }
    }

}
