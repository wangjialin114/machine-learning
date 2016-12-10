package XGBoostDemo;

import java.util.Comparator;

/**
 * Created by Wang on 2016/12/9.
 */
public class TwoDimComp implements Comparator<TwoDimE> {
    /*当选择最优分裂特征时，需要将样本按特征值排序，同时还要记住样本的索引，
    因此构建[特征值，索引]的二维数组，并且排序时已特征值的大小排序**/
    public final int compare(TwoDimE first, TwoDimE second){
        double val1 = first.value;
        double val2 = second.value;
        double diff = val1 - val2;
        if (diff > 0){
            return 1;
        }
        if (diff < 0){
            return  -1;
        }else {
            return 0;
        }
    }
}
