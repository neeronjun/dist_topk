package rstartree;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PTDQuery {

        public static void main(String[] args) throws Exception {

            Configuration conf = new Configuration();
            conf.set("mapreduce.map.output.compress", "true");
            Job job1 = Job.getInstance(conf, "job1");
            job1.setJarByClass(PTDQuery.class);
            job1.setMapperClass(Mapper1.class); //change to Mapper1
            //job1.setCombinerClass(Reducer1.class);
            job1.setReducerClass(Reducer1.class);
            job1.setPartitionerClass(Partitioner1.class);
            job1.setNumReduceTasks(3);
            //job1.setMapOutputKeyClass(IntWritable.class);
            //job1.setMapOutputValueClass(MapperOut.class);
            job1.setOutputKeyClass(IntWritable.class);
            job1.setOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job1, new Path(args[0]));
            FileOutputFormat.setOutputPath(job1, new Path(args[1]));
            if (!job1.waitForCompletion(true)) {
                System.exit(1);
            }

        }
    }
