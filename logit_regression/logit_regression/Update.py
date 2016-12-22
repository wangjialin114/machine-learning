# -*- coding: utf-8 -*-
"""
Created on Wed Dec 21 14:49:03 2016

@author: Wang
"""
import sys

import numpy as np

class Update:
    
    solve_method = "grad_descent"
    eta = 0.01
    epsilon_ridge = 0.001
    epsilon_lasso = 0.001
    max_iter_num = 10000
    
    def __init__(self, solve_method, max_iter_num=10000, eta=0.01, epsilon=0.001):
        """construct method of the class
        
        Input:
            solve_method: the type of method to solve the model
            max_iter_num: the maximum number of ierations when solving the model
            eta: the learning rate when using the gradient descent to solve the model
            epsilon: rss square sum norm tolerance to stop updating
        """
        
        self.solve_method = solve_method
        self.max_iter_num = max_iter_num
        self.eta = eta
        self.epsilon_ridge = epsilon
        self.epsilon_lasso = epsilon
        
    def update_model(self, logit_regression, x, y):
        """update the parameter of the model
        
        use the name decided in thr runtime to call the correponding method
        
        Input:
            regression:  the regression model
            x : the data points
            y:  the target values
            
        Return:
            None
        """
        
        method_name = self.solve_method + "_update"
        try:
            method = getattr(self, method_name)
        except :
            print("solver " + self.solve_method + " is not supported")
            sys.exit(0)
        return method(logit_regression, x, y)
        
    def grad_descent_update(self, logit_regression, x, y):
        """update the parameters for the ridge regression model
        
        Input:
            regression:  the regression model
            x : the data points
            y:  the target values
            
        Return:
            None
        """
        assert logit_regression.regression_type.lower() == "ridge"
        loss_partial = self.compute_loss_partial(logit_regression, x, y)
        partial_norm = self.compute_loss_partial_norm(loss_partial)
        iter_num = 0
        while((partial_norm>self.epsilon_ridge) & (iter_num<self.max_iter_num)):
            logit_regression.w = (1-2*self.eta*logit_regression.C)*logit_regression.w - self.eta*loss_partial
            loss_partial = self.compute_loss_partial(logit_regression, x, y)
            partial_norm = self.compute_loss_partial_norm(loss_partial)
            iter_num = iter_num + 1
            
        #print(iter_num)
        
    def coor_descent_update(self, logit_regression, x, y):
        """update the parameters using the coordinate descent
        
        Input:
            regression:  the regression model
            x : the data points
            y:  the target values
            
        Return:
            None
        """
        assert logit_regression.regression_type.lower() == "lasso"
        iter_num = 0
        delta_w = 1 
        while((delta_w>self.epsilon_lasso) & (iter_num<self.max_iter_num)):
            w = logit_regression.w
            for i in xrange(logit_regression.w.shape[0]):
                partial = self.compute_loss_partial_coor(logit_regression, x, y, i)
                if(logit_regression.w[i, 0] > 0):
                    logit_regression.w[i, 0] = logit_regression.w[i, 0] - self.eta*(partial+2*logit_regression.C)
                elif(logit_regression.w[i, 0] < 0):
                    logit_regression.w[i, 0] = logit_regression.w[i, 0] - self.eta*(partial-2*logit_regression.C)
                else:
                    logit_regression.w[i, 0] = logit_regression.w[i, 0] - self.eta*partial
            delta_w = np.dot(w.T, logit_regression.w)
            iter_num = iter_num + 1
        print(iter_num)
    
    def newton_update(self, logit_regression, x, y):
        """use the newton method to update the parameters for the ridge regression model
        
        Input:
            logit_regression:  the regression model
            x : the data points
            y:  the target values
            
        Return:
            None
        """
        
        assert logit_regression.regression_type.lower() == "ridge"
        B = np.identity(logit_regression.w.shape[0])
        #print(B)
        g = self.compute_loss_partial(logit_regression, x, y) + 2*logit_regression.C*logit_regression.w
        #print(g)
        g_norm = self.compute_loss_partial_norm(g)
        iter_num = 0
        while((g_norm>self.epsilon_ridge) & (iter_num<self.max_iter_num)):
            p = - np.dot(np.linalg.inv(B), g)
            logit_regression.w = logit_regression.w + self.eta*p
            tmp_g = self.compute_loss_partial(logit_regression, x, y)+ 2*logit_regression.C*logit_regression.w
            B1 = B
            B2 = np.dot((tmp_g-g),np.transpose(tmp_g-g))/np.dot(np.transpose(tmp_g-g),self.eta*p)
            B3 = np.dot(np.dot(B,self.eta*p),np.dot(np.transpose(self.eta*p),B)/(np.dot(np.dot(np.transpose(self.eta*p),B), self.eta*p)))
            B  = B1 + B2 - B3
            g = tmp_g
            g_norm = self.compute_loss_partial_norm(g)
            iter_num = iter_num + 1
            #print(logit_regression.w)
        print(iter_num)
        
    
    def compute_loss_partial_norm(self, loss_partial):
        """compute the residual square sum partial norm
        
        Input:
            loss_partial: the loss partial vector
        Return:
            rss_partial_norm: the norm
        """
        
        norm = np.dot(np.transpose(loss_partial), loss_partial)
        
        return norm
    
    def compute_loss_partial(self, logit_regression, x, y):
        """compute the rss partial for updating the parameters
        
        Input:
            regression: the regression model
            x: the data matrix
            y: the target values vector
            
        Return:
            loss_partial: the partial derivative vector
        """
        
        prob_vec = logit_regression.predict_prob(x)
        
        #the shape assertion
        assert prob_vec.shape == y.shape
        loss_partial = np.dot(np.transpose(x), prob_vec - y)
        
        return loss_partial
        
    def compute_loss_partial_coor(self, logit_regression, x, y, index):
        """compute the rss partial for updating the parameters
        
        Input:
            regression: the regression model
            x: the data matrix
            y: the target values vector
            index: the index of the w to be updated
            
        Return:
            loss_partial: the partial derivative vector
        """
        
        prob_vec = logit_regression.predict_prob(x)
        
        #the shape assertion
        assert prob_vec.shape == y.shape
        loss_partial = np.dot(np.transpose(x[:, index:index+1]), prob_vec - y)
        
        return loss_partial