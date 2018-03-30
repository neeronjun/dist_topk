package rstartree;

public class EntryHelper {
    private Entry entryId;
    private float UB;
    private float LB;

    public EntryHelper() {

        this.entryId = null;
        this.UB = 0;
        this.LB = 0;
    }
    public Entry getNode() {
        return this.entryId;
    }

    public void setNode(Entry node) {
        this.entryId = node;
    }

    public float getUB() {
        return this.UB;
    }

    public void setUB(float UB) {
        this.UB = UB;
    }

    public float getLB() {
        return this.LB;
    }

    public void setLB(float LB) {
        this.LB = LB;
    }

    public EntryHelper(Entry entryId, float UB, float LB) {

        this.entryId = entryId;
        this.UB = UB;
        this.LB = LB;
    }
}
