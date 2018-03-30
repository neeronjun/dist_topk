package rstartree;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class Mapper2 extends Mapper<IntWritable, ReduceValue, IntWritable, Text> {

    private float score = 0;
    private float tau =0;
    public void reduce(IntWritable key, Iterable<ReduceValue> values, Context context)
            throws IOException, InterruptedException {
        if(values == null) return;
        for(ReduceValue value: values) {
            score += value.getObjLB().get();
            if(tau > value.getTau_i().get()){
                tau = value.getTau_i().get();
            }
        }
        if (score < tau){
            return;
        }
        Text text;
        text = new Text(Float.toString(score));
        context.write(new IntWritable(1), new Text(key.toString()+"\t"+text));
    }
}
