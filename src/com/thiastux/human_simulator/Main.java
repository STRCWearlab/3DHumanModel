package com.thiastux.human_simulator;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Line;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private Spatial human;
    private AnimControl animControl;
    private Skeleton skeleton;
    private Quaternion[] animationQuaternions;
    private TCPDataClient tcpDataClient;
    private Bone[] bones;
    private String[] bonesName = {
        "RightHand", "RightForeArm", "RightArm",
        "LeftHand", "LeftForeArm", "LeftArm",
        "Head", "LowerBack",
        "RightUpLeg", "RightLeg",
        "LeftUpLeg", "LeftLeg"
    };

    public static void main(String[] args) {
        Main app = new Main(args);
        app.start();
    }
    private Quaternion layingQuat;

    public Main(String[] args) {
        tcpDataClient = new TCPDataClient(this, args);
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Application initialization started");
        human = assetManager.loadModel("Models/HumanDressedNormalPose/Human_2_dressed_normalpose.mesh.j3o");
        human.setLocalTranslation(0, -8, 0);
        animControl = human.getControl(AnimControl.class);
        skeleton = animControl.getSkeleton();

        rootNode.attachChild(human);

        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, human, inputManager);

        //Add light to the scene
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White.mult(2.3f));
        rootNode.addLight(light);

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

        //Set color background to white
        viewPort.setBackgroundColor(ColorRGBA.White);

        rootNode.setLocalScale(0.3f, 0.3f, 0.3f);

        animationQuaternions = new Quaternion[12];

        bones = new Bone[12];
        for (int i = 0; i < 12; i++) {
            bones[i] = skeleton.getBone(bonesName[i]);
            bones[i].setUserControl(paused);
            animationQuaternions[i] = new Quaternion();
        }

        //Try to load a town scene
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial gameLevel = assetManager.loadModel("main.scene");
        gameLevel.setLocalTranslation(0, -8.4f, 0);
        gameLevel.setLocalScale(2);
        rootNode.attachChild(gameLevel);

        setPauseOnLostFocus(false);

        tcpDataClient.startExecution();
        System.out.println("Application initialization ended");

        float[] anglesFaceFront = {
            (float) Math.toRadians(90.0), 0.0f, 0.0f
        };
        float[] rotateModel = {
            0.0f, (float) Math.toRadians(180.0), 0.0f
        };
        //bones[7].setUserControl(true);
        //bones[7].setUserTransforms(Vector3f.ZERO, new Quaternion(anglesFaceFront), Vector3f.UNIT_XYZ);
        human.setLocalRotation(new Quaternion(rotateModel));
        layingQuat = new Quaternion(anglesFaceFront);

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (Const.animationStart) {
            getData();
            animateModel();
        }
    }

    @Override
    public void stop() {
        tcpDataClient.stopExecution();
        System.out.println("Application ended");
        super.stop();
    }

    private void getData() {
        animationQuaternions = tcpDataClient.getData();
    }

    private void animateModel() {
        for (int i = 0; i < 12; i++) {
            bones[i].setUserControl(true);
            if (i == 7) {
                bones[i].setLocalRotation(animationQuaternions[i].mult(layingQuat));
                //bones[i].setUserTransforms(Vector3f.ZERO, animationQuaternions[i].mult(layingQuat), Vector3f.UNIT_XYZ);
            } else {
                bones[i].setUserTransforms(Vector3f.ZERO, animationQuaternions[i], Vector3f.UNIT_XYZ);
            }
        }
        skeleton.updateWorldVectors();
    }
}
