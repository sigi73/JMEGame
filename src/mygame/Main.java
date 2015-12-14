package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.HashMap;

public class Main extends SimpleApplication
{
    private ThirdPersonCharacterControl playerController;

    public static void main(String... args)
    {
        Main gp = new Main();
        gp.start();
    }

    @Override
    public void simpleInitApp()
    {
        // Disables the default diagnostic
        setDisplayFps(false);
        setDisplayStatView(false);
        
//        flyCam.setEnabled(false);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        Spatial sceneModel = assetManager
                .loadModel("Scenes/ManyLights/Main.scene");
        sceneModel.scale(1f, .5f, 1f);
        CollisionShape sceneShape = CollisionShapeFactory
                .createMeshShape(sceneModel);
        RigidBodyControl scene = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(scene);
        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(scene);

        Spatial playerModel = assetManager
                .loadModel("Models/MainCharacter3/MainCharacter3.j3o");
        
        playerModel.scale(2.f);
        playerModel.rotate(0f, 180f * FastMath.DEG_TO_RAD, 0f);
        playerModel.setLocalTranslation(-5f, 2f, 5f);
        
        ChaseCamera chaseCam = new ChaseCamera(cam, playerModel, inputManager);
        chaseCam.setHideCursorOnRotate(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setSmoothMotion(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 1f, 0));
        chaseCam.setDefaultDistance(7f);
        chaseCam.setMaxDistance(8f);
        chaseCam.setMinDistance(6f);
        chaseCam.setUpVector(Vector3f.UNIT_Y);
//        chaseCam.setChasingSensitivity(5f);
        chaseCam.setTrailingEnabled(false);
        chaseCam.setRotationSpeed(2f);
        
        HashMap<String, String> anims = new HashMap<>();
        anims.put("Idle", "Idle");
        anims.put("Move", "Running");
               
        playerController = new ThirdPersonCharacterControl(inputManager, anims, playerModel);
        playerController.cam = cam;
        //playerController.setCamera(cam);
        playerModel.addControl(playerController);
        
        rootNode.attachChild(playerModel);
        bulletAppState.getPhysicsSpace().add(playerModel);

        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-.1f, -.7f, -1f));
        rootNode.addLight(sun);
    }

    @Override
    public void simpleUpdate(float tpf)
    {
        playerController.update(tpf);
    }

    @SuppressWarnings("unused")
    private void log(Object tag, Object val)
    {
        System.out.println(tag.toString() + ": " + val.toString());
    }
}
