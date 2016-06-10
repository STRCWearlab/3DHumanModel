/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathias
 */
public class AnimationLoader {

    public static void main(String[] args) {

        try {
            
            connectTestServer();

            String path = "";

            String userName = String.valueOf(System.currentTimeMillis());
            String userAnswer = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            File userFolder = new File(path);

            System.out.println("Welcome in the test: ");
            System.out.println("Before to start to actual test, a short demo of the 3D model will be shown.");
            System.out.println("Press Enter when ready or enter 0 to skip the demo:");
            userAnswer = reader.readLine();

            if(!userAnswer.trim().equals("0"))
                launchDemo();

            System.out.println("Now we will begin with the test 1.");
            System.out.println("Remember: press the SpaceBar when you recognize an activity."
                    + "\n It is up to you to decide what is an activiy.");
            System.out.println("Press Enter when ready or enter 0 to skip this test:");
            userAnswer = reader.readLine();
            
            if(!userAnswer.trim().equals("0"))
                test1();
            
            System.out.println("Test 1 ended.");
            System.out.println("Now we will begin with the test 2.");
            System.out.println("Remember: you will see a short animation that represent an activity."
                    + "\n At the end, please enter what you think is the activity accomplished by the model."
                    + "\n Use no more than 2-3 words.");
            System.out.println("Press Enter when ready or enter 0 to skip this test:");
            userAnswer = reader.readLine();
            
            if(!userAnswer.trim().equals("0"))
                test2();
            
            System.out.println("Test 2 ended.");
            System.out.println("Now we will begin with the test 3.");
            System.out.println("Remember: you will see a short animation that represent an activity."
                    + "\n At the end, please choose the activity you think the model would accomplish from the"
                    + "\n set of proposed activities.");
            System.out.println("Press Enter when ready or enter 0 to skip this test:");
            userAnswer = reader.readLine();
            
            if(!userAnswer.trim().equals("0"))
                test3();
            
            System.out.println("Test 3 ended.");
            System.out.println("Thank you very much for your attendance and you availability.");

        } catch (IOException ex) {
            Logger.getLogger(AnimationLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void launchDemo() {
        
    }

    private static void test1() {
        
    }

    private static void test2() {
        
    }

    private static void test3() {
        
    }

    private static void connectTestServer() {
        
    }

}
