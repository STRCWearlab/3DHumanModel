/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.experiments;

import com.jme3.system.AppSettings;
import com.thiastux.human_simulator.model.Const;
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

    private Demo demo;
    private Test1 test1;
    private Test2 test2;
    private Test3 test3;
    
    private String logDirectory;

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
        logDirectory = args[2];
        initializeAppSettings();
    }

    private void start() {
        //startTests();
        //startDemo();
        startDrillTest1();
        //startDrillTest2();
        //startDrillTest3();
    }

    private void initializeAppSettings() {
        appSettings = new AppSettings(true);
        appSettings.setWidth(1366);
        appSettings.setHeight(768);
        appSettings.setTitle("My awesome Game");
        appSettings.setVSync(true);
        appSettings.setSamples(4);
    }

    public void startTests() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String userId;
            System.out.println("Insert userId");
            userId = reader.readLine();
            logService = new LogService(logDirectory, userId);
        } catch (IOException ex) {
            Logger.getLogger(TestLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startDemo() {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String answer;

            System.out.println("Welcome. A demo will be now shown.");
            System.out.println("Press Enter when you are ready or 0 to skip the demo.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS=Const.DEMO;
                demo = new Demo(dataLoader);
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

            System.out.println("Ok, the demo is ended. Now we will start with the test 1.");
            System.out.println("Press Enter when you are ready or 0 to skip this test.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS=Const.DRILL_TEST1;
                test1 = new Test1(dataLoader, logService);
                test1.setShowSettings(false);
                test1.setSettings(appSettings);
                Const.TEST1_RUNNING = true;
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

            System.out.println("Ok, the test 1 is ended. Now we will start with the test 2.");
            System.out.println("Press Enter when you are ready or 0 to skip this test.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS=Const.DRILL_TEST2;
                test2 = new Test2(dataLoader, logService, lock);
                test2.setShowSettings(false);
                test2.setSettings(appSettings);
                Const.TEST2_RUNNING=true;
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

            System.out.println("Ok, the test 2 is ended. Now we will start with the test 3.");
            System.out.println("Press Enter when you are ready or 0 to skip this test.");
            answer = reader.readLine();
            if (!answer.trim().equals("0")) {
                Const.TEST_STATUS=Const.DRILL_TEST3;
                test3 = new Test3(dataLoader, logService);
                test3.setShowSettings(false);
                test3.setSettings(appSettings);
                Const.TEST3_RUNNING=true;
                test3.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(TestLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
