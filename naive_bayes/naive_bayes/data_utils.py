# -*- coding: utf-8 -*-
"""
Created on Tue Dec 27 14:39:06 2016

@author: WangJalin
"""

import numpy as np

class data_utils:
    """
    """
    
    label = []
    word_set = []

    def __init__(self):
        pass
    
    def label_to_num(self, labels):
        """map the label to number
        
        Input:
            labels: the labels
            
        Return:
            y: the mapped number
        """
        
        self.label = list(set(labels))
        
        y = np.zeros([len(labels), 1])
        
        for i in xrange(len(labels)):
            y[i, 0] = self.label.index(labels[i])
        
        return y
        
    def num_to_label(self, num):
        """map the number to the label(maybe string)
        
        Input:
            num: the number which represents the labels
        
        Return:
            labels: the list of the labels
        """
        
        labels = [self.label[i] for i in num]

        return labels
        
    def create_word_set(self, texts):
        """map the feature to number
        
        Input:
            texts: list, whose element is the word list
            
        Return:
            feature_num: the 
        """
        
        word_set = set()
        
        for text in texts:
            word_set = set.union(word_set, set(text))        
            
        self.word_set = list(word_set)
    
    def feature_to_num(self, texts):
        """map the string feature to the num
        
        Input:
            texts: the text list
            
        Return:
            feature_matrix: the numeric feature matrix
        """
        
        feature_num = []

        for text in texts:
            text_to_num = [self.word_set.index(word) for word in text ]
            feature_num.append(text_to_num)
            
        feature_matrix = np.zeros([len(texts), len(self.word_set)])
        
        for i in xrange(len(feature_num)):
            num = feature_num[i]
            for j in num:
                feature_matrix[i, j] = feature_matrix[i, j] + 1
        
        return feature_matrix
            
        
    