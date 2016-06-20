/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.experiments;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.thiastux.human_simulator.model.Const;
import com.thiastux.human_simulator.model.Stickman;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author mathias
 */
public class Test2 extends SimpleApplication implements ScreenController {

    private Stickman stickman;
    private DataLoader dataLoader;
    private LogService logService;
    private Quaternion[] animationQuaternions = new Quaternion[12];
    private Geometry terrainGeometry;
    private Quaternion[] previousQuaternions = new Quaternion[12];
    private Quaternion preRot;
    private Quaternion qAlignArmR;
    private Quaternion qAlignArmL;
    private final float TERRAIN_WIDTH = 50f;
    private final float TERRAIN_HEIGHT = 50f;
    private HashMap<Integer, Spatial> skeletonMap = new HashMap<>();
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    private Object lock;
    private HashMap<Integer, List<DatasetChunk>> datasetHashMap;
    private int TEST_NUMSAMPLE = 4;
    private List<DatasetChunk> testChunks;
    private DatasetChunk activeChunk;
    private int activeChunkIndex;
    private long currentTimestamp;
    private long previousTimestamp;
    private float elapsedTime = 0;
    private int SAMPLING_FREQUENCY = 33;
    private int animationIndex=0;

    public Test2(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public Test2(DataLoader dataLoader, LogService logService, Object lock) {
        this.dataLoader = dataLoader;
        this.logService = logService;
        this.lock = lock;
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Test 2 initialization started");
        addReferenceSystem();

        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, rootNode, inputManager);
        chaseCam.setDefaultHorizontalRotation((float) Math.toRadians(90));
        chaseCam.setDefaultVerticalRotation((float) Math.toRadians(30 / 2));
        chaseCam.setDefaultDistance(50f);

        createHumanModel();

        loadTerrain();

        setLightAndShadow();

        initializeInterface();

        computeInitialQuaternions();

        loadDataset();

        setPauseOnLostFocus(false);

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (Const.TEST2_RUNNING && !Const.WAITING_TEST2_ANSWER) {
            getData(tpf);
            animateModel();
        } else if (Const.WAITING_TEST2_ANSWER) {
            askUserLabel();
        } else if (!Const.TEST2_RUNNING) {
            stop();
        }
    }

    @Override
    public void stop() {
        logService.saveTest2Log();
        super.stop();
        destroy();
    }

    private void getData(float tpf) {
        elapsedTime += tpf * 1000;
        if (elapsedTime > SAMPLING_FREQUENCY) {
            animationIndex += (int) elapsedTime / SAMPLING_FREQUENCY;
            try {
                animationQuaternions = activeChunk.getDatasetChunk().get(animationIndex);
                elapsedTime = 0;
            } catch (IndexOutOfBoundsException e) {
                animationIndex=0;
                Const.WAITING_TEST2_ANSWER = true;
            }
        }
    }

    private void animateModel() {
        for (int i = 0; i < 12; i++) {
            Quaternion rotQuat = preProcessingQuaternion(i);
            if (rotQuat != null) {
                stickman.updateModelBonePosition(rotQuat, i);
            }
        }
        if (!Const.useLegs) {
            stickman.rotateLegs(previousQuaternions[0]);
        }
    }

    private Quaternion preProcessingQuaternion(int i) {

        if (animationQuaternions[i] == null) {
            return null;
        }

        //Normalize quaternion to adjust lost of precision using mG.
        Quaternion outputQuat = animationQuaternions[i].normalizeLocal();

        if (i == 2 || i == 3 || i == 4) {
            outputQuat = outputQuat.mult(qAlignArmR);
        }

        if (i == 5 || i == 6 || i == 7) {
            outputQuat = outputQuat.mult(qAlignArmL);
        }

        outputQuat = outputQuat.normalizeLocal();
        outputQuat = outputQuat.mult(preRot);

        previousQuaternions[i] = outputQuat.normalizeLocal();

        outputQuat = conjugate(getPrevLimbQuaternion(i)).mult(outputQuat);

        outputQuat = outputQuat.normalizeLocal();

        return outputQuat;
    }

    private Quaternion conjugate(Quaternion quaternion) {
        return new Quaternion(-quaternion.getX(), -quaternion.getY(), -quaternion.getZ(), quaternion.getW());
    }

    private Quaternion getPrevLimbQuaternion(int i) {
        switch (i) {
            case 1:
            case 3:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return previousQuaternions[i - 1];
            case 2:
            case 5:
            case 8:
            case 10:
                return previousQuaternions[0];
            default:
                return Quaternion.IDENTITY;
        }

    }

    private void addReferenceSystem() {

        Node refNode = new Node("RefNode");

        Line xAxisline = new Line(new Vector3f(0, 0, 0), new Vector3f(3, 0, 0));
        Geometry xAxisGeometry = new Geometry("xAxis", xAxisline);
        Material xLineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        xLineMaterial.getAdditionalRenderState().setLineWidth(2);
        xLineMaterial.setColor("Color", ColorRGBA.Green);
        xAxisGeometry.setMaterial(xLineMaterial);

        Line yAxisline = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 3, 0));
        Geometry yAxisGeometry = new Geometry("yAxis", yAxisline);
        Material yLineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        yLineMaterial.getAdditionalRenderState().setLineWidth(2);
        yLineMaterial.setColor("Color", ColorRGBA.Blue);
        yAxisGeometry.setMaterial(yLineMaterial);

        Line zAxisline = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 3));
        Geometry zAxisGeometry = new Geometry("zAxis", zAxisline);
        Material zLineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        zLineMaterial.getAdditionalRenderState().setLineWidth(2);
        zLineMaterial.setColor("Color", ColorRGBA.Red);
        zAxisGeometry.setMaterial(zLineMaterial);

        refNode.attachChild(xAxisGeometry);
        refNode.attachChild(yAxisGeometry);
        refNode.attachChild(zAxisGeometry);

        refNode.setLocalTranslation(-7, 0, 0);

        rootNode.attachChild(refNode);
    }

    private void createHumanModel() {
        stickman = new Stickman(rootNode, skeletonMap, assetManager);
    }

    private void loadTerrain() {
        Quad terrainMesh = new Quad(TERRAIN_WIDTH, TERRAIN_HEIGHT);
        terrainGeometry = new Geometry("Terrain", terrainMesh);
        terrainGeometry.setLocalRotation(new Quaternion().fromAngles((float) Math.toRadians(-90), 0f, 0f));
        terrainGeometry.setLocalTranslation(-TERRAIN_WIDTH / 2, -(stickman.TORSO_HEIGHT / 2 + stickman.ULEG_LENGTH + stickman.LLEG_LENGTH), TERRAIN_HEIGHT / 2);
        Material terrainMaterial = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        terrainMaterial.setBoolean("UseMaterialColors", true);
        terrainMaterial.setColor("Ambient", ColorRGBA.White);
        terrainMaterial.setColor("Diffuse", ColorRGBA.White);
        terrainGeometry.setMaterial(terrainMaterial);

        terrainGeometry.setShadowMode(RenderQueue.ShadowMode.Receive);

        rootNode.attachChild(terrainGeometry);
    }

    private void setLightAndShadow() {
        //Add light to the scene
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
        rootNode.addLight(sun);

        DirectionalLight sun2 = new DirectionalLight();
        sun2.setColor(ColorRGBA.White);
        sun2.setDirection(new Vector3f(.5f, .5f, .5f).normalizeLocal());
        rootNode.addLight(sun2);

        rootNode.setShadowMode(RenderQueue.ShadowMode.Off);

        stickman.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        terrainGeometry.setShadowMode(RenderQueue.ShadowMode.Receive);

        final int SHADOWMAP_SIZE = 512;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);

    }

    private void computeInitialQuaternions() {
        // Compose two rotations:
        // First, rotate the rendered model to face inside the screen (negative z)
        // Then, rotate the rendered model to have the torso horizontal (facing downwards, leg facing north)
        Quaternion quat1 = new Quaternion().fromAngles((float) Math.toRadians(-90), 0f, 0f);
        Quaternion quat2 = new Quaternion().fromAngles(0f, (float) Math.toRadians(180), 0f);
        preRot = quat1.mult(quat2);

        String print = String.format("qPreRot: %.1f %.1f %.1f %.1f", preRot.getW(), preRot.getX(), preRot.getY(), preRot.getZ());
        System.out.println(print + "    ");

        qAlignArmR = new Quaternion().fromAngles(0f, 0f, (float) Math.toRadians(90));
        print = String.format("qRArmRot: %.1f %.1f %.1f %.1f", qAlignArmR.getW(), qAlignArmR.getX(), qAlignArmR.getY(), qAlignArmR.getZ());
        System.out.println(print + "    ");

        qAlignArmL = new Quaternion().fromAngles(0f, 0f, (float) Math.toRadians(-90));
        print = String.format("qLArmRot: %.1f %.1f %.1f %.1f", qAlignArmL.getW(), qAlignArmL.getX(), qAlignArmL.getY(), qAlignArmL.getZ());
        System.out.println(print + "    ");

        for (int i = 0; i < 12; i++) {
            previousQuaternions[i] = new Quaternion();
        }

    }

    private void loadDataset() {
        datasetHashMap = dataLoader.getDrillTestHashMap();
        testChunks = new ArrayList<>();
        for (Integer key : datasetHashMap.keySet()) {
            List<DatasetChunk> chunks = datasetHashMap.get(key);
            Collections.shuffle(chunks);
            testChunks.addAll(chunks.subList(0, TEST_NUMSAMPLE));
        }
        Collections.shuffle(testChunks);
        activeChunk = testChunks.get(activeChunkIndex);
        previousTimestamp = System.currentTimeMillis();
    }

    private void initializeInterface() {
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                viewPort);

        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/Test2/test2Interface.xml", "controls", this);
    }

    public void saveTest2Event() {
        TextField userLabelTextField = nifty.getCurrentScreen().findNiftyControl("userLabel", TextField.class);
        String userLabel = userLabelTextField.getDisplayedText();
        logService.addTest2Event("{userLabel:"+userLabel+", actualLabel: "+activeChunk.getActualLabel()+"},");
        //System.out.println(userLabel);
        try {
            activeChunk = testChunks.get(++activeChunkIndex);
        } catch (IndexOutOfBoundsException exception) {
            Const.TEST2_RUNNING = false;
        }
        Const.WAITING_TEST2_ANSWER = false;
        guiViewPort.removeProcessor(niftyDisplay);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    private void askUserLabel() {
        if (guiViewPort.getProcessors().isEmpty()) {
            guiViewPort.addProcessor(niftyDisplay);
            TextField userLabelTextField = nifty.getCurrentScreen().findNiftyControl("userLabel", TextField.class);
            userLabelTextField.setText("");
        }
    }

}
