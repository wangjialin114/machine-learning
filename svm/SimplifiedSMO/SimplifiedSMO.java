package SimplifiedSMO;


/**
 * Created by Wang on 2016/12/5.
 */
public class SimplifiedSMO {

    public double[] E; //存储E
    public int[] indexOpt = new int[2]; //优化的参数的索引
    KernelFunction K;
    SVMModel model;
    int maxEpoch = 0; //最大迭代次数
    public boolean[] selectMask; //变量是否已被更新过
    double epsilon;


    public SimplifiedSMO(double[][] x, int[] y, double C, KernelFunction.kernel k, double epsilon, int maxEpoch){
        /*构造函数
        * */
        //init alpha and b
        this.E = new double[y.length];
        selectMask = new boolean[y.length];
        for (int i = 0; i < selectMask.length; i++) {
            selectMask[i] = false;
        }
        this.maxEpoch = maxEpoch;
        this.K = new KernelFunction(k);
        this.epsilon = epsilon;
        this.model = new SVMModel( x,  y,  C, this.K);
        this.updateE();
    }

    public SimplifiedSMO(double[][] x, int[] y, double C, KernelFunction.kernel k, double parameter, double epsilon, int maxEpoch){
        /*构造函数
        * */
        //init alpha and b
        this.E = new double[y.length];
        selectMask = new boolean[y.length];
        for (int i = 0; i < selectMask.length; i++) {
            selectMask[i] = false;
        }
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
        //while (!(sumToZeroCondi() && boundCondi()) && iterNum < this.maxEpoch){
        while (iterNum < this.maxEpoch){
            //选取变量
            chooseOptAlpha();
            //更新变量
            updateParamter();
            iterNum ++;
            //更新E
            updateE();
            System.out.println(iterNum);
        }
	}

    public boolean sumToZeroCondi(){
        double sum = 0;
        for (int i = 0; i < model.y.length; i++) {
            sum += model.alpha[i]*model.y[i];
        }
        return (Math.abs(sum) < this.epsilon);
    }

    public boolean boundCondi(){
        boolean flag = true;
        for (int i = 0; i < model.alpha.length; i++) {
            if (model.alpha[i] >=0 && model.alpha[i] <= model.C){
                flag = false;
                break;
            }
        }
        return flag;
    }

    public void chooseOptAlpha(){
        /**
         * 选取优化变量，记录其下标
         */
        // 第一个优化变量的选取
        indexOpt[0] = chooseFirstAlpha();
        //第二个优化变量的选取
        indexOpt[1] = chooseSecondAlpha(indexOpt[0]);
        selectMask[indexOpt[0]] = true;
        selectMask[indexOpt[1]] = true;
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

    public int chooseFirstAlpha(){
        int i;
        for ( i = 0; i < model.y.length; i++) {
            if (selectMask[i] == true){
                continue;
            }
            int flag = violateKKT(i);
            if(flag == 1){
                break;
            }
        }
        if(i == model.y.length){
            for (int j = 0; j < selectMask.length; j++) {
                selectMask[j] = false;
            }
            for ( i = 0; i < model.y.length; i++) {
                int flag = violateKKT(i);
                if(flag == 1){
                    break;
                }
                i--;// 防止 i = y.length
            }

        }
        return i;
    }

    public int chooseSecondAlpha(int j){
        /*选择第二个需要优化的变量, j是第一个优化变量的索引
        * */
        double[] deltaE = new double[model.y.length];
        for (int i = 0; i < model.y.length; i++) {
            //System.out.println(i);
            //System.out.println(j);
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

    public boolean updateParamter(){
        /**更新变量
         */
        //未经剪辑
        // alpha2
        double K11 = K.calculateK(model.x[indexOpt[0]], model.x[indexOpt[0]]);
        double K22 = K.calculateK(model.x[indexOpt[1]], model.x[indexOpt[1]]);
        double K12 = K.calculateK(model.x[indexOpt[0]], model.x[indexOpt[1]]);

        double ita = K11 + K22 - 2*K12;
        double alpha2Old = this.model.alpha[this.indexOpt[1]];
        double alpha1Old = this.model.alpha[this.indexOpt[0]];
        //剪辑,计算L和H是用的旧的参数
        double L = this.getL();
        double H = this.getH();

        model.alpha[indexOpt[1]] += model.y[indexOpt[1]]*(E[indexOpt[0]]-E[indexOpt[1]])/ita;
        if(model.alpha[indexOpt[1]] > H){
            model.alpha[indexOpt[1]] = H;
        }else if(model.alpha[indexOpt[1]] < L) {
            model.alpha[indexOpt[1]] = L;
        }
        // 更新alpha1
        model.alpha[indexOpt[0]] += model.y[indexOpt[0]]*model.y[indexOpt[1]]*(alpha2Old-model.alpha[indexOpt[1]]);

        // 更新b
        calculateB(model.alpha[indexOpt[0]], alpha1Old, model.alpha[indexOpt[1]], alpha2Old );

        return (Math.abs(model.alpha[indexOpt[0]]-alpha1Old)<epsilon ) && (Math.abs(model.alpha[indexOpt[0]]-alpha1Old)<epsilon );
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

    public void calculateB(double alpha1, double alpha1Old, double alpha2, double alpha2Old ){
        /*计算参数b
        * */
        double b1 = 0;
        double b2 = 0;
        double K11 = K.calculateK(model.x[indexOpt[0]], model.x[indexOpt[0]]);
        double K22 = K.calculateK(model.x[indexOpt[1]], model.x[indexOpt[1]]);
        double K12 = K.calculateK(model.x[indexOpt[0]], model.x[indexOpt[1]]);
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

    public int violateKKT(int i){
        /**
         * 计算训练样本点是否违反KKT条件
         * flag : 0 ,未违反； 1，违反
         */
        int flag = 0;

        double gx = model.calculateGx(model.x[i]);
        double tmp = model.y[i]*gx - 1;
        if((model.alpha[i] < model.C  && tmp < epsilon )
                || (model.alpha[i] > 0 && tmp > -epsilon)
                || ((Math.abs(model.alpha[i]) < epsilon  || Math.abs(model.alpha[i]-model.C) < epsilon) && tmp < epsilon)){
            flag = 1;
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

        SVMFileReader reader = new SVMFileReader("..\\heart_scale");
        reader.getSVMData(260);
        KernelFunction.kernel k = KernelFunction.kernel.Gauss;
        SimplifiedSMO smo = new SimplifiedSMO(reader.x, reader.y , 1, k, 0.01, 0.001, 5000);

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
