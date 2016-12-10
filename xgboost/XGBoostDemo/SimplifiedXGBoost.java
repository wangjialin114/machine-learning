package XGBoostDemo;

/**
 * Created by Wang on 2016/12/9.
 */
public class SimplifiedXGBoost {
    //TODO maxDepth
    int maxDepth; //
    double[][] x;
    int[] y;
    int numTrees; //数的数目
    double gamma;// 对树结构的惩罚，叶子节点数目
    double lambda; // L2规范化惩罚系数
    Tree[] trees;
    double minimum; //每个叶子的最小的节点数目
    double[] yPre; // 前一轮的y(t-1)
    Predict predict = new Predict();//用于计算最终的预测值类


    public SimplifiedXGBoost(double[][] x, int[] y,int numTrees, double minimum, double gamma, double lambda){
        this.x = x;
        this.y = y;
        this.numTrees = numTrees;
        this.gamma = gamma;
        this.minimum = minimum;
        this.trees = new Tree[numTrees];
        this.lambda = lambda;
        this.yPre = new double[y.length];
        for (int i = 0; i < yPre.length; i++) {
            this.yPre[i] = 0;
        }

    }
    public void xgbTrain(){
        /**训练得到模型*/
        for (int i = 0; i < numTrees ; i++) {
            //生成树
            GrowTree gt = new GrowTree( this.x, this.y,  gamma, lambda, minimum, yPre);
            gt.splitNode(); //生成树
            trees[i] = gt.tree;
            this.yPre = gt.yPre;
        }
    }

    public double predict(double[][] x, int[] y){
        /*预测正确率*/
        double correctNum = 0;
        for (int i = 0; i < y.length; i++) {
            double val = predict.predictTotal(x[i]);
            int label;
            if(val > 0){
                label = 1;
            }else {
                label = -1;
            }
            if(label == y[i]){
                correctNum ++;
            }
        }
        double prob = correctNum/y.length;
        return  prob;
    }



    public static void main(String[] args){
        // 读取文件
        //double[][] x = {{20, 175, 29},{13, 135, 21},{21, 137, 28},{12, 176, 24}};
        //int[] y = {1, 1, -1, -1};
        double gamma = 10;
        double lambda = 2;
        double minimum = 3;
        int numTrees = 500;
        Utils reader = new Utils("..\\heart_scale");
        reader.getSVMData(260);
        SimplifiedXGBoost xgb = new  SimplifiedXGBoost(reader.x, reader.y, numTrees,  minimum,  gamma, lambda);
        System.out.println("开始训练...");
        long startTime = System.currentTimeMillis();
        xgb.xgbTrain();
        xgb.predict.setTrees(xgb.trees);
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("训练结束");
        System.out.println("训练时间为: " +  time);
        System.out.println("开始预测...");
        double probability = xgb.predict(reader.x, reader.y);
        System.out.println("预测正确率为：" + probability);

    }
}
