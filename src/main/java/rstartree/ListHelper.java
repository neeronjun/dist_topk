package rstartree;

import com.github.davidmoten.guavamini.Optional;

public class ListHelper {
    private Node nodeId;
    private float UB;
    private float LB;

    public ListHelper() {

        this.nodeId = null;
        this.UB = 0;
        this.LB = 0;
    }

    public Node getNode() {
        return nodeId;
    }

    public void setNode(Node node) {
        this.nodeId = node;
    }

    public float getUB() {
        return UB;
    }

    public void setUB(float UB) {
        this.UB = UB;
    }

    public float getLB() {
        return LB;
    }

    public void setLB(float LB) {
        this.LB = LB;
    }

    public ListHelper(Node nodeId, float UB, float LB) {

        this.nodeId = nodeId;
        this.UB = UB;
        this.LB = LB;
    }

}
