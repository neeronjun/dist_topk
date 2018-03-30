package rstartree;

import java.util.List;

import rstartree.geometry.Geometry;

public interface NonLeafFactory<T, S extends Geometry> {

    NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context);
}
