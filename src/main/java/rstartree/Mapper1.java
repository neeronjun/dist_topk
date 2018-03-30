package rstartree;


import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import rstartree.fbs.SerializerFlatBuffers;
import rstartree.geometry.Geometry;
import rstartree.geometry.Rectangle;
import rx.functions.Func1;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class Mapper1 extends Mapper<Object, Text, IntWritable, Text> {

    private HashMap<Integer, int[]> partialList = new HashMap<>();
    private float tau_i;
    int level = 4;
    int k = 10;
    int numPartitions =3;
    float [] q ={ 6000, 5000};
    MinHeap heap;
    ListHelper e = new ListHelper();
    ListHelper er = new ListHelper();
    List<RTree<Object,Rectangle>> indexes = new ArrayList<>();
    HashMap<Integer, List<RectangleDepth>> [] indexes1 = new HashMap[numPartitions];
    HashMap<Integer, Node> [] mbrIndexes = new HashMap[numPartitions];
    private static final byte[] EMPTY = new byte[] {};
    HeapNode extract = new HeapNode();
    HashMap<Integer, Node> htNode = new HashMap<Integer, Node>();
    HashMap<Integer, List<RectangleDepth>> rhashMap = new HashMap<>();
    HashMap<Integer, Node> mbrhashMap;

    protected void setup(Context context) throws IOException {
        loadIndexes(numPartitions, context);
        loadIndex(indexes, level);
    }

    private void loadIndexes(int n, Context context) {
        for(int i=0; i< n;i++){
            String infile = "/user/hduser/index1/file"+i;
            //File file = new File("/home/nrai/Desktop/file"+i);
            RTree<Object, Rectangle> rtree1 = RTree.star().maxChildren(10).create();
            try {
                rtree1 = deserialize(InternalStructure.SINGLE_ARRAY, infile, context, createSerializer(),true);
                //rtree1 = deserialize(InternalStructure.SINGLE_ARRAY, file, createSerializer(),true);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Node node1 = rtree1.root().get();
            mbrhashMap = new HashMap<>();
            indexes1[i] = new HashMap<>(getRectangleDepths(node1, -1, level, i));
            //indexes1.add(i, getRectangleDepths(node1, -1, level));
        }
    }

    public static List<List<RectangleDepth>> loadIndex(List<RTree<Object, Rectangle>> indexes, int level){
        List<List<RectangleDepth>> indexes1 = new ArrayList<List<RectangleDepth>>();
        for(RTree rTree: indexes) {
            Visualizer vi = new Visualizer();
            List<RectangleDepth> list;
            List<RectangleDepth> list1 = new ArrayList<>();
            list = vi.getRectangleDepths((Node<Object, Geometry>) rTree.root().get(),0);
            for (int j = 0; j < list.size(); j++) {
                RectangleDepth rd = list.get(j);
                if (rd.getDepth() <= level) {
                    list1.add(rd);
                }
            }
            //System.out.println("index "+list1.size());
            indexes1.add(list1);
        }
        return indexes1;
    }

    public void map(Object key, Text val, Context context) throws IOException, InterruptedException {
        Node node;
        Text value = new Text("null");
        EntryHelper t;
        int count = 0;
        tau_i = Float.NEGATIVE_INFINITY; //initialize the value of tau_i to negative infinity
        heap = new MinHeap(); //initialize an empty heap
        List<EntryHelper> Scand = new ArrayList<EntryHelper>();
        partialList = new HashMap<>();
        //load the tree

        RTree<Object, Rectangle> rtree;
        rtree = loadTree(val.toString(), context);

        node = rtree.root().get();;
        //Node node = this.tree.root().get();
        heap.insert(node.hashCode(), 0);
        htNode.put(node.hashCode(), node); //htNode is a hash table that stores nodes
        while (!heap.isEmpty()) {
            extract = heap.extractMin();
            e = new ListHelper();
            int id = extract.getNode();
            e.setNode(htNode.get(id));
            e.setUB(-extract.getUB());
            if (e.getUB() <= this.tau_i) {
                break;
            }
            if (e.getNode() instanceof Leaf) {
                List<Entry> entries = ((Leaf) e.getNode()).entries();
                for (Entry entry : entries) {
                    t = new EntryHelper();
                    Geometry geo = entry.geometry();
                    t.setNode(entry);
                    //check the value of k
                    if (count < k) {
                        t.setLB(ScoreLB(indexes1, q, geo, level));
                        t.setUB(ScoreUB(indexes1, q, geo, level));
                        Scand.add(t);
                        Collections.sort(Scand, new Comparator<EntryHelper>() {
                            @Override
                            public int compare(EntryHelper o1, EntryHelper o2) {
                                if (o1.getLB() > o2.getLB())
                                    return -1;
                                if (o1.getLB() < o2.getLB())
                                    return 1;
                                return 0;
                            }
                        });
                        count++;
                        if (count == k)
                            tau_i = Scand.get(k - 1).getLB();
                    } else {
                        t.setUB(ScoreUB(indexes1, q, geo, level));
                        if (t.getUB() >= tau_i) {
                            t.setLB(ScoreLB(indexes1, q, geo, level));
                            Scand.add(t);
                            Collections.sort(Scand, new Comparator<EntryHelper>() {
                                @Override
                                public int compare(EntryHelper o1, EntryHelper o2) {
                                    if (o1.getLB() > o2.getLB())
                                        return -1;
                                    if (o1.getLB() < o2.getLB())
                                        return 1;
                                    return 0;
                                }
                            });
                            tau_i = (Scand).get(k - 1).getLB();
                            count = Scand.size();
                        }
                    }
                }
                //nonleaf
            } else {
                for (int i = 0; i < e.getNode().count(); i++) {
                    //System.out.println("No. of childs= "+((NonLeaf) e.getNode()).count());
                    er = new ListHelper();
                    er.setNode(((NonLeaf) e.getNode()).child(i));
                    Geometry geometry = e.getNode().geometry();
                    er.setUB(ScoreUB(indexes1, q, geometry, level));
                    if (er.getUB() > tau_i) {
                        heap.insert(er.hashCode(), -er.getUB());
                        htNode.put(er.hashCode(), er.getNode()); //htNode is a hash table that stores nodes
                    }
                }
            }

        }
        for (int i = 0; i < Scand.size(); i++) {
            EntryHelper eh1;
            eh1 = Scand.get(i);
            if (eh1.getUB() < tau_i) {
                Scand.remove(eh1);
                i--;
            }
        }

//        for (int i = 0; i < Scand.size(); i++){
//            EntryHelper eh1 = Scand.get(i);
//            //System.out.print(eh1.getNode().geometry().mbr().id()+",");
//        }
        System.out.println("SCandsize :"+Scand.size());
        //System.out.println(partialList.keySet());
        for (int i = 0; i < Scand.size(); i++) {
            EntryHelper eh = Scand.get(i);
            int objectID = eh.getNode().geometry().mbr().id();
            //System.out.println("objectID: "+objectID);
            int [] mbrID = partialList.get(eh.getNode().geometry().mbr().id());
            //System.out.println(objectID+","+mbrID[0]+","+mbrID[1]+","+eh.getLB()+","+eh.getUB()+","+tau_i);
            MapperOut valOut = new MapperOut(objectID, objectID, mbrID[0], mbrID[1], eh.getLB(), eh.getUB(), tau_i);
            String str = new String(String.valueOf(valOut.getInstID())+"\t"+String.valueOf(valOut.getObjID())+"\t"+ String.valueOf(valOut.getMBRID())+"\t"+String.valueOf(valOut.getTreeID())+"\t"+String.valueOf(valOut.getT_LB())+"\t"+String.valueOf(valOut.getT_UB()+"\t"+String.valueOf(tau_i)+"\0"));
            //System.out.println(str);
            value.set(str);

            String s = val.toString();
            int treeID = Integer.parseInt(s);
            //System.out.println(mbrIndexes[0].size());
            context.write(new IntWritable(treeID), value);
        }


    }
    private RTree<Object, Rectangle> loadTree(String x, Context context) throws IOException {
        String filePath = "/user/hduser/index1/file"+x;
        RTree<Object, Rectangle> rtree = RTree.star().maxChildren(10).create();
        //File file = new File("/home/nrai/Desktop/file"+x);
        try {
            rtree = deserialize(InternalStructure.SINGLE_ARRAY, filePath, context, createSerializer(),true);
            //rtree = deserialize(InternalStructure.SINGLE_ARRAY, file, createSerializer(),true);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return rtree;
    }


    private float ScoreLB(HashMap<Integer, List<RectangleDepth>> [] indexes1, float[] q, Geometry t, int level) {
        float prob = 0;
        //int count = 0;

        HashMap<Integer, List<RectangleDepth>> rhashMap;
        //for (HashMap<Integer, List<RectangleDepth>> rhashMap:indexes1
        //  ) {

        for (int i=0;i<indexes1.length;i++)
        {

            rhashMap = indexes1[i];
            Queue<List<RectangleDepth>> queue = new LinkedList<>();
            queue.add(rhashMap.get(0));

            while (!queue.isEmpty()) {
                List<RectangleDepth> listrd = queue.remove();

                if (listrd == null)
                    break;
                else {
                    for (RectangleDepth rd : listrd
                            ) {
                        int [] arr = new int[2];
                        //printRect(rd.getRectangle());
                        //printRect(t.mbr());
                        if (fullyDominate(rd.getRectangle().geometry(), t)) {
                            prob = prob + rd.getProb();
                        }else if (partiallyDominate(rd.getRectangle().geometry(), t)) {

                            if (rd.getDepth() == level) {
                                //prob = prob + 0;
                                //System.out.println("INSIDE");
                                arr[0] = rd.getRectangle().id();
                                arr[1] = i;
                                //System.out.println(t.mbr().id() +",arr0: "+arr[0]+",arr1: "+arr[1]);
                                partialList.put(t.mbr().id(), arr);
                            } else if(rd.getDepth() < level )
                                queue.add(rhashMap.get(rd.getRectangle().geometry().mbr().hashCode()));
                        }
                    }
                }
            }
            //count++;
        }
        return prob;
    }

    public float ScoreUB(HashMap<Integer, List<RectangleDepth>> [] indexes1, float[] q, Geometry t, int level) {
        double prob = 0;
        for (HashMap<Integer, List<RectangleDepth>> rhashMap:indexes1
                ) {

            Queue<List<RectangleDepth>> queue = new LinkedList<>();
            queue.add(rhashMap.get(0));

            while (!queue.isEmpty()) {
                List<RectangleDepth> listrd = queue.remove();
                if (listrd == null)
                    break;
                else {
                    for (RectangleDepth rd : listrd
                            ) {
                        if (fullyDominate(rd.getRectangle().geometry(), t))
                            prob = prob + rd.getProb();
                        else if (partiallyDominate(rd.getRectangle().geometry(), t))
                            if (rd.getDepth() == level)
                                prob = prob + rd.getProb();
                            else
                                queue.add(rhashMap.get(rd.getRectangle().geometry().mbr().hashCode()));
                    }
                }

            }
        }
        return (float) prob;
    }


    protected <T, S extends Geometry> HashMap<Integer, List<RectangleDepth>> getRectangleDepths(Node<T, S> node,
                                                                                                int depth, int level, int num) {

        List<RectangleDepth> list = new ArrayList<>();
        if(depth == -1){
            list.add(new RectangleDepth(node.geometry().mbr(), 0));
            rhashMap.put(0, list);
            getRectangleDepths(node, 0, level, num);

        }else if(depth < level) {

            if (node instanceof Leaf) {
                final Leaf<T, S> leaf = (Leaf<T, S>) node;
                for (final Entry<T, S> entry : leaf.entries()) {

                    list.add(new RectangleDepth(entry.geometry().mbr(), depth + 1));

                    //if(depth == level-1)
                    //mbrhashMap.put(entry.geometry().mbr().id(), (Node) entry);
                }
                rhashMap.put(node.geometry().mbr().hashCode(), list);
            } else {

                final NonLeaf<T, S> n = (NonLeaf<T, S>) node;
                for (int i = 0; i < n.count(); i++) {
                    list.add(new RectangleDepth(n.child(i).geometry().mbr(), depth + 1));

                    mbrhashMap.put(n.child(i).geometry().mbr().id(), (Node) n.child(i));
                }
                rhashMap.put(node.geometry().mbr().hashCode(), list);
                for (int i = 0; i < n.count(); i++) {

                    getRectangleDepths(n.child(i), depth + 1, level, num);

                }
            }
        }
        mbrIndexes[num] = mbrhashMap;
        return rhashMap;
    }

    private <R, S extends  Geometry> boolean fullyDominate(Geometry root, Geometry n) {

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

