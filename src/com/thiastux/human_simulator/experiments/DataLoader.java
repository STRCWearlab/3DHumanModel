/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.experiments;

import com.jme3.math.Quaternion;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.org.apache.xalan.internal.xsltc.compiler.NodeTest;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import com.thiastux.human_simulator.model.Const;
import com.thiastux.human_simulator.Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author mathias
 */
public class DataLoader {

    private HashMap<Integer, List<DatasetChunk>> testDrillDatasetHashMap;
    private HashMap<Integer, List<DatasetChunk>> testAdlDatasetHashMap;

    private String drillDatasetPath;
    private String adlDatasetPath;
    private HashMap<Integer, Float[]> columnIndexMap;
    private List<Quaternion[]> demoQuaternionsList;

    private final int DEMO_DURATION = 120;
    private final int TEST1_DURATION = 600;
    private final int SAMPLING_RATE = 33;
    private List<Test1Entry> test1DrillQuaternionsList;
    private List<Test1Entry> test1AdlQuaternionsList;

    public Quaternion[] animationQuaternions;
    public Test1Entry test1AnimationEntry;

    private final Object lock;
    private TestLoader testLoader;

    private int[] openDoorLabels = {
        406516, 406517
    };
    
    private int[] closeDoorLabels = {
        404516, 404517
    };

    private int[] openDrawerLabels = {
        406519, 406511, 406508
    };

    private int[] closeDrawerLabels = {
        404519, 404511, 404508
    };
    
    public DataLoader(String datasetPath1, String datasetPath2, Object lockObject, String[] args) {
        lock = lockObject;

        this.drillDatasetPath = datasetPath1;
        this.adlDatasetPath = datasetPath2;

        demoQuaternionsList = new ArrayList<>();

        testDrillDatasetHashMap = new HashMap<>();
        test1DrillQuaternionsList = new ArrayList<>();

        testAdlDatasetHashMap = new HashMap<>();
        test1AdlQuaternionsList = new ArrayList<>();

        animationQuaternions = new Quaternion[12];

        parseParameters(args);
        initializeDatasets();
    }
    
    public List<Quaternion[]> getDemoData(){
        return demoQuaternionsList;
    }
    
    public HashMap<Integer, List<DatasetChunk>> getDrillTestHashMap(){
        return testDrillDatasetHashMap;
    }
    
    public List<Test1Entry> getTest1DrillQuaternionsList(){
        return test1DrillQuaternionsList;
    }

    private void initializeDatasets() {
        try {
            String readLine;
            int demoDatasetCount = DEMO_DURATION * 1000 / SAMPLING_RATE;
            int test1DatasetCount = TEST1_DURATION * 1000 / SAMPLING_RATE;
            System.out.println("Loading of the file:");
            System.out.println(drillDatasetPath);
            LineNumberReader lnr = new LineNumberReader(new FileReader(drillDatasetPath));
            lnr.skip(Long.MAX_VALUE);
            long numLinesTot = (lnr.getLineNumber() + 1);
            System.out.println(numLinesTot + " lines read.");
            int numLinesRead = 0;
            System.out.println("Start of sending:");
            FileReader fileReader = new FileReader(drillDatasetPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            readLine = bufferedReader.readLine();
            int previousActivityLabel = 0;
            DatasetChunk chunk = new DatasetChunk(previousActivityLabel);
            while (readLine != null) {
                numLinesRead++;
                System.out.print(numLinesRead * 100 / numLinesTot + "%\r");
                String[] values = readLine.split(" ");
                float qw, qx, qy, qz;
                int activityLabel;
                Quaternion[] quaternions = new Quaternion[12];
                long timestamp = Long.parseLong(values[0]);
                activityLabel = Integer.parseInt(values[values.length - 1]);
                for (int i = 0; i < 12; i++) {
                    try {
                        Float[] columnValues = columnIndexMap.get(i);
                        qw = Float.parseFloat(values[columnValues[0].intValue()]);
                        qx = Float.parseFloat(values[columnValues[1].intValue()]);
                        qy = Float.parseFloat(values[columnValues[2].intValue()]);
                        qz = Float.parseFloat(values[columnValues[3].intValue()]);
                        quaternions[i] = new Quaternion(qy / 1000.0f, qz / 1000.0f, qx / 1000.0f, qw / 1000.0f);
                    } catch (NullPointerException e) {
                        quaternions[i] = null;
                    }
                }
                if (numLinesRead < demoDatasetCount) {
                    demoQuaternionsList.add(quaternions);
                } else if (numLinesRead - demoDatasetCount < test1DatasetCount) {
                    test1DrillQuaternionsList.add(new Test1Entry(timestamp, quaternions, activityLabel));
                }
                if (activityLabel != 0) {
                    if (activityLabel != previousActivityLabel) {
                        List<DatasetChunk> datasetChunks = null;
                        if (ArrayUtils.contains(openDoorLabels, previousActivityLabel)) {
                            if (testDrillDatasetHashMap.containsKey(openDoorLabels[0])) {
                                datasetChunks = testDrillDatasetHashMap.get(openDoorLabels[0]);
                            } else {
                                datasetChunks = new ArrayList<>();
                            }
                            datasetChunks.add(chunk);
                            testDrillDatasetHashMap.put(openDoorLabels[0], datasetChunks);
                        } else if (ArrayUtils.contains(openDrawerLabels, previousActivityLabel)) {
                            if (testDrillDatasetHashMap.containsKey(openDrawerLabels[0])) {
                                datasetChunks = testDrillDatasetHashMap.get(openDrawerLabels[0]);
                            } else {
                                datasetChunks = new ArrayList<>();
                            }
                            datasetChunks.add(chunk);
                            testDrillDatasetHashMap.put(openDrawerLabels[0], datasetChunks);
                        } else if (ArrayUtils.contains(closeDoorLabels, previousActivityLabel)) {
                            if (testDrillDatasetHashMap.containsKey(closeDoorLabels[0])) {
                                datasetChunks = testDrillDatasetHashMap.get(closeDoorLabels[0]);
                            } else {
                                datasetChunks = new ArrayList<>();
                            }
                            datasetChunks.add(chunk);
                            testDrillDatasetHashMap.put(closeDoorLabels[0], datasetChunks);
                        } else if (ArrayUtils.contains(closeDrawerLabels, previousActivityLabel)) {
                            if (testDrillDatasetHashMap.containsKey(closeDrawerLabels[0])) {
                                datasetChunks = testDrillDatasetHashMap.get(closeDrawerLabels[0]);
                            } else {
                                datasetChunks = new ArrayList<>();
                            }
                            datasetChunks.add(chunk);
                            testDrillDatasetHashMap.put(closeDrawerLabels[0], datasetChunks);
                        } else {
                            if (testDrillDatasetHashMap.containsKey(previousActivityLabel)) {
                                datasetChunks = testDrillDatasetHashMap.get(previousActivityLabel);
                            } else {
                                datasetChunks = new ArrayList<>();
                            }
                            datasetChunks.add(chunk);
                            testDrillDatasetHashMap.put(previousActivityLabel, datasetChunks);
                        }
                        chunk = new DatasetChunk(activityLabel);
                        previousActivityLabel = activityLabel;
                    } else {
                        chunk.setActualLabel(activityLabel);
                        chunk.addQuaternions(quaternions);
                    }
                }
                readLine = bufferedReader.readLine();
            }
            testDrillDatasetHashMap.remove(0);
            System.out.println("\nDrill dataset loaded.");
        } catch (IOException ex) {

        }
        /*try {
            String readLine;
            int demoDatasetCount = DEMO_DURATION * 1000 / SAMPLING_RATE;
            int test1DatasetCount = TEST1_DURATION * 1000 / SAMPLING_RATE;
            System.out.println("Loading of the file:");
            System.out.println(adlDatasetPath);
            LineNumberReader lnr = new LineNumberReader(new FileReader(adlDatasetPath));
            lnr.skip(Long.MAX_VALUE);
            long numLinesTot = (lnr.getLineNumber() + 1);
            System.out.println(numLinesTot + " lines read.");
            int numLinesRead = 0;
            System.out.println("Start of sending:");
            FileReader fileReader = new FileReader(adlDatasetPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            readLine = bufferedReader.readLine();
            int previousActivityLabel = 0;
            DatasetChunk chunk = new DatasetChunk();
            while (readLine != null) {
                numLinesRead++;
                System.out.print(numLinesRead * 100 / numLinesTot + "%\r");
                String[] values = readLine.split(" ");
                float qw, qx, qy, qz;
                int activityLabel;
                Quaternion[] quaternions = new Quaternion[12];
                long timestamp = Long.parseLong(values[0]);
                activityLabel = Integer.parseInt(values[values.length - 1]);
                for (int i = 0; i < 12; i++) {
                    try {
                        Float[] columnValues = columnIndexMap.get(i);
                        qw = Float.parseFloat(values[columnValues[0].intValue()]);
                        qx = Float.parseFloat(values[columnValues[1].intValue()]);
                        qy = Float.parseFloat(values[columnValues[2].intValue()]);
                        qz = Float.parseFloat(values[columnValues[3].intValue()]);
                        quaternions[i] = new Quaternion(qy / 1000.0f, qz / 1000.0f, qx / 1000.0f, qw / 1000.0f);
                    } catch (NullPointerException e) {
                        quaternions[i] = null;
                    }
                }
                //TODO check if Adl dataset has the initial phase.
                if (numLinesRead < demoDatasetCount) {
                    demoQuaternionsList.add(quaternions);
                } else if (numLinesRead - demoDatasetCount < test1DatasetCount) {
                    test1DrillQuaternionsList.add(new Test1Entry(timestamp, quaternions, activityLabel));
                }
                if (activityLabel != 0) {
                    if (activityLabel != previousActivityLabel) {
                        List<DatasetChunk> datasetChunks;
                        if (testDrillDatasetHashMap.containsKey(previousActivityLabel)) {
                            datasetChunks = testDrillDatasetHashMap.get(previousActivityLabel);
                        } else {
                            datasetChunks = new ArrayList<>();
                        }
                        datasetChunks.add(chunk);
                        testDrillDatasetHashMap.put(previousActivityLabel, datasetChunks);
                        chunk = new DatasetChunk();
                        previousActivityLabel = activityLabel;
                    } else {
                        chunk.addQuaternions(quaternions);
                    }
                }
                readLine = bufferedReader.readLine();
            }
            System.out.println("\nAll data loaded.");
        } catch (IOException ex) {

        }*/
    }

    private void parseParameters(String[] args) {
        columnIndexMap = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            columnIndexMap.put(i, new Float[4]);
        }
        try {
            //If the numbers of the parameters isn't
            //multiple of 5, throw an exception
            if (args.length % 5 != 0) {
                throw new WrongNumberArgsException("Wrong number of parameters!");
            }
            for (int i = 0; i < args.length; i += 5) {

                //Read the command
                String param = args[i];

                if (param.endsWith("l")) {
                    Const.useLegs = true;
                }

                //Get the index of the array corresponding to the command
                Float[] paramsValues = new Float[4];
                Integer limbColIndex = Const.BindColumIndex.get(param).getCode();
                paramsValues[0] = Float.parseFloat(args[i + 1]);
                paramsValues[1] = Float.parseFloat(args[i + 2]);
                paramsValues[2] = Float.parseFloat(args[i + 3]);
                paramsValues[3] = Float.parseFloat(args[i + 4]);

                columnIndexMap.put(limbColIndex, paramsValues);
            }
        } catch (NullPointerException | IndexOutOfBoundsException | WrongNumberArgsException | NumberFormatException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            System.exit(-1);
        }
    }
            
}
