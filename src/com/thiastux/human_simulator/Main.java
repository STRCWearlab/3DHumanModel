package com.thiastux.human_simulator;

import com.thiastux.human_simulator.model.Const;
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
    private Quaternion[] previousQuaternions;
    private TCPDataServer tcpDataClient;
    private Bone[] bones;
    private String[] bonesName = {
        "LowerBack", "Head",
        "RightArm", "RightForeArm", "RightHand",
        "LeftArm", "LeftForeArm", "LeftHand",
        "RightUpLeg", "RightLeg",
        "LeftUpLeg", "LeftLeg"
    };

    private int animationIndex;

    public static void main(String[] args) {
        Main app = new Main(args);
        app.start();
    }

    private Quaternion preRot;
    private Quaternion qAlignArmR;
    private Quaternion qAlignArmL;

    public Main(String[] args) {
        tcpDataClient = new TCPDataServer(this, args);
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Application initialization started");
        human = assetManager.loadModel("Models/HumanDressedNormalPose/Human_2_dressed_normalpose.mesh.j3o");
        //human = assetManager.loadModel("Models/HumanDressedDivided/Human_2_dressed_normalpose.mesh.j3o");
        //human = assetManager.loadModel("Models/HumanDressedHipsDivided/Human_2_dressed_normalpose.mesh.j3o");
        human.setLocalTranslation(0, -8, 0);
        animControl = human.getControl(AnimControl.class);
        skeleton = animControl.getSkeleton();

        rootNode.attachChild(human);

        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, human, inputManager);
        chaseCam.setSmoothMotion(true);
        chaseCam.setDefaultHorizontalRotation((float) Math.toRadians(45));

        //Add light to the scene
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White.mult(2.3f));
        rootNode.addLight(light);

        addReferenceSystem();

        //Set color background to white
        viewPort.setBackgroundColor(ColorRGBA.White);

        rootNode.setLocalScale(0.3f, 0.3f, 0.3f);

        animationQuaternions = new Quaternion[12];
        previousQuaternions = new Quaternion[12];

        bones = new Bone[12];
        for (int i = 0; i < 12; i++) {
            bones[i] = skeleton.getBone(bonesName[i]);
            bones[i].setUserControl(paused);
            animationQuaternions[i] = new Quaternion();
            previousQuaternions[i] = new Quaternion();
        }

        //Try to load a town scene
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial gameLevel = assetManager.loadModel("main.scene");
        gameLevel.setLocalTranslation(0, -8.4f, 0);
        gameLevel.setLocalScale(2);
        rootNode.attachChild(gameLevel);

        setPauseOnLostFocus(false);

        tcpDataClient.startExecution();

        animationIndex = 0;
        System.out.println("Application initialization ended");
        // Compose two rotations:
        // First, rotate the rendered model to face inside the screen (negative z)
        // Then, rotate the rendered model to have the torso horizontal (facing downwards, leg facing north)
        float[] quat1Angles = {(float) Math.toRadians(-90), 0f, 0f};
        Quaternion quat1 = new Quaternion(quat1Angles);
        float[] quat2Angles = {0f, (float) Math.toRadians(180), 0f};
        Quaternion quat2 = new Quaternion(quat2Angles);
        preRot = quat1.mult(quat2);

        String print = String.format("qPreRot: %.1f %.1f %.1f %.1f", preRot.getW(), preRot.getX(), preRot.getY(), preRot.getZ());
        System.out.println(print + "    ");

        float[] qRArmAngles = {0f, 0f, (float) Math.toRadians(90)};
        qAlignArmR = new Quaternion(qRArmAngles);
        print = String.format("qRArmRot: %.1f %.1f %.1f %.1f", qAlignArmR.getW(), qAlignArmR.getX(), qAlignArmR.getY(), qAlignArmR.getZ());
        System.out.println(print + "    ");

        float[] qLArmAngles = {0f, 0f, (float) Math.toRadians(-90)};
        qAlignArmL = new Quaternion(qLArmAngles);
        print = String.format("qLArmRot: %.1f %.1f %.1f %.1f", qAlignArmL.getW(), qAlignArmL.getX(), qAlignArmL.getY(), qAlignArmL.getZ());
        System.out.println(print + "    ");

        animationIndex = 0;

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (Const.animationStart) {
            getData();
            animateModel();
        }
        //debugBoneAnimation(5, 0, true);
    }

    @Override
    public void stop() {
        tcpDataClient.stopExecution();
        System.out.println("\nApplication ended");
        super.stop();
    }

    private void getData() {
        animationQuaternions = tcpDataClient.getData();
    }

    private void animateModel() {
        for (int i = 0; i < 12; i++) {
            bones[i].setUserControl(true);
            //animationIndex % 250 == 0 && animationIndex < 2001
            Quaternion rotQuat = preProcessingQuaternion(i);
            if (rotQuat != null) {
                bones[i].setUserTransforms(Vector3f.ZERO, rotQuat, Vector3f.UNIT_XYZ);
            }
        }
        animationIndex++;
        if (false) {
            System.out.println("");
        }
        skeleton.updateWorldVectors();
    }
    
    private Quaternion preProcessingQuaternion(int i) {
        if (animationQuaternions[i] == null) {
            return Quaternion.IDENTITY;
        }
        
        Quaternion outputQuat = animationQuaternions[i].normalizeLocal();
        
        if (i == 2 || i == 3 || i == 4) {
            //if (i == 2) {
            outputQuat = new Quaternion(outputQuat.getX(), outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
            outputQuat = outputQuat.mult(qAlignArmR);
        }

        if (i == 5 || i == 6 || i == 7) {
            //if (i == 5) {
            outputQuat = new Quaternion(outputQuat.getX(), outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
            outputQuat = outputQuat.mult(qAlignArmL);
        }

        outputQuat = outputQuat.normalizeLocal();
        outputQuat = outputQuat.mult(preRot);

        previousQuaternions[i] = outputQuat.normalizeLocal();

        outputQuat = conjugate(getPrevLimbQuaternion(i)).mult(outputQuat);

        outputQuat = outputQuat.normalizeLocal();
        
        if (i == 2 || i == 3 || i == 4) {
            outputQuat = new Quaternion(-outputQuat.getX(), -outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
        }

        if (i == 5 || i == 6 || i == 7) {
            outputQuat = new Quaternion(-outputQuat.getX(), -outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
        }
        
        return outputQuat;
        
    }

    private Quaternion preProcessingQuaternion(int i, boolean par) {

        if (animationQuaternions[i] == null) {
            return Quaternion.IDENTITY;
        }

        
        //Normalize quaternion to adjust lost of precision using mG.
        Quaternion outputQuat = animationQuaternions[i].normalizeLocal();
        
        

        if (par) {
            String print = String.format("q[%d]: %.1f %.1f %.1f %.1f", i, outputQuat.getW(), outputQuat.getX(), outputQuat.getY(), outputQuat.getZ());
            System.out.print(print + "    ");
        }

        if (i == 2 || i == 3 || i == 4) {
            //if (i == 2) {
            outputQuat = new Quaternion(outputQuat.getX(), outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
            outputQuat = outputQuat.mult(qAlignArmR);
        }

        if (i == 5 || i == 6 || i == 7) {
            //if (i == 5) {
            outputQuat = new Quaternion(outputQuat.getX(), outputQuat.getY(), outputQuat.getZ(), outputQuat.getW());
            outputQuat = outputQuat.mult(qAlignArmL);
        }

        outputQuat = outputQuat.normalizeLocal();
        outputQuat = outputQuat.mult(preRot);

        if (par) {
            String print = String.format("q[%d]*preRot: %.1f %.1f %.1f %.1f", i, outputQuat.getW(), outputQuat.getX(), outputQuat.getY(), outputQuat.getZ());
            System.out.print(print + "    ");
        }

        previousQuaternions[i] = outputQuat.normalizeLocal();

        outputQuat = outputQuat.mult(conjugate(getPrevLimbQuaternion(i)));

        outputQuat = outputQuat.normalizeLocal();

        if (par && i > 0) {
            String print = String.format("q[%d]*conj(prev): %.1f %.1f %.1f %.1f", i, outputQuat.getW(), outputQuat.getX(), outputQuat.getY(), outputQuat.getZ());
            System.out.print(print + "    ");
        }

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

    private void debugBoneAnimation(int boneIndex, int axis, boolean counterClockWise) {
        if (counterClockWise) {
            animationIndex++;
        } else {
            animationIndex--;
        }
        Quaternion rot = new Quaternion();
        switch (axis) {
            case 0:
                rot = new Quaternion(new float[]{(float) Math.toRadians(animationIndex % 360), 0f, 0f});
                break;
            case 1:
                rot = new Quaternion(new float[]{0f, (float) Math.toRadians(animationIndex % 360), 0f});
                break;
            case 2:
                rot = new Quaternion(new float[]{0f, 0f, (float) Math.toRadians(animationIndex % 360)});
                break;
        }
        bones[boneIndex].setUserControl(true);
        bones[boneIndex].setUserTransforms(Vector3f.ZERO,
                rot,
                Vector3f.UNIT_XYZ);

        skeleton.updateWorldVectors();
    }

}
