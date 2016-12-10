package XGBoostDemo;

/**
 * Created by WangJalin on 2016/12/10.
 */
public class Predict {
    Tree[] trees;//用于预测的树的模型

    public void setTrees(Tree[] trees){
        this.trees = trees;
    }

    public double predictOneTree(Tree tree, double[] x){
        /*预测x在tree的y值*/
        double predict = 0.0;
        Node node = tree.tree.getFirst();
        for (int i = 0; i < tree.tree.size(); i++) {
            if (node.isLeaf){

                return node.y;
            }else {
                if (x[node.feature] <= node.splitValue){
                    node = node.nodeL;
                }else {
                    node = node.nodeR;
                }
            }

        }
        return  predict;
    }

    public double predictTotal(double[] x){
        /**预测样本x的最终值y*/
        double predict = 0.0;
        for (int i = 0; i < trees.length; i++) {
            //System.out.println(i);
            predict += predictOneTree(trees[i], x);
        }
        return  predict;
    }
}
