/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator;

import com.jme3.math.Quaternion;
import java.awt.Dimension;

/**
 *
 * @author mathias
 */
class AnimationPacket {
    
    private final int dimension=12;
    Quaternion[] quaternionsArray;

    public AnimationPacket() {
        quaternionsArray = new Quaternion[dimension];
    }
    
    public Quaternion getQuaternion(int position){
        return quaternionsArray[position];
    }
}
