package XGBoostDemo;

import java.util.HashSet;

/**
 * Created by Wang on 2016/12/9.
 */
public class Node {
    /*树的一个节点**/
    HashSet<Integer> x = new HashSet<Integer>(); //训练时表示在此节点的样本的索引
    Node nodeL = null; //左子节点
    Node nodeR = null; //右子节点
    int feature; //特征的索引
    double splitValue; //分裂的特征值
    double y; // 如果是叶子，表示叶子的权重
    boolean isLeaf = false; //是否为叶子节点

    public Node(int[] id){
        for (int i = 0; i < id.length; i++) {
            x.add(id[i]);
        }
    }

    private void setNodeL(Node L){
        this.nodeL =  L;
    }

    private void setNodeR(Node R){
        this.nodeR =  R;
    }

}
