package rstartree;

public class MapperOut {
    private int objID;
    private int instID;
    private int MBRID;
    private int treeID;
    private float t_LB;
    private float t_UB;
    private float tau_i;




    public MapperOut(int objectID, int id, int mbrID, int treeID, float lb, float ub, float tau_i) {
        this.objID = id;
        this.instID = objectID;
        this.MBRID = mbrID;
        this.treeID = treeID;
        this.t_LB = lb;
        this.t_UB = ub;
        this.tau_i = tau_i;
    }

    public int getTreeID() {    return treeID;  }

    public void setTreeID(int treeID) { this.treeID = treeID; }

    public int getObjID() {
        return objID;
    }

    public void setObjID(int objID) {
        this.objID = objID;
    }

    public int getInstID() {
        return instID;
    }

    public void setInstID(int instID) {
        this.instID = instID;
    }

    public int getMBRID() {
        return MBRID;
    }

    public void setMBRID(int MBRID) {
        this.MBRID = MBRID;
    }

    public float getT_LB() {
        return t_LB;
    }

    public void setT_LB(float t_LB) {
        this.t_LB = t_LB;
    }

    public float getT_UB() {
        return t_UB;
    }

    public void setT_UB(float t_UB) {
        this.t_UB = t_UB;
    }

    public float getTau_i() {
        return tau_i;
    }

    public void setTau_i(float tau_i) {
        this.tau_i = tau_i;
    }
}
