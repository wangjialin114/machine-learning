package DCD;


import java.util.Random;

/**
 * Created by Wang on 2016/12/5.
 */
public class SVMCoorDescent {

    SVMModel model;
    int optIndex;
    double epsilon;
    int maxEpoch;

    public SVMCoorDescent(double[][] x, int[] y, double C, double epsilon, int maxEpoch){
        /*构造函数*/
        this.model = new SVMModel(x, y, C);
        this.epsilon = epsilon;
        this.maxEpoch = maxEpoch;
    }

    public void coorDescentTrain(){
        int iterNum = 0;
        Random ra = new Random();
        while(iterNum<maxEpoch){
            // 随机选择一个优化的点
            optIndex = ra.nextInt(model.y.length);
            //计算G
            double G = model.calculateG(optIndex);
            //计算PG
            double PG = calculatePG(G);
            //更新w
            double alphaDelta = updateAlpha(G);
            updateW(alphaDelta, PG);
            System.out.println(iterNum);
            iterNum++;
        }
    }

    public double calculatePG(double G){
        /*计算Projection Gradient PG*/
        double PG;
        if (Math.abs(model.alpha[optIndex]) < epsilon ){
            PG = Math.min(G, 0);
        }else if (Math.abs(model.alpha[optIndex]-model.C) < epsilon){
            PG = Math.max(G, 0);
        }else{
            PG = G;
        }
        return PG;
    }

    public double updateAlpha(double G){
        /*更新alpha*/
        double Qii = 0;
        double alphaOld =  model.alpha[optIndex];

        for (int i = 0; i < model.x[optIndex].length; i++) {
            Qii += model.x[optIndex][i]*model.x[optIndex][i];
        }
        model.alpha[optIndex] = Math.min(Math.max(model.alpha[optIndex]-G/Qii, 0), model.C);
        return  model.alpha[optIndex] - alphaOld;
    }

    public void updateW(double alphaDelta, double PG){
        /*更新W*/
        if(Math.abs(PG) > epsilon){
            double ratio = alphaDelta*model.y[optIndex];
            for (int i = 0; i < model.w.length; i++) {
                model.w[i] += ratio*model.x[optIndex][i];
            }
        }
    }

    public double predict(double[][] x, int[] y){
        double correctNum = 0;
        for (int i = 0; i < y.length; i++) {
            int label = model.predictClass(x[i]);
            if(label == y[i]){
                correctNum ++;
            }
        }
        double prob = correctNum/y.length;
        return  prob;
    }

    // Test case
    public static void main(String[] args){

        SVMFileReader reader = new SVMFileReader("..\\heart_scale");
        reader.getSVMData(260);
        // 注意x[i]的后面都要加上一个元素1， 把b当成w的最后一个分量，已在uSVMFileReader中添加
        SVMCoorDescent scd = new SVMCoorDescent(reader.x, reader.y , 1, 0.001, 5000);

        System.out.println("开始训练...");
        long startTime = System.currentTimeMillis();
        scd.coorDescentTrain();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("训练结束");
        System.out.println("训练时间为: " +  time);
        //开始预测
        //数据是原来用于训练的数据
        System.out.println("开始预测...");
        double probability = scd.predict(reader.x, reader.y);
        System.out.println("预测正确率为：" + probability);
    }

}
