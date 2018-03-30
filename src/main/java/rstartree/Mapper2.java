package rstartree;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import rstartree.geometry.Geometry;
import rstartree.geometry.Rectangle;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Mapper2 extends Mapper<Object, Text, IntWritable, Text> {

    private HashMap<Integer, int[]> partialList = new HashMap<>();
    private float tau_i;
    int level = 4;
    int k = 10;
    int numPartitions = 3;
    float[] q = {0, 0};
    MinHeap H;
    ListHelper e = new ListHelper();
    ListHelper er = new ListHelper();
    List<RTree<Object, Rectangle>> indexes = new ArrayList<>();
    List<HashMap<Integer, List<RectangleDepth>>> indexes1 = new ArrayList<>();
    private static final byte[] EMPTY = new byte[]{};
    HeapNode extract = new HeapNode();
    HashMap<Integer, Node> htNode = new HashMap<Integer, Node>();
    HashMap<Integer, List<RectangleDepth>> rhashMap = new HashMap<>();

    protected void setup(Context context) throws IOException {
        //loadIndexes(numPartitions);
        //loadIndex(indexes, 5);
    }


    public void map(Object key, Text val, Context context) throws IOException, InterruptedException {

        /*for (int i = 0; i < Scand.size(); i++) {
            EntryHelper eh = Scand.get(i);
            int objectID = eh.getNode().geometry().mbr().id();

            int[] mbrID = partialList.get(eh.getNode().geometry().mbr().id());

            MapperOut valOut = new MapperOut(objectID, objectID, mbrID[0], mbrID[1], eh.getLB(), eh.getUB(), tau_i);
            String str = new String(String.valueOf(valOut.getInstID()) + "\t" + String.valueOf(valOut.getObjID()) + "\t" + String.valueOf(valOut.getMBRID()) + "\t" + String.valueOf(valOut.getTreeID()) + "\t" + String.valueOf(valOut.getT_LB()) + "\t" + String.valueOf(valOut.getT_UB() + "\t" + String.valueOf(tau_i) + "\0"));
            //String str = "123,444,999"+"\0";
            //String str = "004-034556";
            value.set(str);

            //      System.out.println(val);
            //context.write(key2, value);
            System.out.println(val.toString());

            //context.write(key2, val);
            String s = val.toString();
            int treeID = Integer.parseInt(s);
            context.write(new IntWritable(treeID), value);
        }

*/
    }
}