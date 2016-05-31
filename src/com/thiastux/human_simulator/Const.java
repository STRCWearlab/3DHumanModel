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

    public static boolean animationStart = false;

    private static String[] command
            = {"-tor", "-hea", "-rua", "-rla", "-rha", "-lua", "-lla", "-lha", "-rth", "-rsh", "-lth", "-lsh"};

    private static String[] priorQuatCommand
            = {"-ptor", "-phea", "-prua", "-prla", "-prha", "-plua", "-plla", "-plha", "-prth", "-prsh", "-plth", "-plsh"};

    public enum BindColumIndex {

        tor(0),
        hea(1),
        rua(2),
        rla(3),
        rha(4),
        lua(5),
        lla(6),
        lha(7),
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
        ptor(0),
        phea(1),
        prua(2),
        prla(3),
        prha(4),
        plua(5),
        plla(6),
        plha(7),
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
