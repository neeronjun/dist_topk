package rstartree;

import rstartree.geometry.Geometry;
import rstartree.geometry.HasGeometry;

public interface Entry<T, S extends Geometry> extends HasGeometry {

    T value();

    @Override
    S geometry();

}