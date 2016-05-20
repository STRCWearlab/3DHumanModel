/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator;

import com.jme3.math.Quaternion;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathias
 */
public class TCPDataClient extends Thread {

    private Object lock;
    private int tcpPort;
    private ServerSocket listenSocket;
    private Socket connectedSocket;
    private BufferedReader inputBuffer;
    private Quaternion[] animationPacket;
    private HashMap<Integer, Float[]> columnIndexMap;
    private Quaternion[] priorQuaternions;

    public TCPDataClient(Object lock, String[] args) {
        this.lock = lock;
        animationPacket = new Quaternion[12];
        parseParameters(args);
        initializeSocket();
    }

    @Override
    public void run() {
        String line;

        String[] values;

        float qw;
        float qx;
        float qy;
        float qz;

        while (true) {
            try {
                line = inputBuffer.readLine();
                values = line.split(" ");
                for (int i = 0; i < 12; i++) {
                    try{
                        Float[] columnValues = columnIndexMap.get(i);
                        qw=Float.parseFloat(values[columnValues[0].intValue()]);
                        qx=Float.parseFloat(values[columnValues[1].intValue()]);
                        qy=Float.parseFloat(values[columnValues[2].intValue()]);
                        qz=Float.parseFloat(values[columnValues[3].intValue()]);
                        animationPacket[i] = new Quaternion(qx, qy, qz, qw);
                    }catch(NullPointerException e){
                        animationPacket[i] = Quaternion.IDENTITY;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(TCPDataClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized Quaternion[] getData() {
        return new Quaternion[12];
    }

    private void parseParameters(String[] args) {
        columnIndexMap = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            columnIndexMap.put(i, new Float[4]);
        }
        try {
            if ((args.length - 1) % 5 != 0) {
                throw new WrongNumberArgsException("Wrong number of parameters!");
            }
            tcpPort = Integer.parseInt(args[0]);
            for (int i = 1; i < args.length; i += 4) {
                String param = args[i];
                Float[] paramsValues = new Float[0];
                Integer limbColIndex = null;
                Integer limbPriorIndex = null;
                try {
                    limbColIndex = Const.BindColumIndex.get(args[i]).getCode();
                } catch (NullPointerException e) {
                    limbPriorIndex = Const.PriorQuatIndex.get(args[i]).getCode();
                }
                paramsValues[0] = Float.parseFloat(args[i + 1]);
                paramsValues[1] = Float.parseFloat(args[i + 2]);
                paramsValues[2] = Float.parseFloat(args[i + 3]);
                paramsValues[3] = Float.parseFloat(args[i + 4]);

                columnIndexMap.put(limbColIndex, paramsValues);
                if (limbPriorIndex != null) {
                    priorQuaternions[limbPriorIndex] = new Quaternion(paramsValues[0], paramsValues[1], paramsValues[2], paramsValues[3]);
                }
            }
        } catch (NullPointerException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            System.exit(-1);
        } catch (IndexOutOfBoundsException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            System.exit(-1);
        } catch (WrongNumberArgsException e) {
            Logger.getLogger(TCPDataClient.class.getName()).log(Level.SEVERE, null, e);
            System.exit(-1);
        } catch (NumberFormatException e) {
            Logger.getLogger(TCPDataClient.class.getName()).log(Level.SEVERE, null, e);
            System.exit(-1);
        }
    }

    private void initializeSocket() {
        try {
            listenSocket = new ServerSocket(tcpPort);
            connectedSocket = listenSocket.accept();
            inputBuffer = new BufferedReader(
                    new InputStreamReader(
                            connectedSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(TCPDataClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
