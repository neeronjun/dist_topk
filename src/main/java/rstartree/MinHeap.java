package rstartree;

import java.util.ArrayList;

public class MinHeap {

    private final ArrayList<HeapNode>  list;
    private HeapNode heapNode = new HeapNode();
    private float ub = 0;

    public MinHeap() {

        this.list = new ArrayList<HeapNode>();
    }

    public MinHeap(ArrayList<HeapNode> items) {

        this.list = items;
        buildHeap();
    }

    public void insert(int item, double ub) {
        heapNode = new HeapNode();
        heapNode.setNode(item);
        heapNode.setUB((float) ub);

        list.add(heapNode);
        int i = list.size() - 1;

        HeapNode parent = parent(i);
        int parentIndex = list.indexOf(parent);

        //System.out.println(String.valueOf(i));
        //System.out.println(String.valueOf(parentIndex));
        //System.out.println("Node=" + parent.getNode()+"key = " + parent.getUB());
        //System.out.println(String.valueOf(list.get(parentIndex).getUB()));

        // bubble-up until heap property is maintained
        while (parentIndex != i && list.get(parentIndex).getUB() > list.get(i).getUB()) {

            swap(i, parentIndex);
            i = parentIndex;
            parentIndex = list.indexOf(parent(i));
        }

        for(int j=0;j<=list.size()-1;j++){

            parent = list.get(j);
            //System.out.println("Node=" + parent.getNode()+"\tkey = " + parent.getUB()+ "\tj="+ String.valueOf(j));
        }
    }

    public void buildHeap() {

        for (int i = list.size() / 2; i >= 0; i--) {
            minHeapify(i);
        }
    }

    public HeapNode extractMin() {

        if (list.size() == 0) {

            throw new IllegalStateException("MinHeap is EMPTY");
        } else if (list.size() == 1) {

            HeapNode min = list.remove(0);
            return min;
        }

        // remove the last item ,and set it as new root
        HeapNode min = list.get(0);
        HeapNode lastItem = list.remove(list.size() - 1);
        list.set(0, lastItem);

        // bubble-down until heap property is maintained
        minHeapify(0);

        // return min key
        return min;
    }

    public void decreaseKey(int i, HeapNode key) {

        if (list.get(i).getUB() < key.getUB()) {

            throw new IllegalArgumentException("Key is larger than the original key");
        }

        list.set(i, key);
        HeapNode parent = parent(i);
        int parentIndex = list.indexOf(parent);

        // bubble-up until heap property is maintained
        while (i > 0 && list.get(parentIndex).getUB() > list.get(i).getUB()) {

            swap(i, parentIndex);
            i = parentIndex;
            parentIndex = list.indexOf(parent(i));
        }
    }

    private void minHeapify(int i) {

        //System.out.println(String.valueOf("MinHeap value = "+i));
        HeapNode left = left(i);
        HeapNode right = right(i);
        // find the smallest key between current node and its children.
        if(!isLeaf(i)) {
             if(list.get(i).getUB()>left.getUB()||list.get(i).getUB()>right.getUB()) {

                if (left.getUB() < right.getUB()) {
                    swap(i, list.indexOf(left));
                    minHeapify(list.indexOf(left));
                } else if(right.getUB()<left.getUB()) {
                    swap(i, list.indexOf(right));
                    minHeapify(list.indexOf(right));
                }
            }
        }
    }

    public HeapNode getMin() {

        return list.get(0);
    }

    public boolean isEmpty() {

        return list.size() == 0;
    }

    private HeapNode right(int i) {

        int index = 2 * i +2;
        if(index < list.size()) {
            return list.get(index);
        }else {
            return new HeapNode(0,Float.POSITIVE_INFINITY);
        }
    }

    private HeapNode left(int i) {
        int index = 2 * i + 1;
        if(index < list.size()) {
            return list.get(index);
        }else {
            return new HeapNode(0,Float.POSITIVE_INFINITY);
        }
    }

    private HeapNode parent(int i) {

        if (i % 2 == 1) {
            int par = i/2;
            return list.get(par);
        }
        int par = (i-1)/2;
        return list.get(par);
    }

    private void swap(int i, int parentIndex) {

        HeapNode temp = new HeapNode();
        temp = list.get(parentIndex);
        list.set(parentIndex, list.get(i));
        list.set(i, temp);
    }
    private boolean isLeaf(int pos)
    {
        if (pos >=  (list.size() / 2)  &&  pos <= list.size())
        {
            return true;
        }
        return false;
    }

}