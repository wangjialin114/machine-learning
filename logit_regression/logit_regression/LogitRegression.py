# -*- coding: utf-8 -*-
"""
Created on Wed Dec 21 14:30:42 2016

@author: Wang
"""

import numpy as np
import time

from Update import Update

class LogitRegression:
    """The class of logitregression for classification
    
    parameters:
        regressiont_type: must be one of {"Ridge", "Lasso"}
        solve_method: must be one of {"grad_descent", "coor_descent", "bfgs"}
        C: the penalty factor
        w: the paramters of the decision plane, y = 1/(1+e^(-w^Tx)), feature_num * 1
        update: the solver to update w
    """
    
    regression_type = 'Ridge'
    solve_method = 'grad_descent'
    C = 0.0
    w = 0  
    update = Update(solve_method)
    
    def __init__(self, regression_type, solve_method, x, C = 0.0, max_iter_num = 10000):
        """Class Init Method
        
        init the parameters of the class
        
        Input:
            regression_type: the type of regression, must be one of {'LS', 'Ridge', 'Lasso'}
            solve_method: the method type of solve the parameters of the model, 
                must be one of {'GradDescent', 'CoorDescent'}
            x : the matrix of the data, column should represent one data point
            C : when using 'LS' or 'Lasso' model, these is the penalty paramter
        
        Return:
            None
        """
        
        self.regression_type = regression_type
        self.C = C
        self.solve_method = solve_method
        #print(x.shape)
        self.w = np.zeros([x.shape[1], 1])
        #y = np.dot(x, self.w)
        #print(y)
        self.update = Update(solve_method, max_iter_num)
        
    
    def predict_prob(self, x):
        """predict the prob of data x to be positive class
        
        Input:
            x: the data, N*d, d is the feature num
            
        Return:
            prob: the prob vectors
        """
        
        prob =  1/(1+np.exp(-np.dot(x, self.w)))
        
        return prob
        
    def predict_class(self, x):
        """predict y of x use the learned model
        
        use formula y = w^Tx
        
        Input:
            x: the input data
        
        Return:
            y_predict: the output of x
        """
        
        prob_predict = self.predict_prob(x)
        class_label = np.ones([prob_predict.shape[0], 1])
        for i in xrange(prob_predict.shape[0]):
            if(prob_predict[i] < 0.5):
                class_label[i] = 0
                
        return class_label
    
    def predict_accuracy(self, x, y):
        """to test the accuracy of the model on the dataset
        
        Input:
            x: N*d, the data
            y: N*1, the class label
            
        Return:
            acc: the accuracy of the model
        """
        
        predict_labels = self.predict_class(x)
        
        acc = np.sum(predict_labels == y)*1.0/y.shape[0]

        return acc
    
    def solve_regression(self, x, y):
        """solve the model
        
        use the update method to slove and get the paramters
        
        Input:
            x: the matrix of the data, shape should be N*d , 
                N is the number of data points, d is the number of features
            y: the target value of the corresponding points, shape should be N*1
            
        Return:
            None
        """
        
        self.update.update_model(self, x, y)
        
    def test(self):
        """use the data from sklearn importing data set
        
        """
        from sklearn import datasets
        from sklearn.preprocessing import StandardScaler
        digits = datasets.load_digits()
        X, y = digits.data, digits.target
        x = StandardScaler().fit_transform(X)

        # classify small against large digits
        y = (y > 4).astype(np.int)
        
        x = np.append(x, np.ones([x.shape[0],1]), 1)
        logit_regression = LogitRegression("Ridge", "newton", x, 0.1, max_iter_num=1000)
        logit_regression.solve_regression(x, y)
        print(logit_regression.w)
        print(logit_regression.predict_accuracy(x, y))
        
if __name__ == "__main__":
    """
    x = np.array([[1,0],[2,0],[3,0.2],[3,0.4],[0,5],[0,2],[0,3]])
    x_test = np.array([[1.5,0.2],[1.9,0],[0, 1.9]])
    y = np.array([[0], [0], [0], [0], [1], [1], [1]])
    x = np.append(x, np.ones([x.shape[0],1]), 1)
    x_test = np.append(x_test, np.ones([x_test.shape[0],1]), 1)
    logit_regression = LogitRegression("Ridge", "newton", x, 0.1, max_iter_num=1000)
    logit_regression.solve_regression(x, y)
    print(logit_regression.w)
    print(logit_regression.predict_class(x_test))
    """
    from sklearn import datasets
    from sklearn.preprocessing import StandardScaler
    digits = datasets.load_digits()
    X, y = digits.data, digits.target
    y = np.reshape(y, [y.shape[0], 1])
    x = StandardScaler().fit_transform(X)
    #print(y.shape)
    print(X.shape)
    # classify small against large digits
    y = (y > 4).astype(np.int)
    #print(y)
    x = np.append(x, np.ones([x.shape[0],1]), 1)
    logit_regression = LogitRegression("lasso", "coor_descent", x, 0.1, max_iter_num=5000)
    time_start = time.time()
    logit_regression.solve_regression(x, y)
    time_end = time.time()
    time_cost = time_end - time_start
    print("It costs " + str(time_cost) + " seconds")
    print(logit_regression.predict_accuracy(x, y))
        
        
        