package KNN;

/**
 * Created by Wang on 2016/12/9.
 */
public class TwoDimE {
    /*当选择最优分裂特征时，需要将样本按特征值排序，同时还要记住样本的索引，
    因此构建[特征值，索引]的元素类**/
    double value;
    int key;

    public TwoDimE(double value, int key){
        this.value = value;
        this.key = key;
    }
}
