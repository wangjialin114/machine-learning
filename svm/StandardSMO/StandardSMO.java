package StandardSMO;


import java.util.Random;

/**
 * Created by Wang on 2016/12/5.
 */
public class StandardSMO {
    public double[] E; //存储E
    public int[] indexOpt = new int[2]; //优化的参数的索引
    KernelFunction K;
    SVMModel model;
    int maxEpoch = 0; //最大迭代次数
    double epsilon;


    public StandardSMO(double[][] x, int[] y, double C, KernelFunction.kernel k, double epsilon, int maxEpoch){
        /*构造函数
        * */
        //init alpha and b
        this.E = new double[y.length];
        this.maxEpoch = maxEpoch;
        this.K = new KernelFunction(k);
        this.epsilon = epsilon;
        this.model = new SVMModel( x,  y,  C, this.K);
        this.updateE();
    }

    public StandardSMO(double[][] x, int[] y, double C, KernelFunction.kernel k, double parameter, double epsilon, int maxEpoch){
        /*构造函数
        * */
        //init alpha and b
        this.E = new double[y.length];
        this.maxEpoch = maxEpoch;
        this.K = new KernelFunction(k, parameter);
        this.epsilon = epsilon;
        this.model = new SVMModel( x,  y,  C, this.K);
        this.updateE();
    }
	
	public void smoTrain(){
		/*train the model
		* */
        int iterNum = 0;
        int numChanged = 0;
        boolean examineAll = true;
        while ((iterNum < this.maxEpoch) && (examineAll ||  (numChanged > 0))){
                numChanged = 0;
                if (examineAll){
                    // 在所有训练样本上遍历
                    for (int i = 0; i < model.y.length ; i++) {
                        numChanged += updateOnce(i);
                        iterNum++;
                        System.out.println(iterNum);
                        if (iterNum == this.maxEpoch){
                            break;
                        }
                    }

                }else{
                    // 在支持向量上遍历(0<alpha<C)
                    int svNum = model.svIndex.size();
                    for (int i = 0; i < svNum; i++) {
                        numChanged += updateOnce(model.svIndex.get(i));
                        iterNum++;
                        System.out.println(iterNum);
                        if (iterNum == this.maxEpoch){
                            break;
                        }
                    }
                }
                if (examineAll){
                    examineAll = false;
                }else if (numChanged == 0){
                    examineAll = true;
                }
            // 更新支持变量的索引
            model.updateSvIndex();
        }
	}

    public int updateOnce(int i){
        indexOpt[1] = i;
        if(violateKKT(i)){
            if (model.svIndex.size() > 1){
                //选取第二个优化变量
                //第一种方式
                indexOpt[0] = chooseSecondAlpha(i);
                if (updateParamter() == 1){
                    return  1;
                }
            }
            //第二种方式,随机开始，遍历0<alpha<C(支持向量)
            if (model.svIndex.size() > 0){
                Random ra = new Random();
                int startId = ra.nextInt(model.svIndex.size());
                for (int j = startId; j < startId+model.svIndex.size(); j++) {
                    if (j < model.svIndex.size()){
                        indexOpt[0] = model.svIndex.get(j);
                        if (updateParamter() == 1){
                            return  1;
                        }
                    }else{
                        indexOpt[0] = model.svIndex.get(j - model.svIndex.size());
                        if (updateParamter() == 1){
                            return  1;
                        }
                    }
                }

            }

            //第三种方式,遍历所有样本
            Random ra = new Random();
            int startId = ra.nextInt(model.y.length);
            for (int j = startId; j < startId+model.y.length; j++) {
                if (j < model.y.length){
                    indexOpt[0] = j;
                    if (updateParamter() == 1){
                        return  1;
                    }
                }else{
                    indexOpt[0] = j - model.y.length;
                    if (updateParamter() == 1){
                        return  1;
                    }
                }

            }
        }
        return 0;

    }

    public void updateE(){
        /**计算E，用于选取第二个需要优化的变量
         * E值为预测值和真实输出之差
         */
        for (int i = 0; i < model.y.length; i++) {
            double gx = model.calculateGx(model.x[i]);
            E[i] = gx - model.y[i];
        }
    }


    public int chooseSecondAlpha(int j){
        /*选择第二个需要优化的变量, j是第一个优化变量的索引
        * */
        double[] deltaE = new double[model.y.length];
        for (int i = 0; i < model.y.length; i++) {
            //System.out.println(i);
            //System.out.println("j" + j);
            deltaE[i] = Math.abs(E[i] - E[j]);
        }
        return getMaxIndex(deltaE);
    }

    public int getMaxIndex(double x[]){
        /*返回数组$x[]$中最大元素的索引
        * */
        int id = 0;
        double max = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max){
                id = i;
                max = x[i];
            }
        }
        return id;
    }

    public int updateParamter(){
        /**更新变量
         */
        //未经剪辑
        // alpha2
        double K11 = K.calculateK(model.x[indexOpt[0]], model.x[indexOpt[0]]);
        double K22 = K.calculateK(model.x[indexOpt[1]], model.x[indexOpt[1]]);
        double K12 = K.calculateK(model.x[indexOpt[0]], model.x[indexOpt[1]]);

        double ita = K11 + K22 - 2*K12;
        double alpha1New;
        double alpha2New;
        //剪辑,计算L和H是用的旧的参数
        double L = this.getL();
        double H = this.getH();

        // 第一种特殊情况
        if (L == H){
            return 0;
        }

        if (ita > 0){
            alpha2New = model.alpha[indexOpt[1]] +  model.y[indexOpt[1]]*(E[indexOpt[0]]-E[indexOpt[1]])/ita;
            if(model.alpha[indexOpt[1]] > H){
                alpha2New = H;
            }else if(model.alpha[indexOpt[1]] < L) {
                alpha2New = L;
            }
        }else{
            // ita = 0的特殊情况
            double s = model.y[indexOpt[0]]*model.y[indexOpt[1]];
            double f1 = model.y[indexOpt[0]]*(E[indexOpt[0]]+model.b) - model.alpha[indexOpt[0]]*K11 - s*model.alpha[indexOpt[1]]*K12;
            double f2 = model.y[indexOpt[1]]*(E[indexOpt[1]]+model.b) - model.alpha[indexOpt[1]]*K22 - s*model.alpha[indexOpt[0]]*K12;
            double L1 = model.alpha[indexOpt[0]] + s*(model.alpha[indexOpt[1]] - L);
            double H1 = model.alpha[indexOpt[0]] + s*(model.alpha[indexOpt[1]] - H);
            double LObj = L1*f1 + L*f2 + 0.5*L1*L1*K11 + 0.5*L*L*K22 + s*L*L1*K12;
            double HObj = H1*f1 + H*f2 + 0.5*H1*H1*K11 + 0.5*H*H*K22 + s*H*H1*K12;
            if (LObj < HObj - epsilon){
                alpha2New = L;
            }else if (LObj > HObj + epsilon){
                alpha2New = H;
            }else {
                alpha2New = model.alpha[indexOpt[1]];
            }
        }

        if (Math.abs(alpha2New-model.alpha[indexOpt[1]]) < epsilon*(epsilon+alpha2New+model.alpha[indexOpt[1]])){
            return 0;
        }

        // 更新alpha1
        alpha1New = model.alpha[indexOpt[0]] + model.y[indexOpt[0]]*model.y[indexOpt[1]]*(model.alpha[indexOpt[1]] - alpha2New);

        // 更新b
        calculateB(alpha1New, model.alpha[indexOpt[0]], alpha2New, model.alpha[indexOpt[1]],  K11, K12, K22  );
        // 更新 alpha
        model.alpha[indexOpt[0]] = alpha1New;
        model.alpha[indexOpt[1]] = alpha2New;

        //更新E
        updateE();
        return 1;
    }

    public double getL(){
        /*计算L用于alpha的剪辑
        * */
        double L = 0;
        if(model.y[indexOpt[0]] == model.y[indexOpt[1]]){
            L = Math.max(0, model.alpha[indexOpt[1]]+model.alpha[indexOpt[0]]-model.C);
        }else{
            L = Math.max(0, model.alpha[indexOpt[1]]-model.alpha[indexOpt[0]]);
        }
        return L;
    }

    public double getH(){
        /*计算H用于alpha的剪辑
        * */
        double H = 0;
        if(model.y[indexOpt[0]] == model.y[indexOpt[1]]){
            H = Math.min(model.C, model.alpha[indexOpt[1]]+model.alpha[indexOpt[0]]);
        }else{
            H = Math.min(model.C, model.alpha[indexOpt[1]]-model.alpha[indexOpt[0]]+model.C);
        }
        return H;
    }

    public void calculateB(double alpha1, double alpha1Old, double alpha2, double alpha2Old, double K11, double K12, double K22 ){
        /*计算参数b
        * */
        double b1 = 0;
        double b2 = 0;

        b1  =  -E[indexOpt[0]] - model.y[indexOpt[0]]*K11*(alpha1-alpha1Old) - model.y[indexOpt[1]]*K12*(alpha2-alpha2Old) + model.b;
        b2  =  -E[indexOpt[1]] - model.y[indexOpt[0]]*K12*(alpha1-alpha1Old) - model.y[indexOpt[1]]*K22*(alpha2-alpha2Old) + model.b;
        if(alpha1 > 0 && alpha1 < model.C){
            model.b = b1;
            return ;
        }
        if(alpha2 > 0 && alpha2 < model.C){
            model.b = b2;
            return ;
        }
        model.b = (b1+b2)/2;

    }

    public boolean violateKKT(int i){
        /**
         * 计算训练样本点是否违反KKT条件
         * flag : 0 ,未违反； 1，违反
         */
        boolean flag = false;

        double gx = model.calculateGx(model.x[i]);
        double tmp = model.y[i]*gx - 1;
        if((model.alpha[i] < model.C  && tmp < -epsilon )
                || (model.alpha[i] > 0 && tmp > epsilon)
                || ((Math.abs(model.alpha[i]) < epsilon  || Math.abs(model.alpha[i]-model.C) < epsilon) && tmp < epsilon)){
            flag = true;
        }
        return flag;
    }

    public boolean violateKKTNonBound(int i){
        /**
         * 计算alpha在0到C中间的点,训练样本点是否违反KKT条件
         * flag : 0 ,未违反； 1，违反
         */
        boolean flag = false;

        double gx = model.calculateGx(model.x[i]);
        double tmp = model.y[i]*gx - 1;
        if((model.alpha[i] < model.C  && tmp < -epsilon )
                || (model.alpha[i] > 0 && tmp > epsilon)){
            flag = true;
        }
        return flag;
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
    //Test case
    public static void main(String[] args){
        SVMFileReader reader = new SVMFileReader("..\\heart_Scale");
        reader.getSVMData(260);
        KernelFunction.kernel k = KernelFunction.kernel.Linear;
        StandardSMO smo = new StandardSMO(reader.x, reader.y , 1, k, 0.001, 5000);

        System.out.println("开始训练...");
        long startTime = System.currentTimeMillis();
        smo.smoTrain();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("训练结束");
        System.out.println("训练时间为: " +  time);
        //开始预测
        //数据是原来用于训练的数据
        System.out.println("开始预测...");
        double probability = smo.predict(reader.x, reader.y);
        System.out.println("预测正确率为：" + probability);

    }

}
