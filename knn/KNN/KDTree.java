package KNN;

import java.util.*;

/**
 * Created by Wang on 2016/12/12.
 */
public class KDTree {
    /*利用KD Tree实现KNN算法，更高效*/
    double alpha = 1.0; //大于1时，求次优解，但效率更高
    LinkedList<Node> tree = new LinkedList<Node>();
    CalDistance calDistance;
    int k; // 最临近点的个数
    int minimum; // 分裂的最少节点数
    PriorityQueue<NearstPoint> nearstDist; //保存距离最小的k个点的队列，保存每个点的索引和距离信息
    ChooseSplitDim  chooseSplitDim ; // 选择划分属性的类

    public KDTree(double[][] x, double alpha, int k, int minimum, CalDistance.DistType distType, ChooseSplitDim.SplitStandard splitStandard){
        /*构造函数*/
        this.alpha = alpha;
        this.minimum = minimum;
        this.k = k;
        nearstDist = new PriorityQueue<NearstPoint>(k);
        NearstPoint init = new NearstPoint(0, Double.MAX_VALUE);
        for (int i = 0; i < this.k; i++) {
            nearstDist.insert(init);
        }
        //this.x = x;
        int[] id = new int[x.length];
        for (int i = 0; i < id.length; i++) {
            id[i] = i;
        }
        Node root = new Node(id,  0);
        tree.add(root);
        this.calDistance = new CalDistance(distType);
        this.chooseSplitDim =  new ChooseSplitDim(splitStandard);
    }


    public void constructKDTree(double[][] x){
        /*构建KD Tree*/
        Queue queue = new Queue();
        queue.enqueue(tree.pop());
        while (queue.getSize() > 0){
            Node node = queue.dequeue();
            //如果点的数目很少就返回
            if(node.id.size() <= minimum){
                tree.add(node);
                continue;
            }
            // 选择划分属性
            node.splitDim = chooseSplitDim.calSplitDim(x, node.id);
            //  按 数据点的划分属性的值进行排序，得到排序后的索引信息
            int[] sortedId = getSortId(node, node.splitDim, x);
            // 选择中位点为划分属性的值
            node.splitValue = x[sortedId[(int)(Math.ceil(sortedId.length/2.0))-1]][node.splitDim];
            // 复制小于划分属性的值的节点的索引，构造左节点
            Node nodeL = new Node(Arrays.copyOfRange(sortedId,0, (int)(Math.ceil(sortedId.length/2.0))), node.depth+1);
            // 复制大于划分属性的值的节点的索引，构造右节点
            Node nodeR = new Node(Arrays.copyOfRange(sortedId,(int)(Math.ceil(sortedId.length/2.0)), sortedId.length), node.depth+1);
            nodeL.setNodeParent(node);
            nodeR.setNodeParent(node);
            node.setNodeL(nodeL);
            node.setNodeR(nodeR);
            // 入队列
            queue.enqueue(nodeL);
            queue.enqueue(nodeR);
            // 加入表示树的链表
            tree.add(node);
        }
    }

    public int[] getSortId(Node node, int i, double[][] x){
        /*按数据点的划分属性的值进行排序，得到排序后的索引信息*/
        TwoDimE[] col = new TwoDimE[node.id.size()];
        int[] id = new int[node.id.size()];
        Iterator<Integer> iterator = node.id.iterator();
        int cnt = 0;
        while (iterator.hasNext()){
            int j  = iterator.next();
            col[cnt] = new TwoDimE(x[j][i], j);
            cnt++;
        }
        TwoDimComp  twoDimComp = new TwoDimComp();
        Arrays.sort(col, twoDimComp);
        for (int j = 0; j < col.length; j++) {
            id[j] = col[j].key;
        }
        return  id;
    }

    public void searchKnn(double[] query, double[][] x){
        /*查询最近的k个点*/
        // 首先找到属于哪个叶子节点,
        Node node = tree.getFirst();
        while (node.nodeL != null){
            if (query[node.splitDim] > node.splitValue){
                node = node.nodeR;
            }else {
                node = node.nodeL;
            }
        }
        // 回溯寻找最邻近距离
        Iterator<Integer> iterator = node.id.iterator();
        while (iterator.hasNext()){
            int id = iterator.next();
            double dist = calDistance.calDistance(x[id], query);
            if (dist < nearstDist.getMax().dist){
                NearstPoint v = new NearstPoint(id, dist);
                nearstDist.delMax();
                nearstDist.insert(v);
            }
        }
        node.status = true;

        //int cnt = 0;
        node = node.nodeParent;
        while(true){
            // 节点深度为0， 遍历到根节点，结束
            if (node.depth == 0){
                break;
            }
            double delta = query[node.splitDim]-node.splitValue;

            if (delta > nearstDist.getMax().dist){
                // 比切分值大r， 在右边, 左子节点不相交。不用访问
                node.nodeL.status = true;
            }else if (delta < -nearstDist.getMax().dist){
                // 比切分值小r， 在左边,右子节点不相交, 不用访问
                node.nodeR.status = true;
            }

            // 判断是否左右子节点都已遍历
            if (node.nodeL.status && node.nodeR.status){
                //System.out.println(cnt++);
                node.status = true;
                node = node.nodeParent;
            }else {
                // 如果当前结点左节点已访问过，则访问右节点，否则左节点
                if (node.nodeL.status){
                    node = node.nodeR;
                }else {
                    node = node.nodeL;
                }
                // 如果当前结点为叶子节点，则遍历计算距离
                if (node.splitDim == -1) {
                    iterator = node.id.iterator();
                    while (iterator.hasNext()) {
                        int id = iterator.next();
                        double dist = calDistance.calDistance(x[id], query);
                        if (dist < nearstDist.getMax().dist) {
                            nearstDist.delMax();
                            NearstPoint v = new NearstPoint(id, dist);
                            nearstDist.insert(v);
                        }
                    }
                    node.status = true;
                    node = node.nodeParent;
                }
            }
        }
    }

    public void showKnnInfo(){
        /*显示最近的K个点的索引和相应的距离*/
        for (int i = 0; i < this.k; i++) {
            NearstPoint v = nearstDist.delMax();
            System.out.printf("%d, %f \n", v.index, v.dist);
        }
    }

    public static void main(String[] agrs){
        int k = 2;
        CalDistance.DistType distType = CalDistance.DistType.Euclidean;
        double[] query = {8,3};
        double[][] x =  {{2,1},{0.4, 0.77},{2.5,4},{4,6},{8,1},{7,2},{-2,3},{-4,-7},{5,-4},{-9,6},{-8,-1},{7,-2},{2.3,3.6},{-4.1,7.2},{5.5,-4.04}};
        double alpha = 1.0;
        int minimum = 3;
        ChooseSplitDim.SplitStandard splitStandard = ChooseSplitDim.SplitStandard.StdMax;
        KDTree kdTree = new KDTree(x, alpha, k, minimum, distType, splitStandard);
        kdTree.constructKDTree(x);
        kdTree.searchKnn(query, x);
        kdTree.showKnnInfo();
    }

}
