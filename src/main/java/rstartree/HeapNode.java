package rstartree;

import com.github.davidmoten.guavamini.Optional;
import rstartree.geometry.Geometry;
import rstartree.internal.NodeAndEntries;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.List;

public class HeapNode {
    private int node;
    private float UB;

    public HeapNode(int node, float UB) {
        this.node = node;
        this.UB = UB;
    }
    public HeapNode(){
        this.node = 0;
        this.UB =0;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public float getUB() {
        return UB;
    }

    public void setUB(float UB) {
        this.UB = UB;
    }

}
