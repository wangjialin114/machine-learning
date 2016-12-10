package XGBoostDemo;

import java.util.*;

/**
 * Created by Wang on 2016/12/9.
 */
public class GrowTree {
    /*如何新生成一个tree*/
    int depth;
    Node root;
    double[][] x;
    int[] y;
    double[] yPre;
    Tree tree;
    //Feature feaRoot;
    int featureNum;
    //int instanceNum;
    double gamma;
    double lambda;
    double[] g;
    double[] h;
    double minimum;
    double shrinkage = 0.1;


    public GrowTree(double[][] x, int[] y, double gamma, double lambda, double minimum , double[] yPre){
        this.depth = 2;
        int[] id = new int[y.length];
        for (int i = 0; i < id.length; i++) {
            id[i] = i;
        }
        this.tree = new Tree();
        this.root = new Node(id);
        this.x = x;
        this.y = y;
        this.featureNum = x[0].length;
        this.yPre = new double[y.length];
        this.g = new double[y.length];
        this.h = new double[y.length];
        for (int i = 0; i < yPre.length; i++) {
            this.yPre[i] = yPre[i];
            this.g[i] = 0;
            this.h[i] = 0;
        }
        this.gamma = gamma;
        this.lambda = lambda;
        this.minimum = minimum;
        this.updateg();
        this.updateh();
    }


    public void splitNode(){
        Queue nodeQueue = new Queue();
        //插入根节点
        nodeQueue.enqueue(root);

        while (nodeQueue.getSize() > 0){
            // 出队列
            Node q = nodeQueue.dequeue();
            // 分裂
            double G = calculateG(q);
            double H = calculateH(q);

            // 停止分裂的条件
            HashSet<Integer> result = new HashSet<Integer>();
            Iterator<Integer> iterator = q.x.iterator();
            while (iterator.hasNext()){
                result.add(y[iterator.next()]);
            }
            if ((q.x.size() < minimum) || (result.size() < 2)){
                //叶子节点
                q.y = -calculateG(q)/(calculateH(q)+lambda);
                //更新yPre
                Iterator<Integer> iterator2 = q.x.iterator();
                while (iterator2.hasNext()){
                    int id = iterator2.next();
                    yPre[id] = yPre[id] + shrinkage*q.y;
                }
                //
                q.isLeaf = true;
                tree.tree.addLast(q);
                continue;
            }
            //
            double deer = 0;
            int splitId = 0;
            int splitFeature = 0;
            int[] splitIdx = new int[q.x.size()];
            // 选择最佳分裂的特征和特征值
            for (int i = 0; i < featureNum; i++) {
                double GL = 0;
                double HL = 0;
                int[] idx;
                idx = getSortId(q, i);

                for (int j = 0; j < q.x.size(); j++) {
                    GL = GL + g[idx[j]];
                    double GR = G - GL;
                    HL = HL + h[idx[j]];
                    double HR = H - HL;
                    if(Math.max(deer, GL*GL/(HL+lambda) + GR*GR/(HR+lambda)-G*G/(H+lambda)-gamma)> deer){
                        deer = Math.max(deer, GL*GL/(HL+lambda) + GR*GR/(HR+lambda)-G*G/(H+lambda)-gamma);
                        splitId = j;
                        splitFeature = i;
                        splitIdx = idx;
                    }
                }
            }
            // deer为增益
            if (deer <= 0.001){
                q.y = -calculateG(q)/(calculateH(q)+lambda);
                q.isLeaf = true;
                //更新yPre
                Iterator<Integer> iterator2 = q.x.iterator();
                while (iterator2.hasNext()){
                    int id = iterator2.next();
                    yPre[id] = yPre[id] + shrinkage*q.y;
                }
                tree.tree.addLast(q);
            }else{
                //分裂
                Node nodeL = new Node(Arrays.copyOfRange(splitIdx,0, splitId+1));// TODO
                Node nodeR = new Node(Arrays.copyOfRange(splitIdx,splitId+1, splitIdx.length));
                q.nodeL = nodeL;
                q.nodeR = nodeR;
                q.feature = splitFeature;
                q.splitValue = x[splitId][splitFeature];
                q.isLeaf = false;
                tree.tree.addLast(q);
                nodeQueue.enqueue(nodeL);
                nodeQueue.enqueue(nodeR);
            }
        }


    }


    public int[] getSortId(Node node, int i){
        TwoDimE[] col = new TwoDimE[node.x.size()];
        int[] id = new int[node.x.size()];
        Iterator<Integer> iterator = node.x.iterator();
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


    public void updateg(){
        for (int i = 0; i < g.length; i++) {
            //对应square loss的一阶导数
            g[i] = 2*(yPre[i]-y[i]); //
        }
    }


    public void updateh(){
        for (int i = 0; i < h.length; i++) {
            //对应square loss的二阶导数
            h[i] = 2;
        }
    }


    public double calculateG(Node node){
        //TODO
        double G = 0.0;

        Iterator<Integer> iterator = node.x.iterator();
        while (iterator.hasNext()){
            G += g[iterator.next()];
        }

        return  G;
    }

    public double calculateH(Node node){
        //TODO
        double H = 0.0;

        Iterator<Integer> iterator = node.x.iterator();
        while (iterator.hasNext()){
            H += h[iterator.next()];
        }

        return H;
    }
/*
    public static void main(String[] args){
        double[][] x = {{20, 175, 24},{13, 135, 21},{21, 137, 22},{12, 176, 24}};
        int[] y = {1, 1, -1, -1};
        double gamma = 1;
        double minimum = 2;
        GrowTree gt = new GrowTree( x, y, gamma,minimum);
        gt.splitNode();

    }*/

}
