/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator;

import com.jme3.system.AppSettings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathias
 */
public class TestLoader {

    private DataLoader dataLoader;
    private LogService logService;
    private AppSettings appSettings;

    private StickmanDemo demo;
    private Test1 test1;
    private Test2 test2;
    private Test3 test3;

    private static final Object lock = new Object();

    public static void main(String[] args) {
        TestLoader testLoader = new TestLoader(args);
        testLoader.start();
    }

    private TestLoader(String[] args) {
        dataLoader = new DataLoader(args[0],
                args[1],
                lock,
                Arrays.copyOfRange(args, 3, args.length));
        dataLoader.setTestLoader(this);
        long userId = System.currentTimeMillis();
        System.out.println("UserId = " + userId);
        logService = new LogService(args[2], userId);
        initializeAppSettings();
    }

    private void start() {
        //startDemo();
        //startDrillTest1();
        startDrillTest2();
    }

    private void initializeAppSettings() {
        appSettings = new AppSettings(true);
        appSettings.setWidth(1366);
        appSettings.setHeight(768);
        appSettings.setTitle("My awesome Game");
        appSettings.setVSync(true);
        appSettings.setSamples(4);
    }
    
    public void startDemo(){
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String answer;

            System.out.println("Welcome. A demo will be now shown.");
            System.out.println("Press Enter when you are ready or 0 to skip the demo.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS = Const.DEMO;
                dataLoader.start();
                demo = new StickmanDemo(dataLoader);
                demo.setShowSettings(false);
                demo.setSettings(appSettings);
                Const.DEMO_RUNNING = true;
                demo.start();
            }

        } catch (IOException ex) {
            Logger.getLogger(TestLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startDrillTest1() {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String answer;

            System.out.println("Ok, the demo is ended. Now we will start with the first test.");
            System.out.println("Press Enter when you are ready or 0 to skip this test.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS = Const.DRILL_TEST1;
                dataLoader.start();
                test1 = new Test1(dataLoader, logService);
                test1.setShowSettings(false);
                test1.setSettings(appSettings);
                Const.TEST1_RUNNING=true;
                test1.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(TestLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startDrillTest2() {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String answer;

            System.out.println("Ok, the demo is ended. Now we will start with the first test.");
            System.out.println("Press Enter when you are ready or 0 to skip this test.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS = Const.DRILL_TEST2;
                dataLoader.start();
                test2 = new Test2(dataLoader, logService);
                test2.setShowSettings(false);
                test2.setSettings(appSettings);
                test2.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(TestLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startDrillTest3() {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String answer;

            System.out.println("Ok, the demo is ended. Now we will start with the first test.");
            System.out.println("Press Enter when you are ready or 0 to skip this test.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS = Const.DRILL_TEST3;
                dataLoader.start();
                test3 = new Test3(dataLoader, logService);
                test3.setShowSettings(false);
                test3.setSettings(appSettings);
                test3.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(TestLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
