/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.experiments;

import com.jme3.math.Quaternion;

/**
 *
 * @author mathias
 */
class Test1Entry {
    
    long timestamp;
    Quaternion[] quaternions;
    int activityLabel;

    Test1Entry(long timestamp, Quaternion[] quaternions, int activityLabel) {
        this.timestamp = timestamp;
        this.quaternions = quaternions;
        this.activityLabel = activityLabel;
    }

    Test1Entry() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Quaternion[] getQuaternions() {
        return quaternions;
    }

    public int getActivityLabel() {
        return activityLabel;
    }

    @Override
    public String toString() {
        return "{" + "timestamp: " + timestamp + ", activityLabel: " + activityLabel + '}';
    }
   
}
