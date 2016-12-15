package KNN;

/**
 * Created by Wang on 2016/12/12.
 */
public class CalDistance {
    /*计算不同的距离**/
    enum DistType {Cosine, Euclidean}; //可以添加其他的
    DistType distType;

    public CalDistance(DistType distType){
        this.distType = distType;
    }

    public double calDistance(double[] x1, double[] x2){
        double dist;
        switch (distType){
            case Cosine:
                dist = cosineDist(x1, x2);
                break;
            case Euclidean:
                dist = euclidDist(x1, x2);
                break;
            default:
                dist = euclidDist(x1, x2);
                break;
        }
        return dist;

    }


    public double cosineDist(double[] x1, double[] x2){
        /*计算cosine距离*/
        double dist = 0;
        //TODO
        return dist;
    }

    public double euclidDist(double[] x1, double[] x2){
        /*计算欧几里得距离*/
        double deltaSquareSum = 0;

        for (int i = 0; i < x1.length; i++) {
            double delta = x1[i]-x2[i];
            deltaSquareSum += delta*delta;
        }

        return  Math.sqrt(deltaSquareSum);
    }
}
