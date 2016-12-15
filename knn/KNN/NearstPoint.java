package KNN;

/**
 * Created by Wang on 2016/12/12.
 */
public class NearstPoint implements Comparable<NearstPoint>{
    /*一个最近邻点的信息，由索引和距离表示*/
    int index;
    double dist;

    public NearstPoint(int index, double dist){
        this.index = index;
        this.dist = dist;
    }
    @Override
    public int compareTo(NearstPoint o) {
        if (this.dist > o.dist){
            return  1;
        }if (this.dist < o.dist){
            return -1;
        }else {
            return 0;
        }
    }
}
