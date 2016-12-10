package XGBoostDemo;

import java.util.LinkedList;

/**
 * Created by Wang on 2016/12/9.
 */
public class Queue {
    /*利用LinkedList实现队列,
    * 用于实现树分裂过程中的节点
    * **/

    public LinkedList<Node> queue = new LinkedList<Node>();

    public void enqueue(Node e){
        //入队列
        queue.addLast(e);
    }

    public Node dequeue(){
        //出队列
        return queue.removeFirst();
    }

    public int getSize(){
        return  queue.size();
    }

}
