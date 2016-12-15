package KNN;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Wang on 2016/12/14.
 */
public class ChooseSplitDim {
    /*用于KD tree 中选择最优划分属性**/
    enum SplitStandard{WidthMax, StdMax};

    SplitStandard standard;

    public ChooseSplitDim(SplitStandard splitStandard){
        this.standard = splitStandard;
    }

    public int calSplitDim(double[][] x, HashSet<Integer> id){
        //TODO
        int splitDim = 0;
        switch (standard){
            case WidthMax:
                splitDim = calSplitDimWidth(x, id);
                break;
            case StdMax:
                splitDim = calSplitDimStd(x,  id);
                break;
            default:
                break;
        }

        return  splitDim;
    }

    public int calSplitDimWidth(double[][] x, HashSet<Integer> id){
        /*选择划分属性时，选择区域最宽的属性维度为划分属性*/
        int splitDim = 0;

        double[] min = new double[x[0].length];
        double[] max = new double[x[0].length];
        for (int i = 0; i < min.length ; i++) {
            min[i] = Long.MAX_VALUE;
            max[i] = Long.MIN_VALUE;
        }
        Iterator<Integer> it = id.iterator();
        while (it.hasNext()){
            int t = it.next();
            for (int i = 0; i < min.length; i++) {
                if (x[t][i] < min[i]){
                    min[i] = x[t][i];
                }
                if (x[t][i] > max[i]){
                    max[i] = x[t][i];
                }
            }
        }
        // 计算区域宽度
        double deltaMax = Long.MIN_VALUE;
        for (int i = 0; i < min.length; i++) {
             if(Math.abs(max[i] - min[i]) > deltaMax ){
                 splitDim = i;
                 deltaMax = Math.abs(max[i] - min[i]);
             }
        }
        return  splitDim;
    }

    public int calSplitDimStd(double[][] x, HashSet<Integer> id){
        /*选择划分属性时，选择该属性方差最大所对应的属性*/
        int splitDim = 0;
        double[] std = new double[x[0].length];
        double[] mean = new double[x[0].length];
        for (int i = 0; i < std.length; i++) {
            std[i] = 0;
            mean[i] = 0;
        }

        Iterator<Integer> iterator = id.iterator();
        while (iterator.hasNext()){
            int t = iterator.next();
            for (int i = 0; i < mean.length; i++) {
                mean[i] += x[t][i];
            }
        }
        //求平均值
        for (int i = 0; i < mean.length; i++) {
            mean[i] = mean[i]/id.size();
        }
        //求方差
        iterator = id.iterator();
        while (iterator.hasNext()){
            int t = iterator.next();
            for (int i = 0; i < mean.length; i++) {
                std[i] += (x[t][i]-mean[i])*(x[t][i]-mean[i]);
            }
        }
        //选择std最大的那一维
        double stdMax = std[0];
        splitDim = 0;
        for (int i = 0; i < std.length; i++) {
            if (stdMax < std[i]){
                stdMax = std[i];
                splitDim = i;
            }
        }
        return  splitDim;
    }




}
