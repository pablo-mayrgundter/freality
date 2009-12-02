package org.freality.gui.three;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.behaviors.interpolators.KBRotPosScaleSplinePathInterpolator;
import com.sun.j3d.utils.behaviors.interpolators.KBKeyFrame;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.*;

public class PickZoomBehavior extends Behavior {

    TransformGroup vpTransformGroup;
    PickCanvas pickCanvas;

    public PickZoomBehavior(Canvas3D sceneCanvas, BranchGroup sceneGroup, TransformGroup vpTransformGroup) {
        this.vpTransformGroup = vpTransformGroup;
        pickCanvas = new PickCanvas(sceneCanvas, sceneGroup);
        pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
        pickCanvas.setTolerance(3.0f);
    }

    public void initialize() {
        System.out.println("PickZoomBehavior: initialize");
        wakeupOn (new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED));
    }

    BranchGroup behaviorBranchGroup = null;
    KBRotPosScaleSplinePathInterpolator splineInterp = null;
    TransformGroup dummy = new TransformGroup();
    public void processStimulus (Enumeration criteria) {
        System.err.println("PickZoomBehavior: processStimulus: " + criteria);
        try {
            WakeupCriterion wakeup;
            AWTEvent[] event;
            int eventId;
            PickResult pickResult;

            System.err.println("0");
            if (behaviorBranchGroup != null) { // HACK
                splineInterp.setTarget(dummy);
                behaviorBranchGroup.detach();
            }

            while (criteria.hasMoreElements()) {
                System.err.println("1");
                wakeup = (WakeupCriterion) criteria.nextElement();
                System.err.println("2");
                if (wakeup instanceof WakeupOnAWTEvent) {
                    System.err.println("3");

                    event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                    for (int i = 0; i < event.length; i++) { 

                        eventId = event[i].getID();
                        System.err.println("4");
                        if (eventId == MouseEvent.MOUSE_CLICKED) {

                            int x = ((MouseEvent) event[i]).getX();
                            int y = ((MouseEvent) event[i]).getY();
                            pickCanvas.setShapeLocation(x, y);
                
                            System.err.println("5: "+ x +","+ y);
                            Point3d eyePos = pickCanvas.getStartPosition();
                            pickResult = pickCanvas.pickClosest();

                            if (pickResult != null) {
                                System.err.println("PickZoomBehavior: processStimulus: picked node");

                                KBKeyFrame [] keyFrames = new KBKeyFrame[3];

                                int splineFlag = 0; // {0 == spline, 1 == linear}
                                float frameNdx, heading, pitch, bank, tension = 0f, continuity = 0f, bias = 0f;
                                Point3f position, scale;

                                Transform3D vpTransform = new Transform3D();
                                //              vpTransformGroup.getLocalToVworld(vpTransform);
                                vpTransformGroup.getTransform(vpTransform);

                                Vector3f vpTranslation = new Vector3f();
                                vpTransform.get(vpTranslation);

                                Point3f from = new Point3f(vpTranslation.x, vpTranslation.y, vpTranslation.z);
                                System.out.println("going from: " + vpTranslation.x + ", " + vpTranslation.y + ", " + vpTranslation.z);

                                Node pickedNode = pickResult.getObject();
                                Transform3D pickedTransform = new Transform3D();
                                pickedNode.getLocalToVworld(pickedTransform);

                                Vector3f pickedTranslation = new Vector3f();
                                pickedTransform.get(pickedTranslation);

                                Point3f to = new Point3f(pickedTranslation.x * 0.8f, pickedTranslation.y * 0.8f, pickedTranslation.z * 0.8f);

                                //              pickedTransform.lookAt(new Point3d(pickedTranslation.x, pickedTranslation.y, pickedTranslation.z), new Point3d(to), new Vector3d());

                                System.out.println("going to: " + pickedTranslation.x + ", " + pickedTranslation.y + ", " + pickedTranslation.z);

                                frameNdx = 0f;
                                position = from;
                                heading = 0f;
                                pitch = 0f;
                                bank = 0f;
                                scale = new Point3f(1f, 1f, 1f);
                                keyFrames[0] = new KBKeyFrame(frameNdx, splineFlag, position, heading, pitch, bank, scale, tension, continuity, bias);

                                frameNdx = 0.5f;
                                position = new Point3f(0f, 0f, 0f);
                                //              heading = 0f;
                                //              pitch = 0f;
                                bank = 0.1f;
                                //              scale = new Point3f(1f, 1f, 1f);
                                keyFrames[1] = new KBKeyFrame(frameNdx, splineFlag, position, heading, pitch, bank, scale, tension, continuity, bias);

                                frameNdx = 1f;
                                position = to;
                                //              heading = (float)pickedTransform.rotY;
                                //              pitch = 0f;
                                bank = 0f;
                                //              scale = new Point3f(scaleasdf);
                                keyFrames[2] = new KBKeyFrame(frameNdx, splineFlag, position, heading, pitch, bank, scale, tension, continuity, bias);

                                Alpha interpAlpha = new Alpha (2,
                                                               0,
                                                               0, // Wait 2 seconds to start.
                                                               10000, // Run for 10 seconds.
                                                               3000, 0); // Accelerate/Decellerate time.

                                splineInterp = new KBRotPosScaleSplinePathInterpolator(interpAlpha, vpTransformGroup, new Transform3D(), keyFrames);
                                //                splineInterp.setSchedulingBounds(sceneBounds);
                                splineInterp.setEnable(true);
                                behaviorBranchGroup = new BranchGroup();
                                behaviorBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
                                behaviorBranchGroup.addChild(splineInterp);
                                vpTransformGroup.addChild(behaviorBranchGroup);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize();
    }
}
