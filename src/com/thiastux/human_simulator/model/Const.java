/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mathias
 */
public class Const {

    public static boolean animationStart = false;
    
    public static boolean useLegs=false;
    
    private static String[] command
            = {"-tor", "-hea", "-rua", "-rla", "-rha", "-lua", "-lla", "-lha", "-rul", "-rll", "-lul", "-lll"};

    private static String[] priorQuatCommand
            = {"-ptor", "-phea", "-prua", "-prla", "-prha", "-plua", "-plla", "-plha", "-prul", "-prll", "-plul", "-plll"};
    
    public static int TEST_STATUS;
    public static final int DEMO=1000;
    public static final int DRILL_TEST1=1001;
    public static final int DRILL_TEST2=1002;
    public static final int DRILL_TEST3=1003;
    public static final int ADL_TEST1=2001;
    public static final int ADL_TEST2=2002;
    public static final int ADL_TEST3=2003;
    public static boolean isDemoLoaded = false;
    public static boolean DEMO_RUNNING=true;
    public static boolean TEST1_RUNNING=true;
    public static boolean TEST2_RUNNING=true;
    public static boolean TEST3_RUNNING=true;
    public static boolean WAITING_TEST2_ANSWER=false; 
    public static boolean NEXT_CHUNK=false;
    public static boolean WAITING_TEST3_ANSWER=false;
    

    public enum BindColumIndex {

        tor(0),
        hea(1),
        rua(2),
        rla(3),
        rha(4),
        lua(5),
        lla(6),
        lha(7),
        rul(8),
        rll(9),
        lul(10),
        lll(11);

        private static final Map<String, BindColumIndex> lookup
                = new HashMap<>();

        static {
            for (BindColumIndex s : EnumSet.allOf(BindColumIndex.class)) {
                lookup.put(Const.command[s.getCode()], s);
            }
        }

        private int code;

        private BindColumIndex(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static BindColumIndex get(String comm) {
            return lookup.get(comm);
        }
    }

    public enum PriorQuatIndex {
        ptor(0),
        phea(1),
        prua(2),
        prla(3),
        prha(4),
        plua(5),
        plla(6),
        plha(7),
        prul(8),
        prll(9),
        plul(10),
        plll(11);

        private static final Map<String, PriorQuatIndex> lookup
                = new HashMap<>();

        static {
            for (PriorQuatIndex s : EnumSet.allOf(PriorQuatIndex.class)) {
                lookup.put(Const.priorQuatCommand[s.getCode()], s);
            }
        }

        private int code;

        private PriorQuatIndex(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static PriorQuatIndex get(String pComm) {
            return lookup.get(pComm);
        }

    }

}
