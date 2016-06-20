/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.experiments;

import com.thiastux.human_simulator.model.Const;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathias
 */
class LogService {

    private String logFolderPath;
    private File drillTest1File;
    private List<String> drillTest1Events;
    private File drillTest2File;
    private List<String> drillTest2Events;
    private File drillTest3File;
    private List<String> drillTest3Events;
    private File adlTest1File;
    private List<String> adlTest1Events;
    private File adlTest2File;
    private List<String> adlTest2Events;
    private File adlTest3File;
    private List<String> adlTest3Events;

    public LogService(String folderPath, String idLong) {
        
        logFolderPath = folderPath;

        try {
            File userFolder = new File(logFolderPath +"/"+ idLong);

            if (!userFolder.exists()) {
                userFolder.mkdir();
            }

            drillTest1File = new File(userFolder, "drillTest1_"+System.currentTimeMillis()+".txt");

            if (!drillTest1File.exists()) {
                drillTest1File.createNewFile();
            }
            
            drillTest1Events = new ArrayList<>();
            drillTest1Events.add("{drillTest1Events: [");

            drillTest2File = new File(userFolder, "drillTest2_"+System.currentTimeMillis()+".txt");

            if (!drillTest2File.exists()) {
                drillTest2File.createNewFile();
            }
            
            drillTest2Events = new ArrayList<>();
            drillTest2Events.add("{drillTest2Events: [");

            drillTest3File = new File(userFolder, "drillTest3_"+System.currentTimeMillis()+".txt");

            if (!drillTest3File.exists()) {
                drillTest3File.createNewFile();
            }
            
            drillTest3Events = new ArrayList<>();
            drillTest3Events.add("{drillTest3Events: [");

            /*adlTest1File = new File(userFolder, "adlTest1_"+System.currentTimeMillis()+".txt");

            if (!adlTest1File.exists()) {
                adlTest1File.createNewFile();
            }
            
            adlTest1Events = new ArrayList<>();
            adlTest1Events.add("{adlTest1Events: [");

            adlTest2File = new File(userFolder, "adlTest2_"+System.currentTimeMillis()+".txt");

            if (!adlTest2File.exists()) {
                adlTest2File.createNewFile();
            }
            
            adlTest2Events = new ArrayList<>();
            adlTest2Events.add("{adlTest2Events: [");

            adlTest3File = new File(userFolder, "adlTest3_"+System.currentTimeMillis()+".txt");
            if (!adlTest3File.exists()) {
                adlTest3File.createNewFile();
            }
            adlTest3Events = new ArrayList<>();
            adlTest3Events.add("{adlTest3Events: [");*/

        } catch (IOException ex) {
            Logger.getLogger(LogService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void addTest1Event(String eventString) {
        if (Const.TEST_STATUS == Const.DRILL_TEST1) {
            drillTest1Events.add(eventString);
        } else {
            adlTest1Events.add(eventString);
        }
    }

    public void addTest2Event(String eventString) {
        if (Const.TEST_STATUS == Const.DRILL_TEST2) {
            drillTest2Events.add(eventString);
        } else {
            adlTest2Events.add(eventString);
        }
    }

    public void addTest3Event(String eventString) {
        if (Const.TEST_STATUS == Const.DRILL_TEST3) {
            drillTest3Events.add(eventString);
        } else {
            adlTest3Events.add(eventString);
        }
    }

    public void saveTest1Log() {
        try {
            if (Const.TEST_STATUS == Const.DRILL_TEST1) {
                drillTest1Events.add("]}");
                Files.write(drillTest1File.toPath(), drillTest1Events, Charset.forName("UTF-8"));
            } else {
                adlTest1Events.add("]}");
                Files.write(adlTest1File.toPath(), adlTest1Events, Charset.forName("UTF-8"));
            }
        } catch (IOException ex) {
            Logger.getLogger(LogService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveTest2Log() {
        try {
            if (Const.TEST_STATUS == Const.DRILL_TEST2) {
                drillTest2Events.add("]}");
                Files.write(drillTest2File.toPath(), drillTest2Events, Charset.forName("UTF-8"));
            } else {
                drillTest2Events.add("]}");
                Files.write(adlTest2File.toPath(), adlTest2Events, Charset.forName("UTF-8"));
            }
        } catch (IOException ex) {
            Logger.getLogger(LogService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveTest3Log() {
        try {
            if (Const.TEST_STATUS == Const.DRILL_TEST3) {
                drillTest3Events.add("]}");
                Files.write(drillTest3File.toPath(), drillTest3Events, Charset.forName("UTF-8"));
            } else {
                drillTest3Events.add("]}");
                Files.write(adlTest3File.toPath(), adlTest3Events, Charset.forName("UTF-8"));
            }
        } catch (IOException ex) {
            Logger.getLogger(LogService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
