package rstartree;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;

public class ReduceValue {
    private IntWritable objLB;
    private IntWritable objdelLB;
    private FloatWritable tau_i;

    public ReduceValue(IntWritable objLB, IntWritable objdelLB, FloatWritable tau_i) {
        this.objLB = objLB;
        this.objdelLB = objdelLB;
        this.tau_i = tau_i;
    }
    public ReduceValue(){
        this.objLB.set(0);
        this.objdelLB.set(0);
        this.tau_i.set(0);
    }

    public IntWritable getObjLB() {
        return objLB;
    }

    public void setObjLB(IntWritable objLB) {
        this.objLB = objLB;
    }

    public IntWritable getObjdelLB() {
        return objdelLB;
    }

    public void setObjdelLB(IntWritable objdelLB) {
        this.objdelLB = objdelLB;
    }

    public FloatWritable getTau_i() {
        return tau_i;
    }

    public void setTau_i(FloatWritable tau_i) {
        this.tau_i = tau_i;
    }
}
