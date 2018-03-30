package rstartree;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.Random;

public class Partitioner1 extends Partitioner<IntWritable, Text> {
    @Override
    public int getPartition(IntWritable key, Text value, int numPartitions) {
//        Random r = new Random();
//        //return r.nextInt(1);
//        return r.nextInt(numPartitions);
//
//        for(int i=0; i<numPartitions; i++){
//
//            if(.equals("tree"+i))
//                return i;
//        }
        return key.get();
    }
}
