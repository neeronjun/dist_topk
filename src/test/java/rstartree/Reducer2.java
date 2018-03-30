package rstartree;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.StringTokenizer;

public class Reducer2 extends Reducer<IntWritable, ReduceValue, Text, LongWritable> {
    private float score = 0;
    Object t;
    MinHeap minHeap = new MinHeap();
    HeapNode heapNode;

    public void reduce(IntWritable key, Iterable<ReduceValue> values, Context context)
            throws IOException, InterruptedException {
        if(values == null) return;
        for (ReduceValue val: values) {
            t = val.getObjdelLB();
            score = val.getObjLB().get(); //change later
            minHeap.insert(val.hashCode(),score);  //not sure here
        }
        minHeap.buildHeap();
        // for(int i=0; i<k ; i++){
        for(int i=0; i<5 ; i++){
            heapNode = minHeap.extractMin();
            Entry e = ((Leaf) heapNode).entry(i);
            score = heapNode.getUB();
            context.write(new Text(e.toString()), new LongWritable((long)score));
        }
    }
}