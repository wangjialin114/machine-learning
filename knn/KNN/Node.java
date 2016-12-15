package KNN;

import java.util.HashSet;

/**
 * Created by Wang on 2016/12/12.
 */
public class Node {
    /*k-d tree中的一个树节点的结构**/
    int splitDim = -1;
    double splitValue;
    HashSet<Integer> id = new HashSet<Integer>();
    int depth=0;
    Node nodeR = null;
    Node nodeL = null;
    Node nodeParent = null;
    boolean status = false; // false 代表未访问或者剪枝， true代表已访问过或者是被剪枝掉的

    public Node(int[] id, int depth){
        for (int i = 0; i < id.length; i++) {
            this.id.add(id[i]);
        }
        this.depth = depth;
    }

    public void setNodeL(Node L){
        this.nodeL =  L;
    }

    public void setNodeR(Node R){
        this.nodeR =  R;
    }

    public void setNodeParent(Node parent){
        this.nodeParent =  parent;
    }


}
