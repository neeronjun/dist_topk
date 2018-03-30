package rstartree.geometry;

public interface Rectangle extends Geometry, HasGeometry {

    int id();

    float x1();

    float y1();

    float x2();

    float y2();

    float prob();

    float area();

    Rectangle add(Rectangle r);

    boolean contains(double x, double y);

    float intersectionArea(Rectangle r);

    float perimeter();

}