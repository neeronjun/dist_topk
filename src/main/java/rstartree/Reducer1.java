package rstartree;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import rstartree.fbs.SerializerFlatBuffers;
import rstartree.geometry.Geometries;
import rstartree.geometry.Geometry;
import rstartree.geometry.Point;
import rstartree.geometry.Rectangle;
import rx.Observable;
import rx.functions.Func1;
import java.io.*;
import java.util.*;

public class Reducer1 extends Reducer<IntWritable, Text, IntWritable, Text> {

    Node node;
    ListHelper e;
    int numParitions = 3;
    HeapNode extract = new HeapNode();
    MinHeap heap = new MinHeap();
    float[] q = { 6000, 5000};
    Entry entry;
    Hashtable<Integer, Node> htNode = new Hashtable<Integer, Node>();
    HashMap<Integer, List<Point>> pointHash = new HashMap<>();
    HashMap[] treePointHash1 = new HashMap[numParitions];
    HashMap<Integer, List<Point>> treePointHash = new HashMap<>();
    RTree[] rTree = new RTree[numParitions];
    HashMap[] hashMap = new HashMap[numParitions];


    protected void setup(Context context) throws IOException {


        long t = System.currentTimeMillis();

        for(int i=0; i<numParitions; i++) {
            String filepath = "/user/hduser/index1/file" + i;
            try {
                rTree[i] = RTree.star().maxChildren(10).create();
                rTree[i] = deserialize(InternalStructure.SINGLE_ARRAY, filepath, context, createSerializer(), true);
                hashMap[i] = getRectangleDepths((Node<Object, Geometry>) rTree[i].root().get());

                // Reading the object from a file
                String objectPath = "/user/hduser/index1/obj" + i + ".ser";
                Path objectPathFile = new Path(objectPath);
                FileSystem fileSystem = objectPathFile.getFileSystem(context.getConfiguration());

                ObjectInputStream in = new ObjectInputStream(fileSystem.open(objectPathFile));

                this.treePointHash1[i] = (HashMap<Integer, List<Point>>) in.readObject();

                System.out.println("treePointhash has been deserialized ");
            } catch (IOException ex) {
                System.out.println("IOException is caught1");
            } catch (ClassNotFoundException ex) {
                System.out.println("ClassNotFoundException is caught");
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        System.out.println("setup-time " + (System.currentTimeMillis() - t) + "ms");

    }

    public void reduce(IntWritable key, Iterable<Text> val, Context context) throws IOException, InterruptedException {

        loadInstance(key);


        for (Text value : val
                ) {
            //context.write(key, value);

            String item = value.toString();
            String[] items = item.split("\t", -3);

            //int instID = Integer.parseInt(items[0]);
            int objID = Integer.parseInt(items[1]);
            int mbrID = Integer.parseInt(items[2]);
            int treeID = Integer.parseInt(items[3]);
            double t_LB = Double.parseDouble(items[4]);
            double t_UB = Double.parseDouble(items[5]);
            double tau_i = Double.parseDouble(items[6]);

            //EntryHelper eh = new EntryHelper();
            float objdelLB = 0;
            float inst_pScore;
            //Entry entry = (Entry) eh.getNode();
            System.out.println(objID + ", " + key.get());
            //System.out.println(value);
            List<Point> list_ti = pointHash.get(objID);
            // System.out.println(list_ti.size());
            // System.out.println(pointHash.keySet());
            Node nodeMBR = loadNode(mbrID, treeID);

            for (Point t_i : list_ti
                    ) {
                heap = new MinHeap();
                inst_pScore = 0;
                int level = 0;


                if (nodeMBR == null) {
                    System.out.println("cant find the node...mbrID: " + mbrID);
                }

                if (nodeMBR instanceof Leaf) {
                    List<Entry> entryList = ((Leaf) nodeMBR).entries();
                    for (Entry entry1 : entryList) {
                        List<Point> pointList = treePointHash.get(entry1.geometry().mbr().id());
                        for (Point o_j : pointList
                                ) {
                            if (fullyDominate(o_j.geometry(), t_i.geometry())) {
                                inst_pScore = inst_pScore + o_j.geometry().mbr().prob();
                            }
                        }
                    }
                }else {

                    List<Node> childs = ((NonLeaf) nodeMBR).children();

                    for (Node child : childs
                            ) {
                        //System.out.println("inserting to heap... " + child.geometry());
                        heap.insert(child.hashCode(), level);
                        htNode.put(child.hashCode(), child);
                    }
                    //}
                    while (!heap.isEmpty()) {
                        extract = heap.extractMin();
                        e = new ListHelper();
                        int id = extract.getNode();
                        Node node = htNode.get(id);
                        e.setNode(htNode.get(id));
                        e.setUB(-extract.getUB());

                        if (node instanceof Leaf) {
                            List<Entry> entryList = ((Leaf) node).entries();
                            for (Entry entry1 : entryList) {
                                List<Point> pointList = treePointHash.get(entry1.geometry().mbr().id());
                                for (Point o_j : pointList
                                        ) {
                                    if (fullyDominate(o_j.geometry(), t_i.geometry())) {
                                        inst_pScore = inst_pScore + o_j.geometry().mbr().prob();
                                    }
                                }
                            }
                        } else {
                            List<Node> nodeList = ((NonLeaf) node).children();
                            for (Node node1 : nodeList
                                    ) {
                                if (fullyDominate(node1.geometry(), t_i.geometry())) {
                                    inst_pScore = inst_pScore + node1.geometry().mbr().prob();
                                } else {
                                    if (partiallyDominate(node1.geometry(), t_i.geometry())) {
                                        //System.out.println("inserting to heap... " + node1.geometry());
                                        heap.insert(node1.hashCode(), (float) t_UB);
                                        htNode.put(node1.hashCode(), node1);
                                    }
                                }
                            }
                        }
                    }
                }

                //inst_pScore = inst_pScore + mapValue1.getInstID().;
                float f1 = nodeMBR.geometry().mbr().prob();
                float f2 = t_i.geometry().mbr().prob();

                t_UB = (t_UB - (f1 * f2) + inst_pScore);
                //System.out.println("t_UB = " + t_UB);
                if (t_UB < tau_i) {
                    return;
                }
                objdelLB = objdelLB + inst_pScore;

            }
            String str = new String(String.valueOf(objID) + "\t" + String.valueOf(t_LB) + "\t" + String.valueOf(objdelLB) + "\t" + String.valueOf(tau_i) + "\n");
            context.write(new IntWritable(key.get()), new Text(str));

            //IntWritable temp = new IntWritable(mbrID);
            //Text temptext = new Text(String.valueOf(instID));
            //context.write(key,temptext);
        }
    }

    public static int sta = 1;

    private void loadInstance(IntWritable key) {

        if (sta == 1) {

            System.out.println("inside");
            this.pointHash = treePointHash1[key.get()];
        }
        sta++;
    }

    public static int sta1 = 1;



    public Node loadNode(int mbrID, int treeId) {

        HashMap hashMap1 = hashMap[treeId];
        Node node;
        if(hashMap1 != null)
            System.out.println(mbrID +"Node found in tree"+treeId);
        else
            System.out.println("Node not found! ");
        node = (Node) hashMap1.get(mbrID);

        // Method for deserialization of object
        this.treePointHash = treePointHash1[treeId];

        if(hashMap1 == null)
            System.out.println(mbrID +"Node found in tree"+treeId);
        else
            System.out.println("Node not found! ");

        return node;
    }

    private <R, S extends Geometry> boolean fullyDominate(Geometry root, Geometry n) {


        float x1 = n.mbr().x1();
        float y1 = n.mbr().y1();
        float x2 = n.mbr().x2();
        float y2 = n.mbr().y2();
        float x3 = root.mbr().x1();
        float y3 = root.mbr().y1();
        float x4 = root.mbr().x2();
        float y4 = root.mbr().y2();

        x1 = Math.abs(x1 - q[0]);
        y1 = Math.abs(y1 - q[1]);
        x2 = Math.abs(x2 - q[0]);
        y2 = Math.abs(y2 - q[1]);
        x3 = Math.abs(x3 - q[0]);
        y3 = Math.abs(y3 - q[1]);
        x4 = Math.abs(x4 - q[0]);
        y4 = Math.abs(y4 - q[1]);



        if ( x1 < x3 && x2 < x3 && x1 < x4 && x2 < x4 && y1 < y3 && y2 < y3 && y2 < y4 && y1 < y4 ) {
            return true;
        } else {
            return false;
        }

    }


    private <R, S extends Geometry> boolean partiallyDominate(Geometry root, Geometry n) {

        float x1 = n.mbr().x1();
        float y1 = n.mbr().y1();
        float x2 = n.mbr().x2();
        float y2 = n.mbr().y2();
        float x3 = root.mbr().x1();
        float y3 = root.mbr().y1();
        float x4 = root.mbr().x2();
        float y4 = root.mbr().y2();


        x1 = Math.abs(x1 - q[0]);
        y1 = Math.abs(y1 - q[1]);
        x2 = Math.abs(x2 - q[0]);
        y2 = Math.abs(y2 - q[1]);
        x3 = Math.abs(x3 - q[0]);
        y3 = Math.abs(y3 - q[1]);
        x4 = Math.abs(x4 - q[0]);
        y4 = Math.abs(y4 - q[1]);



        //if (x1 <= x3 && x3 <= x2 || x1 <= x4 && x4 <= x2 || x3 <= x1 && x1 <= x4 || y1 <= y4 && y4 <= y2 || y1 <= y3 && y3 <= y2 || ) {
        if(x4 < x1 && x3 < x1 && x4 < x2 && x3 < x2  && y4 < y1 && y3 < y1 && y4 < y2  && y3 < y2 || x3 < x1 && x3 < x2 && x4 < x1 && x4 < x2 || y3 < y1 && y3 < y2 && y4 < y1 && y4 < y2)
            return false;
        else
            return true;

    }

    protected <T, S extends Geometry> HashMap getRectangleDepths(Node<T, S> node) {
        final HashMap<Integer, Node> list = new HashMap<>();
        list.put(node.geometry().mbr().id(), node);
        if (node instanceof Leaf) {
            final Leaf<T, S> leaf = (Leaf<T, S>) node;
            for (final Entry<T, S> entry : leaf.entries()) {
                // list.put(entry.geometry().mbr().hashCode(), (Node) entry);
            }
        } else {
            final NonLeaf<T, S> n = (NonLeaf<T, S>) node;
            for (int i = 0; i < n.count(); i++) {
                list.putAll(getRectangleDepths(n.child(i)));
            }

        }
        return list;
    }

    private static final byte[] EMPTY = new byte[] {};
    private static Serializer<Object, Rectangle> createSerializer() {
        Func1<Object, byte[]> serializer = new Func1<Object, byte[]>() {
            @Override
            public byte[] call(Object o) {
                return EMPTY;
            }
        };
        Func1<byte[], Object> deserializer = new Func1<byte[], Object>() {
            @Override
            public Object call(byte[] bytes) {
                return null;
            }
        };
        Serializer<Object, Rectangle> fbSerializer = SerializerFlatBuffers.create(serializer,
                deserializer);
        return fbSerializer;
    }

    private static RTree<Object, Rectangle> deserialize(InternalStructure structure, String filePath, Context context,
                                                        //private static RTree<Object, Rectangle> deserialize(InternalStructure structure, File file,
                                                        Serializer<Object, Rectangle> fbSerializer, boolean backpressure) throws Exception {

        Path ofile = new Path(filePath);
        FileSystem fs = ofile.getFileSystem(context.getConfiguration());

        InputStream is = fs.open(ofile);
        //InputStream is = new FileInputStream(file);
        fs.getLength(ofile);

        RTree<Object, Rectangle> tr = fbSerializer.read(is, fs.getLength(ofile), structure);
        //RTree<Object, Rectangle> tr = fbSerializer.read(is, file.length(), structure);

        //fs.close();
        //is.close();
        return tr;
    }

}




