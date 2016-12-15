package KNN;

import java.util.Random;

/**
 * Created by Wang on 2016/12/12.
 */
public class KNN {
    KNNOrigin knnOrigin;
    KDTree kdTree;
    LSH lsh;

    public KNN(int k,  int minimum, double alpha, int h, int table, ChooseSplitDim.SplitStandard splitStandard, CalDistance.DistType distType, double[][] x, double[] query){
        this.knnOrigin = new KNNOrigin(k, distType, query, x);
        this.kdTree = new KDTree(x, alpha, k, minimum, distType, splitStandard);
        this.lsh = new LSH(k, h, table, x, CalDistance.DistType.Euclidean);
    }

    public static void main(String[] args){
        int k = 3;
        int minimum = 50;
        double alpha = 1;
        int dataDim = 8;
        int h = 15;
        int tableNum = 10;
        ChooseSplitDim.SplitStandard splitStandard = ChooseSplitDim.SplitStandard.StdMax;
        CalDistance.DistType distType = CalDistance.DistType.Euclidean;
        Random ra = new Random();
        double[][] x = new double[150000][dataDim];
        double[] query = new double[dataDim];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length/2; j++) {
                x[i][j] = ra.nextGaussian()*ra.nextInt(1000);
            }
            for (int j = x[0].length/2; j < x[0].length; j++) {
                x[i][j] = ra.nextInt(1000);
            }
        }
        for (int i = 0; i < query.length; i++) {
            query[i] = ra.nextInt(1000);
        }
        KNN knn = new KNN( k, minimum, alpha, h, tableNum, splitStandard, distType, x, query );
        //KNN
        long startTime = System.currentTimeMillis();
        knn.knnOrigin.searchKNN(x);
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("KNN原始方法花费时间" + time);
        knn.knnOrigin.showKnnInfo();
        // KD TREE
        knn.kdTree.constructKDTree(x);
        startTime = System.currentTimeMillis();
        knn.kdTree.searchKnn(query, x);
        endTime = System.currentTimeMillis();
        time = endTime - startTime;
        System.out.println("Kd-Tree方法花费时间" + time);
        knn.kdTree.showKnnInfo();
        //LSH
        knn.lsh.creatMultiHashTable(x);
        startTime = System.currentTimeMillis();
        knn.lsh.searchNNMultiHash(query, x);
        endTime = System.currentTimeMillis();
        time = endTime - startTime;
        System.out.println("LSH方法花费时间" + time);
        knn.lsh.showKnnInfo();
    }
}
