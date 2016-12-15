package KNN;

/**
 * Created by Wang on 2016/12/12.
 */
public class KNNOrigin {
    /**最原始的KNN，效率很低，用优先队列实现*/
    PriorityQueue<NearstPoint> nearstDist; //保存距离最小的k个点的队列
    int k ; //需要query的点的数目
    CalDistance calDist;
    double[] query; //需要查询的点
    //double[][] x;

    public KNNOrigin(int k, CalDistance.DistType distType, double[] query, double[][] x){
        this.k = k;
        nearstDist = new PriorityQueue<NearstPoint>(k);
        NearstPoint init = new NearstPoint(0, Double.MAX_VALUE);
        for (int i = 0; i < k; i++) {
            nearstDist.insert(init);
        }
        calDist = new CalDistance(distType);
        this.query = query;
        //this.x = x;
    }

    public void searchKNN(double[][] x){
        /*查询最近的k个点*/
        double dist ;
        for (int i = 0; i < x.length; i++) {
            dist =calDist.calDistance(query, x[i]);
            // 与最大的距离比较
            if (dist < nearstDist.getMax().dist){
                //先删除队列中距离最大的，在插入更新
                nearstDist.delMax();
                NearstPoint v = new NearstPoint(i, dist);
                nearstDist.insert(v);
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

    public static void main(String[] agrs){
        int k = 2;
        CalDistance.DistType distType = CalDistance.DistType.Euclidean;
        double[] query = {8, 3};
        double[][] x =  {{2,1},{0.4, 0.77},{2.5,4},{4,6},{8,1},{7,2},{-2,3},{-4,-7},{5,-4},{-9,6},{-8,-1},{7,-2},{2.3,3.6},{-4.1,7.2},{5.5,-4.04}};
        KNNOrigin knn = new KNNOrigin(k, distType, query, x);
        knn.searchKNN(x);
        knn.showKnnInfo();
    }

}

