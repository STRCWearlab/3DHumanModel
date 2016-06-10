/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator;

import com.jme3.math.Quaternion;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mathias
 */
public class DatasetChunk {
    
    private List<Quaternion[]> subDataset;
    
    public DatasetChunk(){
        subDataset = new ArrayList<>();
    }
    
    public void addQuaternions(Quaternion[] quaternions){
        subDataset.add(quaternions);
    }
    
    public List<Quaternion[]> getDatasetChunk(){
        return subDataset;
    }
    
}
