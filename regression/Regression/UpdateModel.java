package Regression;


/**
 * Created by WangJalin on 2016/12/17.
 */
public class UpdateModel {
    enum SolveMethod{GradDescent, CoorDescent};
    SolveMethod solveMethod;
    double epsilon = 0.001;
    int maxIterNum = 1;
    double eta = 0.001;  //梯度下降法的shrinkage参数

    public UpdateModel(SolveMethod solveMethod){
        this.solveMethod = solveMethod;
    }

    public void update(Regression regression, double[][] x, double[] y){
        /*更新模型*/
        switch (solveMethod){
            case GradDescent:
                updateGradDescent(regression, x, y);
                break;
            case CoorDescent:
                updateCoorDescent(regression, x, y);
                break;
            default:
                updateGradDescent(regression, x, y);
                break;
        }
    }


    public void updateCoorDescent(Regression regression, double[][] x, double[] y){
        /*坐标下降法更新模型参数*/
        switch (regression.regressionType) {
            case LS:
                updateCoorDescentLS(regression, x, y);
                break;
            case Ridge:
                updateCoorDescentRidge(regression, x, y);
                break;
            case Lasso:
                updateCoorDescentLasso(regression, x, y);
                break;
            default:
                updateCoorDescentRidge(regression, x, y);
                break;
        }
    }

    public void updateCoorDescentLS(Regression regression, double[][] x, double[] y){
        /*用坐标下降法求解原始的最小二乘法回归模型**/
        updateCoorDescentRidge(regression, x, y);
    }

    public void updateCoorDescentRidge(Regression regression, double[][] x, double[] y){
        /**用坐标下降法求解脊回归模型*/
        double rssPartialNorm = rssPartialNorm(regression, x, y);
        double[] zj = preComputeZj(x);
        int iterNum = 0;
        while ((rssPartialNorm > epsilon) && (iterNum < maxIterNum)){
            for (int i = 0; i < x[0].length; i++) {
                double p = calP(regression, x, y, i);
                System.out.println(p);
                System.out.println(zj[i]);
                regression.w[i] = p/((regression.C+1)*(zj[i]));
                System.out.println(regression.w[i]);
            }
            rssPartialNorm = rssPartialNorm(regression, x, y);
            iterNum ++;
        }
    }

    public void updateCoorDescentLasso(Regression regression, double[][] x, double[] y){
        /*用坐标下降法求解Lasso模型**/
        double rssPartialNorm = rssPartialNorm(regression, x, y);
        double[] zj = preComputeZj(x);
        int iterNum = 0;
        while ((rssPartialNorm > epsilon) && (iterNum < maxIterNum)){
            for (int i = 0; i < x[0].length; i++) {
                double p = calP(regression, x, y, i);
                if (p < -regression.C/2){
                    regression.w[i] = (p + regression.C/2)/zj[i];
                }else if (p > regression.C/2){
                    regression.w[i] = (p - regression.C/2)/zj[i];
                }else {
                    regression.w[i] = 0;
                }
            }
            rssPartialNorm = rssPartialNorm(regression, x, y);
            iterNum ++;
        }
    }

    public void updateGradDescent(Regression regression, double[][] x, double[] y){
        /*梯度下降法更新模型参数*/
        switch (regression.regressionType) {
            case LS:
                updateGradDescentLS(regression, x, y);
                break;
            case Ridge:
                updateGradDescentRidge(regression, x, y);
                break;
            case Lasso:
                updateGradDescentLasso(regression, x, y);
                break;
            default:
                updateGradDescentRidge(regression, x, y);
                break;
        }
    }

    public void updateGradDescentLS(Regression regression, double[][] x, double[] y){
        /*用梯度下降法求解原始的最小二乘法回归模型
        * 可以直接复用更新ridge regression的函数，只是此时C=0**/
        updateGradDescentRidge(regression, x, y);
    }

    public void updateGradDescentRidge(Regression regression, double[][] x, double[] y){
        /**用梯度下降法求解脊回归模型*/
        double rssPartialNorm = rssPartialNorm(regression, x, y);
        int iterNum = 0;
        while ((rssPartialNorm > epsilon) && (iterNum < maxIterNum)){
            double[] partial  = calPartial(regression, x, y);
            for (int i = 0; i < partial.length; i++) {
                regression.w[i] = regression.w[i]*(1-2*this.eta*regression.C) - this.eta*partial[i];
            }
            rssPartialNorm = rssPartialNorm(regression, x, y);
            iterNum ++;
        }
    }

    public void updateGradDescentLasso(Regression regression, double[][] x, double[] y){
        /*用梯度下降法求解Lasso模型**/
        double rssPartialNorm = rssPartialNorm(regression, x, y);
        int iterNum = 0;
        while ((rssPartialNorm > epsilon) && (iterNum < maxIterNum)){
            double[] partial  = calPartial(regression, x, y);
            for (int i = 0; i < partial.length; i++) {
                if (partial[i] < -regression.C/2){
                    regression.w[i] = regression.w[i] - this.eta*(partial[i] + regression.C/2);
                }else if (partial[i] > regression.C/2){
                    regression.w[i] = regression.w[i] - this.eta*(partial[i] - regression.C/2);
                }
            }
            rssPartialNorm = rssPartialNorm(regression, x, y);
            iterNum ++;
        }
    }

    public double[] calResidual(Regression regression, double[][] x, double[] y){
        /*计算残差*/
        double[]  residual = new double[x.length];

        for (int i = 0; i < residual.length; i++) {
            residual[i] = 0;
        }
        for (int i = 0; i < x.length; i++) {
            double yHat = 0;
            for (int j = 0; j < x[0].length; j++) {
                yHat += regression.w[j]*x[i][j];
            }
            residual[i] = y[i] - yHat;
        }

        return residual;
    }

    public double[] calPartial(Regression regression, double[][] x, double[] y){
        /**/
        double[] partial = new double[x[0].length];

        // 初始化
        for (int i = 0; i < partial.length; i++) {
            partial[i] = 0;
        }
        // 计算残差
        double[] residual  = calResidual(regression, x, y);

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                partial[j] += -2*x[i][j]*residual[i];
            }
        }

        return  partial;
    }

    public double rssPartialNorm(Regression regression, double[][] x, double[] y){
        double partialNorm = 0;
        double[] partial;

        partial = calPartial(regression, x, y);

        for (int i = 0; i < partial.length; i++) {
            partialNorm += partial[i]*partial[i];
        }

        return Math.sqrt(partialNorm);
    }

    
    public double[] preComputeZj(double[][] x){
        /*在使用Lasso回归更新时的归一化因子*/
        double[] zj = new double[x[0].length];

        for (int i = 0; i < zj.length; i++) {

        }
        for (int i = 0; i < zj.length; i++) {
            for (int j = 0; j < x.length; j++) {
                zj[i] += x[j][i]*x[j][i];
            }
            System.out.println(zj[i]);
        }

        return zj;
    }


    public double calP(Regression regression, double[][] x, double[] y, int index){
        double p = 0;

        for (int i = 0; i < x.length; i++) {
            double yHatExceptJ = 0;
            for (int j = 0; j < x[0].length; j++) {
                if (j != index){
                    yHatExceptJ +=  regression.w[j]* x[i][j];
                }
            }
            p += (y[i] - yHatExceptJ)*x[i][index];
        }
        return p;
    }

}
