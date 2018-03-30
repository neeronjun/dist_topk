package rstartree;

public class MapValue extends EntryHelper {

    private float tau_i;
    private RectangleDepth rectangleDepth;
    private EntryHelper entryHelper;

    //constructor

    public MapValue() {
        this.tau_i = Float.NEGATIVE_INFINITY;
        this.rectangleDepth = new RectangleDepth(null, 0);
        this.entryHelper = new EntryHelper();

    }

    public float getTau_i() {
        return tau_i;
    }

    public void setTau_i(float tau_i) {
        this.tau_i = tau_i;
    }

    public RectangleDepth getRectangleDepth() {
        return rectangleDepth;
    }

    public void setRectangleDepth(RectangleDepth rectangleDepth) {
        this.rectangleDepth = rectangleDepth;
    }

    public EntryHelper getEntryHelper() {
        return entryHelper;
    }

    public void setEntryHelper(EntryHelper entryHelper) {
        this.entryHelper = entryHelper;
    }

    public MapValue(float tau_i, EntryHelper entryHelper, RectangleDepth rectangleDepth) {
        //super(entryId, UB, LB);
        this.tau_i = tau_i;

        this.entryHelper =  entryHelper;
        this.rectangleDepth = rectangleDepth;
    }
}
