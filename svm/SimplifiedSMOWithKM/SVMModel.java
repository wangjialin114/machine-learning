package SimplifiedSMOWithKM;

import java.util.HashMap;

/**
 * Created by Wang on 2016/12/5.
 */
public class SVMModel {
    public double alpha[];
    public double b;
    public double x[][];
    public double C;
    public int y[];
    public HashMap<String, Double> kernelMatrix;

    public KernelFunction K;

    public SVMModel(double x[][], int y[], double C, KernelFunction K){
        super();
        this.alpha = new double[y.length];
        this.initParameter();
        this.x = x;
        this.y = y;
        this.C = C;
        this.K = K;
        this.kernelMatrix = new HashMap<String, Double>(1000);
    }

    public void initParameter(){
        /*初始化变量
        * */
        for (int i = 0; i < this.alpha.length; i++) {
            this.alpha[i] = 0;
        }
        this.b = 0;
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

    public double calculateGx(int j){
        /*重载计算g(x), 在训练时用
        * */
        double gx =  0;
        for (int i = 0; i < this.alpha.length; i++) {
            String key  = (i < j) ? ( i + "s" + j) : ( j + "s" + i);
            double innerProduct;
            if (kernelMatrix.containsKey(key)){
                innerProduct = kernelMatrix.get(key);
            }else{
                innerProduct = K.calculateK(x[j], x[i]);
                kernelMatrix.put(key, innerProduct);
            }

            gx += this.alpha[i]*this.y[i]*innerProduct;
        }
        gx += b;
        return gx;
    }

    public int predictClass(double[] x){
        /*预测x的类别
        * */
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
