package rstartree.internal;

import java.util.List;

import rstartree.Context;
import rstartree.Entry;
import rstartree.Leaf;
import rstartree.Node;
import rstartree.geometry.Geometry;
import rstartree.geometry.Rectangle;

import rx.Subscriber;
import rx.functions.Func1;

public final class LeafDefault<T, S extends Geometry> implements Leaf<T, S> {

    private final List<Entry<T, S>> entries;
    private final Rectangle mbr;
    private final Context<T, S> context;

    public LeafDefault(List<Entry<T, S>> entries, Context<T, S> context) {
        this.entries = entries;
        this.context = context;
        this.mbr = Util.mbr(entries);
        //Entry entry = entries.get(0);
        //entry.geometry();
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public List<Entry<T, S>> entries() {
        return entries;
    }


    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {
        LeafHelper.search(condition, subscriber, this);
    }

    @Override
    public int count() {
        return entries.size();
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return LeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return LeafHelper.delete(entry, all, this);
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Entry<T, S> entry(int i) {
        return entries.get(i);
    }

}
