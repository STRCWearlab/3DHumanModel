/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.experiments;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathias
 */
public class ExtractDataset {
    
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader(args[0],
                args[1],
                new Object(),
                Arrays.copyOfRange(args, 3, args.length));
        
        List<Test1Entry> entrys = dataLoader.getTest1DrillQuaternionsList();
        List<String> dataset = new ArrayList<>();
        
        dataset.add("{dataset : [");
        
        for(Test1Entry entry : entrys){
            dataset.add(entry.toString()+ ",");
        }
        
        dataset.add("]}");
        File datasetTest1 = new File("/home/mathias/Documents/Academic/PhD/MyPublications/HASCA2016/PrivacyPreservingLabelling/Experiments/test1_dataset.txt");
        
        try {
            Files.write(datasetTest1.toPath(), dataset, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(ExtractDataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    
}
