package KNN;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Wang on 2016/12/12.
 */
public class LSH {
    int k;
    int h;
    int tableNum; // hash表的数目
    double[][] lines; // 表示line的参数
    HashSet<Integer>[] hashTable; // 单个的hash表
    HashSet<Integer>[][] multiHashTable; // 多个hash表
    CalDistance calDistance;
    PriorityQueue<NearstPoint> nearstDist; //保存距离最小的k个点的队列

    public LSH(int k, int h, int tableNum, double[][] x, CalDistance.DistType distType){
        /*构造函数，初始化*/
        this.k = k;
        this.h = h;
        this.tableNum = tableNum;
        this.lines = new double[h][x[0].length];
        this.multiHashTable = new HashSet[tableNum][ Math.max(Math.round((float)Math.pow(2, this.h)), 1)];
        for (int i = 0; i < multiHashTable.length; i++) {
            for (int j = 0; j < multiHashTable[0].length; j++) {
                multiHashTable[i][j] = new HashSet();
            }
        }
        this.hashTable = new HashSet[ Math.max(Math.round((float)Math.pow(2, this.h)), 1)];;
        for (int i = 0; i < hashTable.length; i++) {
            hashTable[i] = new HashSet<>();
        }
        nearstDist = new PriorityQueue<NearstPoint>(k);
        NearstPoint init = new NearstPoint(0, Double.MAX_VALUE);
        for (int i = 0; i < this.k; i++) {
            nearstDist.insert(init);
        }
        this.calDistance = new CalDistance(distType);
    }
    public void drawRandomLines(){
        /**随机产生分割区间的线*/
        Random ra = new Random();

        for (int i = 0; i < this.h; i++) {
            for (int j = 0; j < lines[0].length; j++) {
                if (ra.nextFloat() > 0.5){
                    lines[i][j] = ra.nextFloat();
                }else {
                    lines[i][j] = -ra.nextFloat();
                }
            }
        }
    }

    public String searchBin(double x[]){
        /**寻找点属于哪个区间箱子，二进制表示*/
        String index = "";

        for (int i = 0; i < this.h; i++) {
            index += getOneBit(lines[i], x);
        }
        return index;
    }

    public StringBuilder[] getFlipBin(String index){
        /*获得翻转一个bit的箱子的所有的二进制编号*/
        StringBuilder[] flipResult = new StringBuilder[this.h+1];
        for (int i = 0; i < this.h; i++) {
            if (index.charAt(i) == '0'){
                flipResult[i] = new StringBuilder(index).replace(i, i+1, "1");
            }else {
                flipResult[i] = new StringBuilder(index).replace(i, i+1, "0");
            }

        }
        flipResult[h] = new StringBuilder(index);
        return flipResult;
    }

    public int getOneBit(double[] line, double[] x){
        /*根据一条线获得箱子二进制索引的一个bit表示*/
        double result = 0.0;
        for (int i = 0; i < line.length; i++) {
            result += line[i]*x[i];
        }
        if(result < 0){
            return 0;
        }else {
            return 1;
        }
    }

    public void creatMultiHashTable(double[][] x){
        /*创建多个hash表*/
        String index ;
        for (int i = 0; i < tableNum; i++) {
            drawRandomLines();
            for (int j = 0; j < x.length; j++) {
                index = searchBin(x[j]);
                multiHashTable[i][Integer.parseUnsignedInt(index, 2)].add(j);
            }
        }
    }

    public void searchNN(double[] query, double[][] x){
        /*寻找最近邻点的信息,只通过一个hash表中的多个箱子bin（翻转bit得到）*/
        //确定query点落在哪个里面
        String index = searchBin(query);
        StringBuilder[] flipBin = getFlipBin(index);
        Iterator<Integer> ra;
        for (int i = 0; i < flipBin.length; i++) {
            ra = hashTable[Integer.parseUnsignedInt(new String(flipBin[i]), 2)].iterator();
            while (ra.hasNext()){
                int id = ra.next();
                double dist = calDistance.calDistance(query, x[id]);
                if (dist < nearstDist.getMax().dist){
                    NearstPoint v = new NearstPoint(id, dist);
                    nearstDist.delMax();
                    nearstDist.insert(v);
                }
            }
        }
    }

    public void creatHashTable(double[][] x){
        String index ;
        drawRandomLines();
        for (int i = 0; i < x.length; i++) {
            index = searchBin(x[i]);
            hashTable[Integer.parseUnsignedInt(index, 2)].add(i);
        }
    }

    public void searchNNMultiHash(double[] query, double[][] x){
        /*寻找最近邻点的信息,通过多个hash表中同一个二进制索引的箱子*/
        // 确定query点落在哪个箱子bin里面
        for (int i = 0; i < tableNum; i++) {
            String index = searchBin(query);
            Iterator<Integer> ra;
            ra = multiHashTable[i][Integer.parseUnsignedInt(new String(index), 2)].iterator();
            while (ra.hasNext()){
                int id = ra.next();
                double dist = calDistance.calDistance(query, x[id]);
                if (dist < nearstDist.getMax().dist){
                    NearstPoint v = new NearstPoint(id, dist);
                    nearstDist.delMax();
                    nearstDist.insert(v);
                }
            }
        }
    }

    public void showKnnInfo(){
        /*显示最近的K个点的索引和相应的距离*/
        for (int i = 0; i < this.k; i++) {
            NearstPoint v = nearstDist.delMax();
            System.out.printf("%d, %f \n", v.index, v.dist);
        }
    }

    public static void main(String[] args){
        double[] query = {8,3};
        double[][] x =  {{2,1},{0.4, 0.77},{2.5,4},{4,6},{8,1},{7,2},{-2,3},{-4,-7},{5,-4},{-9,6},{-8,-1},{7,-2},{2.3,3.6},{-4.1,7.2},{5.5,-4.04}};
        int k = 5;
        int h = 2;
        int tableNum = 2;
        LSH lsh = new LSH(k, h, tableNum,  x, CalDistance.DistType.Euclidean);
        lsh.creatMultiHashTable(x);
        lsh.searchNNMultiHash(query, x);
        lsh.showKnnInfo();
    }
}
