package DCD;

import java.util.HashMap;

/**
 * Created by Wang on 2016/12/5.
 */
public class SVMModel {
    public double alpha[];
    public double x[][];
    public double C;
    public int y[];
    public double w[];
    public HashMap<String, Double> kernelMatrix;

    public SVMModel(double x[][], int y[], double C){
        super();
        this.alpha = new double[y.length];
        this.w = new double[x[0].length];
        this.initParameter();
        this.x = x;
        this.y = y;
        this.C = C;
        this.kernelMatrix = new HashMap<String, Double>(1000);
    }

    public void initParameter(){
        /*初始化变量
        * */
        for (int i = 0; i < this.alpha.length; i++) {
            this.alpha[i] = 0;
        }
        for (int i = 0; i < this.w.length; i++) {
            this.w[i] = 0;
        }
    }

    public double calculateG(int id){
        /*计算导数G
        * */
        double gx = 0;

        gx = calculateGx(x[id]);
        gx = gx*y[id] - 1;
        return gx;
    }

    public double calculateGx(double[] x){
        /**计算g(x)
         *
         */
        double gx = 0;
        for (int i = 0; i < w.length; i++) {
            gx += w[i]*x[i];
        }
        return gx;
    }

    public int predictClass(double[] x){
        int predict_label = 0;
        //
        double gx = calculateGx(x);
        //predict_label 一定要是+1 或者 -1
        if (gx > 0 ){
            return +1;
        }else{
            return -1;
        }
    }
}
