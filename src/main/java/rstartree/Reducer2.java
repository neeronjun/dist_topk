package rstartree;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;


public class Reducer2 extends Reducer<IntWritable, Text, IntWritable, Text> {


    public void reduce(IntWritable key, Iterable<Text> val, Context context) throws IOException, InterruptedException {


        for (Text value : val
                ) {
            context.write(key, value);

        }
    }
}