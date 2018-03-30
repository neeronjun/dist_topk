package rstartree;

import rstartree.geometry.Geometry;
import rstartree.geometry.HasGeometry;
import rstartree.geometry.Rectangle;

public class Mbr implements HasGeometry {

    private final Rectangle r;

    public Mbr(Rectangle r) {
        this.r = r;
    }

    @Override
    public Geometry geometry() {
        return r;
    }

}
