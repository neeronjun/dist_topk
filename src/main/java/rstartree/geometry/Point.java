package rstartree.geometry;

public final class Point implements Rectangle, java.io.Serializable {

    private final int oid;
    private final int iid;
    private final float x;
    private final float y;
    private final float prob;

    public Point(int oid, int iid, float x, float y, float prob) {
        this.oid = oid;
        this.iid = iid;
        this.x = x;
        this.y = y;
        this.prob = prob;
    }

    static Point create(int oid, int iid, double x, double y, float prob) {
        return new Point(oid, iid, (float) x, (float) y, prob);
    }

    static Point create(int oid, int iid, float x, float y, float prob) {
        return new Point(oid, iid, x, y, prob);
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public double distance(Rectangle r) {
        return RectangleImpl.distance(x, y, x, y, r.x1(), r.y1(), r.x2(), r.y2());
    }

    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    public double distanceSquared(Point p) {
        float dx = x - p.x;
        float dy = y - p.y;
        return dx * dx + dy * dy;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return r.x1() <= x && x <= r.x2() && r.y1() <= y && y <= r.y2();
    }

    public  int oid(){
        return oid;
    }

    public  int iid(){
        return iid;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float getProb(){
        return prob;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Point [oid = " + oid() + ", iid = " + iid() + ", x=" + x() + ", y=" + y()+ ", prob = " + getProb() + "]";
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    @Override
    public int id() {
        return oid;
    }

    @Override
    public float x1() {
        return x;
    }

    @Override
    public float y1() {
        return y;
    }

    @Override
    public float x2() {
        return x;
    }

    @Override
    public float y2() {
        return y;
    }

    @Override
    public float prob() {
        return prob;
    }

    @Override
    public float area() {
        return 0;
    }

    @Override
    public Rectangle add(Rectangle r) {
        return RectangleImpl.create(r.id(), Math.min(x, r.x1()), Math.min(y, r.y1()), Math.max(x, r.x2()),
                Math.max(y, r.y2()), r.prob());
    }

    @Override
    public boolean contains(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public float intersectionArea(Rectangle r) {
        return 0;
    }

    @Override
    public float perimeter() {
        return 0;
    }

}