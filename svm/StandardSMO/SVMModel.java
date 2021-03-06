package StandardSMO;

import java.util.LinkedList;

/**
 * Created by Wang on 2016/12/5.
 */
public class SVMModel {
    public double alpha[];
    public double b;
    public double x[][];
    public double C;
    public int y[];
    public LinkedList<Integer> svIndex; //存储支持向量的下标

    public KernelFunction K;

    public SVMModel(double x[][], int y[], double C, KernelFunction K){
        super();
        this.alpha = new double[y.length];
        this.initParameter();
        this.x = x;
        this.y = y;
        this.C = C;
        this.K = K;
        this.svIndex = new LinkedList<Integer>();
        this.updateSvIndex();
    }

    public void initParameter(){
        /*初始化变量
        * */
        for (int i = 0; i < this.alpha.length; i++) {
            this.alpha[i] = 0;
        }
        this.b = 0;
    }

    public void updateSvIndex(){
        for (int i = 0; i < alpha.length; i++) {
            if (alpha[i] > 0 && alpha[i] < C){
                svIndex.add(i);
            }
        }
    }

    public double calculateGx(double[] x){
        /*计算g(x)
        * */
        double gx =  0;
        for (int i = 0; i < this.alpha.length; i++) {
            gx += this.alpha[i]*this.y[i]*this.K.calculateK(x, this.x[i]);
        }
        gx += b;
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
