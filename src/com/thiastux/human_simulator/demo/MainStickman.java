/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thiastux.human_simulator.demo;

import com.thiastux.human_simulator.*;
import com.thiastux.human_simulator.model.Stickman;
import com.thiastux.human_simulator.model.Const;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import java.util.HashMap;

/**
 *
 * @author mathias
 */
public class MainStickman extends SimpleApplication {

    private static boolean DEBUG = false;

    public static void main(String[] args) {

        MainStickman app;

        if (DEBUG) {
            app = new MainStickman();
        } else {
            app = new MainStickman(args);
        }
        app.start();
    }

    private TCPDataClient tcpDataClient;
    private Quaternion[] animationQuaternions;
    private HashMap<Integer, Spatial> skeletonMap = new HashMap<>();
    private Stickman stickman;
    private Geometry terrainGeometry;

    private final float TERRAIN_WIDTH = 50f;
    private final float TERRAIN_HEIGHT = 50f;
    private Quaternion[] previousQuaternions = new Quaternion[12];
    private Quaternion preRot;
    private Quaternion qAlignArmR;
    private Quaternion qAlignArmL;

    public MainStickman() {
        super();
    }

    public MainStickman(String[] args) {
        tcpDataClient = new TCPDataClient(this, args);
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Application initialization started");

        addReferenceSystem();

        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, rootNode, inputManager);
        chaseCam.setDefaultHorizontalRotation((float) Math.toRadians(90));
        chaseCam.setDefaultVerticalRotation((float) Math.toRadians(30 / 2));
        chaseCam.setDefaultDistance(40f);

        setDisplayFps(false);

        setDisplayStatView(false);

        setPauseOnLostFocus(false);

        createHumanModel();

        loadTerrain();

        setLightAndShadow();

        computeInitialQuaternions();

        if (!DEBUG) {
            tcpDataClient.startExecution();
        }

    }

    @Override
    public void simpleUpdate(float tpf) {
        boolean animStart = Const.animationStart;
        if (animStart && !DEBUG) {
            getData();
            animateModel();
        } else {
            //stickman.animateBone(0, 0, true);
        }
    }

    @Override
    public void stop() {
        if (!DEBUG) {
            tcpDataClient.stopExecution();
        }
        System.out.println("\nApplication ended");
        super.stop();
    }

    private void getData() {
        animationQuaternions = tcpDataClient.getData();
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
        
        if (i == 3 || i == 4) {
            //if (i == 2) {
            //outputQuat = new Quaternion(outputQuat.getX(), outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
            //outputQuat = outputQuat.mult(qAlignArmR);
            //outputQuat = outputQuat.normalizeLocal();
        }

        if (i == 6 || i == 7) {
            //if (i == 5) {
            //outputQuat = new Quaternion(outputQuat.getX(), outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
            //outputQuat = outputQuat.mult(qAlignArmL);
            //outputQuat = outputQuat.normalizeLocal();
        }

        
        outputQuat = outputQuat.mult(preRot);
        
        outputQuat = new Quaternion(outputQuat.getX(), -outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
        
        previousQuaternions[i] = outputQuat.normalizeLocal();
        
        outputQuat = conjugate(getPrevLimbQuaternion(i)).mult(outputQuat);

        outputQuat = outputQuat.normalizeLocal();

        return outputQuat;
    }
    
    private void computeInitialQuaternions() {
        // Compose two rotations:
        // First, rotate the rendered model to face inside the screen (negative z)
        // Then, rotate the rendered model to have the torso horizontal (facing downwards, leg facing north)
        Quaternion quat1 = new Quaternion().fromAngles(0f, 0f, (float) Math.toRadians(90));
        Quaternion quat2 = new Quaternion().fromAngles((float) Math.toRadians(-90), 0f, 0f);
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

        refNode.setLocalTranslation(-10, 0, 0);

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

        terrainGeometry.setShadowMode(ShadowMode.Receive);

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

        rootNode.setShadowMode(ShadowMode.Off);

        stickman.setShadowMode(ShadowMode.CastAndReceive);
        terrainGeometry.setShadowMode(ShadowMode.Receive);

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

    

}
