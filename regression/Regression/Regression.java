package Regression;

/**
 * Created by WangJalin on 2016/12/17.
 */
public class Regression {
    enum RegressionType{ LS, Ridge, Lasso};

    RegressionType regressionType;
    UpdateModel.SolveMethod solveMethod;
    double C = 0;
    double[] w;
    UpdateModel updateModel;

    public Regression(RegressionType regressionType,UpdateModel.SolveMethod solveMethod, double[][] x ){
        this.regressionType = regressionType;
        this.solveMethod = solveMethod;
        this.w = new double[x[0].length];
        for (int i = 0; i < this.w.length; i++) {
            this.w[i] = 0;
        }
        this.updateModel = new UpdateModel(solveMethod);
    }

    public Regression(RegressionType regressionType,UpdateModel.SolveMethod solveMethod, double[][] x, double C ){
        this.regressionType = regressionType;
        this.solveMethod = solveMethod;
        this.w = new double[x[0].length];
        for (int i = 0; i < this.w.length; i++) {
            this.w[i] = 0;
        }
        this.updateModel = new UpdateModel(solveMethod);
        this.C = C;
    }

    public double predict(double[] x){
        /*计算预测值*/
        double y = 0;
        for (int i = 0; i < x.length; i++) {
            y += w[i]*x[i];
        }

        return  y;
    }

    public void solveReression(double[][] x, double[] y){
        /**求解更新模型参数*/
        this.updateModel.update(this, x, y);
    }
    

    public static void main(String[] args){
        /*测试用例*/
        double[][] xOld = {{0,0}, {1,1}, {0,2}, {1,3}, {2,5}, {3,0}};
        //预处理数据,加一列1
        double[][] x = new double[xOld.length][xOld[0].length+1];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                if (j == x[0].length-1){
                    x[i][j] = 1;
                }else {
                    x[i][j] = xOld[i][j];
                }
            }
        }

        double[] y = {0, 2, 2, 4, 7, 3};

        Regression regression = new Regression(RegressionType.Ridge, UpdateModel.SolveMethod.CoorDescent, x, 0);
        regression.solveReression(x, y);
        for (int i = 0; i < regression.w.length; i++) {
            System.out.println(regression.w[i]);
        }
    }
}
