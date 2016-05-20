package com.thiastux.human_simulator;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
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
        "head", "chest",
        "upper_arm.R", "upper_arm.L",
        "forearm.R", "forearm.L",
        "hand.R", "hand.L",
        "thigh.R", "thigh.L",
        "shin.R", "shin.L"
    };
    
    
    public static void main(String[] args) {
        Main app = new Main(args);
        app.start();
    }

    public Main(String[] args) {
        tcpDataClient = new TCPDataClient(this, args);
    }

    @Override
    public void simpleInitApp() {
        human = assetManager.loadModel("Models/HumanModelDressed/HumanModel_dressed.mesh.j3o");
        human.setLocalTranslation(0, -8, 0);
        animControl = human.getControl(AnimControl.class);
        skeleton = animControl.getSkeleton();

        rootNode.attachChild(human);

        flyCam.setEnabled(false);

        //Add light to the scene
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);

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

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        getData();
        animateModel();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void getData() {
        animationQuaternions = tcpDataClient.getData();
    }

    private void animateModel() {
        
    }
}
