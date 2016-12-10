package SimplifiedSMO;
/**
 * Created by Wang on 2016/12/5.
 */

public class KernelFunction {

    enum kernel {Linear, Polynomia, Gauss};
    private kernel flag;
    private double gaussSigma = 0.0;
    private double polyDim = 0.0;

    public KernelFunction(kernel k){
        /**构造函数
         * */
        this.setKernel(k);
    }

    public KernelFunction(kernel k, double parameter){
        switch (k){
            case Polynomia:
                this.polyDim = parameter;
                break;
            case Gauss:
                this.gaussSigma = parameter;
                break;
        }
        setKernel(k);
    }


    void setKernel(kernel k){
        this.flag = k;
    }
    double calculateK(double[] x1, double[] x2){
        /*计算x1,x2的内积
        * */
        double innerProduct = 0;

        switch(flag){
            case Linear:
                innerProduct = kernelLinear(x1, x2);
                break;
            case Gauss:
                //高斯核函数
                innerProduct = kernelGauss(x1, x2);
                break;
            case Polynomia:
                //多项式核函数
                innerProduct = kernelPoly(x1, x2);
                break;
            default:
                innerProduct = kernelLinear(x1, x2);
                break;
        }
        return innerProduct;

    }
    double kernelLinear(double[] x1, double[] x2){
        /*  线性核函数的实现
        * */
        double innerProduct = 0;
        for(int i=0; i < x1.length; i++){
            innerProduct += x1[i]*x2[i];
        }
        return innerProduct;
    }

    double kernelPoly(double[] x1, double[] x2){
        /*  多项式核函数的实现
        * */
        double innerProduct = 0;
        for(int i=0; i < x1.length; i++){
            innerProduct += x1[i]*x2[i];
        }
        innerProduct = Math.pow(innerProduct + 1, polyDim);
        return innerProduct;
    }

    double kernelGauss(double[] x1, double[] x2){
        /* 高斯核函数的实现
        * */
        double innerProduct = 0;
        double delta = 0;
        //
        for (int i = 0; i < x1.length; i++) {
             delta += Math.pow(x1[i] - x2[i], 2);
        }

        innerProduct = Math.exp(-delta/(2*Math.pow(gaussSigma, 2)));

        return innerProduct;
    }

}
