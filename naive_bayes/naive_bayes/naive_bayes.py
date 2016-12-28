# -*- coding: utf-8 -*-
"""
Created on Tue Dec 27 13:09:16 2016

@author: WangJalin
"""

from __future__ import division
import numpy as np

import data_utils

class naive_bayes:
    """the model of naive bayes
    
    parameter:
        model: the model, can be one of {'gaussian', 'multinomial' }, 'gauss' is not supported yet
        alpha: the laplace smooth parameter for the multinomial 
        condi_prob: the conditional prob that learning from the data
        class_prob: the class distribution prob
    """
    model = "multinomial"
    alpha = 1
    condi_prob = 0
    class_prob = 0
    
    def __init__(self, x, y, model="multinomial", alpha=1):
        """the construct method of the class
        
        Input:
            x: the data of the features, data_matrix shape data_num*
            y: the class labels
            model: the model of baive bayes
            alpha: the smoothing parameter
            
        Return:
            None
        """
        
        y_unique = np.unique(y)
        self.condi_prob = np.zeros([y_unique.shape[0], x.shape[1]])
        self.class_prob = np.zeros([y_unique.shape[0], 1])
        
        self.model = model
        self.alpha = alpha
    
    def learning_model(self, x, y):
        """learn the model
        
        Input:
            x: the data of the features, data_matrix shape data_num*
            y: the labels
            
        Return:
            None
        """
        
        # get the class distribution
        self.compute_class_distribution(y)
        # get the feature condititon distribution
        feature_cnt = self.feature_count(x, y)
        self.compute_condi(feature_cnt, x.shape[0])
    
    def compute_class_distribution(self, y):
        """compute the distribution of the data class
        
        Input:
            y: the labels of the data
            
        Return:
            class_prob: the class distribution
        """
        
        y_unique = np.unique(y)
        
        for i in y_unique:
            a = (y==i)
            self.class_prob[i, 0] = np.sum(a.astype(int))/y.shape[0]
        
    
    def compute_condi(self, feature_cnt, data_num):
        """compute the condi probability of the data
        
            only implement the computing of the Multinomial distribution, and use the laplace smoothing
        
        Input:
            feature_cnt: the feature conuter matrix, shape should be class_num*feature_num
            data_num: the number of the data points
        Return:
            None
        """
        
        for i in xrange(self.condi_prob.shape[0]):
            for j in xrange(self.condi_prob.shape[1]):
                self.condi_prob[i, j] = (feature_cnt[i, j] + self.alpha)/(self.class_prob[i, 0]*y.shape[0]+self.alpha*feature_cnt.shape[1])
    
    def feature_count(self, x, y):
        """
        Input:
            x:the data of the features, data_matrix shape data_num*
            y:the labels of the data
                    
        Return:
            feature_cnt: the feature conuter matrix, shape should be class_num*feature_num
        """
        
        feature_cnt = np.zeros([self.condi_prob.shape[0], self.condi_prob.shape[1]])
        
        for i in xrange(feature_cnt.shape[0]):
            for j in xrange(feature_cnt.shape[1]):
                feature_cnt[i, j] = np.sum(x[np.array(y==i)[:, 0], j])
        
        return feature_cnt
        
        
    def predict(self, x):
        """predict the class of the data
        
        Input:
            x: the data that need me predict
        
        Return:
            predict_prob: the probability that the data belongs to each class
        """
        
        predict_prob = np.zeros([x.shape[0], self.class_prob.shape[0]])
        # use log likehood to calculate
        log_prob = np.log(self.condi_prob)
        # turn the feature matrix to binary matrix, then can be vector calaulted
        x = (x>0.5).astype(int)
        
        for i in xrange(x.shape[0]):
            for c in xrange(predict_prob.shape[1]):
                predict_prob[i, c] = np.matmul(log_prob[c, :], x[i, :])*np.log(self.class_prob[c, 0])
        
        # exp to prob
        predict_prob = np.exp(predict_prob)
        # normalize
        predict_prob = (predict_prob.T/np.sum(predict_prob, 1)).T

        return predict_prob
    
    def predict_class(self, x):
        """predict the class of x
        
        Input:
            x:the data that need me predict
        
        Return:
            prob_max_id: the predict class
        """
        predict_prob = self.predict(x)
        prob_max_id = np.argmax(predict_prob, 1)
        
        return prob_max_id
    
    
if __name__ == "__main__":
    # train
    postingList=[['my', 'dog', 'has', 'flea', 'problems', 'help', 'please'],
                 ['maybe', 'not', 'take', 'him', 'to', 'dog', 'park', 'stupid'],
                 ['my', 'dalmation', 'is', 'so', 'cute', 'I', 'love', 'him'],
                 ['stop', 'posting', 'stupid', 'worthless', 'garbage'],
                 ['mr', 'licks', 'ate', 'my', 'steak', 'how', 'to', 'stop', 'him'],
                 ['quit', 'buying', 'worthless', 'dog', 'food', 'stupid']]      
    labels = ['no', 'yes', 'no', 'yes', 'no', 'yes']
    a = data_utils.data_utils()
    a.create_word_set(postingList)
    # string to feature matrix
    x = a.feature_to_num(postingList)
    y = a.label_to_num(labels)
    # naive bayes learning
    nb = naive_bayes(x, y)
    nb.learning_model(x, y)
    # predict
    post_test = [["stupid", "dog"]]
    x_test = a.feature_to_num(post_test)
    y = nb.predict_class(x_test)
    print("the class of  the text is " + a.num_to_label(list(y))[0])
    
    