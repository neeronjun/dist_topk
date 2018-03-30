package rstartree;

import rstartree.geometry.Geometry;

public interface EntryFactory<T,S extends Geometry> {
    Entry<T,S> createEntry(T value, S geometry);
}
