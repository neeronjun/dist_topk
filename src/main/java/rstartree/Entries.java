package rstartree;

import rstartree.geometry.Geometry;
import rstartree.internal.EntryDefault;

public final class Entries {

    private Entries() {
        // prevent instantiation
    }
    
    public static <T, S extends Geometry> Entry<T,S> entry(T object, S geometry) {
        return EntryDefault.entry(object, geometry);
    }
    
}
