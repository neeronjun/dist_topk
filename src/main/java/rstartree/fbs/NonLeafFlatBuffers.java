package rstartree.fbs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import rstartree.Context;
import rstartree.Entries;
import rstartree.Entry;
import rstartree.Node;
import rstartree.NonLeaf;
import rstartree.fbs.generated.Box_;
import rstartree.fbs.generated.Entry_;
import rstartree.fbs.generated.Geometry_;
import rstartree.fbs.generated.Node_;
import rstartree.geometry.Geometries;
import rstartree.geometry.Geometry;
import rstartree.internal.NodeAndEntries;
import rstartree.internal.NonLeafHelper;

import rx.Subscriber;
import rx.functions.Func1;

final class NonLeafFlatBuffers<T, S extends Geometry> implements NonLeaf<T, S>, Serializable {

    private final Node_ node;
    private final Context<T, S> context;
    private final Func1<byte[], ? extends T> deserializer;

    NonLeafFlatBuffers(Node_ node, Context<T, S> context, Func1<byte[], ? extends T> deserializer) {
        Preconditions.checkNotNull(node);
        // remove precondition because reduces performance
        // Preconditions.checkArgument(node.childrenLength() > 0);
        this.node = node;
        this.context = context;
        this.deserializer = deserializer;
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return NonLeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return NonLeafHelper.delete(entry, all, this);
    }


    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber) {
        // pass through entry and geometry and box instances to be reused for
        // flatbuffers extraction this reduces allocation/gc costs (but of
        // course introduces some mutable ugliness into the codebase)
        searchWithoutBackpressure(node, criterion, subscriber, deserializer, new Entry_(),
                new Geometry_(), new Box_());
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> void searchWithoutBackpressure(Node_ node,
            Func1<? super Geometry, Boolean> criterion, Subscriber<? super Entry<T, S>> subscriber,
            Func1<byte[], ? extends T> deserializer, Entry_ entry, Geometry_ geometry, Box_ box) {
        {
            node.mbb(box);
            if (!criterion
                    .call(Geometries.rectangle((int)(box.maxX()+box.maxY()+box.minX()+box.minY()), box.minX(), box.minY(), box.maxX(), box.maxY(),0)))
                return;
        }
        int numChildren = node.childrenLength();
        // reduce allocations by reusing objects
        Node_ child = new Node_();
        if (numChildren > 0) {
            for (int i = 0; i < numChildren; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                node.children(child, i);
                searchWithoutBackpressure(child, criterion, subscriber, deserializer, entry,
                        geometry, box);
            }
        } else {
            int numEntries = node.entriesLength();
            // reduce allocations by reusing objects
            // check all entries
            for (int i = 0; i < numEntries; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                // set entry
                node.entries(entry, i);
                // set geometry
                entry.geometry(geometry);
                final Geometry g = FlatBuffersHelper.toGeometry(geometry);
                if (criterion.call(g)) {
                    T t = FlatBuffersHelper.parseObject(deserializer, entry);
                    Entry<T, S> ent = Entries.entry(t, (S) g);
                    subscriber.onNext(ent);
                }
            }
        }

    }

    private List<Node<T, S>> createChildren() {

        // reduce allocations by resusing objects
        int numChildren = node.childrenLength();
        List<Node<T, S>> children = new ArrayList<Node<T, S>>(numChildren);
        for (int i = 0; i < numChildren; i++) {
            Node_ child = node.children(i);
            if (child.childrenLength() > 0) {
                children.add(new NonLeafFlatBuffers<T, S>(child, context, deserializer));
            } else {
                children.add(new LeafFlatBuffers<T, S>(child, context, deserializer));
            }
        }
        return children;
    }

    @Override
    public int count() {
        return node.childrenLength();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        return FlatBuffersHelper.createBox(node.mbb());
    }

    @Override
    public Node<T, S> child(int i) {
        Node_ child = node.children(i);
        if (child.childrenLength() > 0)
            return new NonLeafFlatBuffers<T, S>(child, context, deserializer);
        else
            return new LeafFlatBuffers<T, S>(child, context, deserializer);
    }

    @Override
    public List<Node<T, S>> children() {
        return createChildren();
    }

    @Override
    public String toString() {
        return "Node [" + (node.childrenLength() > 0 ? "NonLeaf" : "Leaf") + ","
                + FlatBuffersHelper.createBox(node.mbb()).toString() + "]";
    }

}
