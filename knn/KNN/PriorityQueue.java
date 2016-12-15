package KNN;

import java.util.Comparator;

/**
 * Created by Wang on 2016/12/12.
 */
public class PriorityQueue<Key extends Comparable<Key>> {
    /*优先队列，用于按顺序存储在KNNOrign中计算时的距离，耗时logK*/
    //PriorityQueue
    private Key[] pq;
    private int N = 0 ;//store in 1,..., N-1
    private int maxN = 0;
    public PriorityQueue(int maxN){
        pq = (Key[]) new Comparable[maxN+1];
        this.maxN = maxN;
    }

    public boolean isEmpty(){
        return N == 0;
    }
    public int size(){
        return N;
    }
    public void insert(Key v){
        //N is too large enlarge the size of the array
        pq[++N] = v;
        swim(N);
    }

    public Key getMax(){
        return pq[1];
    }
    public Key delMax(){
        Key min = pq[1];
        exch(1, N--);
        pq[N+1] = null;
        sink(1);
        return min;
    }
    private boolean bigger(int i, int j){
        return pq[j].compareTo(pq[i]) > 0;
    }
    private void exch(int i, int j){
        Key  tmp = pq[i];
        pq[i] = pq[j];
        pq[j] = tmp;
    }
    private void swim(int k){
        //excute whten inserting the new Key
        while(k > 1 && bigger(k/2 ,k)){
            exch(k/2, k);
            k = k/2;
        }
    }
    private void sink(int k){
        //excute when del the minmum
        while(2*k <= N){
            int j = 2*k;
            if(j < N && bigger(j, j+1))
                j++;
            if(!bigger(k, j))
                break;
            exch(k, j);
            k = j;
        }
    }

    //test units
    public static void main(String[] args){
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(19);
        pq.insert(3);
        pq.insert(5);
        pq.insert(4);
        pq.insert(1);
        pq.insert(8);
        pq.insert(7);
        pq.insert(17);
        pq.insert(1);
        pq.insert(1);
        for (int i = 0; i < 5; i++) {
            int a = pq.delMax();
            System.out.println(a);
        }
        pq.insert(1);

    }
}
