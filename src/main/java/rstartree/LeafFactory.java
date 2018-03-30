package rstartree;

import java.util.List;

import rstartree.geometry.Geometry;

public interface LeafFactory<T, S extends Geometry> {
    Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context);
}
