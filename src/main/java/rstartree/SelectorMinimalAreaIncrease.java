package rstartree;

import static java.util.Collections.min;

import java.util.List;

import rstartree.geometry.Geometry;
import rstartree.internal.Comparators;

/**
 * Uses minimal area increase to select a node from a list.
 *
 */
public final class SelectorMinimalAreaIncrease implements Selector {

    @Override
    public <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes) {
        return min(nodes, Comparators.areaIncreaseThenAreaComparator(g.mbr()));
    }
}
