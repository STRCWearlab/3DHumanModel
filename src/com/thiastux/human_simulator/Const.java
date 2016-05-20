/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mathias
 */
public class Const {
    
    private static String[] command = 
    {"rha","rla","rua","lha","lla","lua","hea","tor","rth","rsh","lth","lsh"};
    
    private static String[] priorQuatCommand =
    {"prha","prla","prua","plha","plla","plua","phea","ptor","prth","prsh","plth","plsh"};

    public enum BindColumIndex {

        rha(0),
        rla(1),
        rua(2),
        lha(3),
        lla(4),
        lua(5),
        hea(6),
        tor(7),
        rth(8),
        rsh(9),
        lth(10),
        lsh(11);

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
        prha(0),
        prla(1),
        prua(2),
        plha(3),
        plla(4),
        plua(5),
        phea(6),
        ptor(7),
        prth(8),
        prsh(9),
        plth(10),
        plsh(11);
        
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
