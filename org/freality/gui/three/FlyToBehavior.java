package org.freality.gui.three;

import com.sun.j3d.utils.behaviors.interpolators.KBRotPosScaleSplinePathInterpolator;
import com.sun.j3d.utils.behaviors.interpolators.KBKeyFrame;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.media.j3d.Alpha;
import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.PositionPathInterpolator;
import javax.media.j3d.RotPosPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Point3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;

// Should move to freality 3d.
public class FlyToBehavior extends Behavior {

    final TransformGroup vpTG;
    final Node [] destinationNodes;

    BranchGroup behaviorBranchGroup = null;
    KBRotPosScaleSplinePathInterpolator splineInterp = null;

    /** @param destinationNodes Must be length 10. */
    public FlyToBehavior(TransformGroup vpTG, Node [] destinationNodes, Bounds sceneBounds) {
        this.vpTG = vpTG;
        if (destinationNodes.length != 10)
            throw new IllegalArgumentException("Destination node array must be length 10.");
        this.destinationNodes = destinationNodes;
        setSchedulingBounds(sceneBounds);
    }

    public void initialize() {
        wakeupOn (new WakeupOnAWTEvent(KeyEvent.KEY_TYPED));
    }

    public void processStimulus (Enumeration criteria) {

        try {
            WakeupCriterion wakeup;
            AWTEvent [] events;
            int eventId;

            if (behaviorBranchGroup != null) { // HACK
                behaviorBranchGroup.detach();
                behaviorBranchGroup = null;
            }


            while (criteria.hasMoreElements()) {
                wakeup = (WakeupCriterion) criteria.nextElement();
                if (wakeup instanceof WakeupOnAWTEvent) {

                    events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                    for (int i = 0; i < events.length; i++) { 

                        eventId = events[i].getID();
                        if (eventId == KeyEvent.KEY_TYPED) {
                            final KeyEvent event = (KeyEvent) events[i];
                            final char c = event.getKeyChar();
                            if (((int)'0' <= (int)c) && ((int)c <= (int)'9')) {

                                final int ndx = (int)c - (int)'0';
                                doIt(destinationNodes[ndx]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        initialize();
    }

    public void doIt(Node pickedNode) {

        final Transform3D vpT = new Transform3D();
        final Vector3f vpTr = new Vector3f();
        vpTG.getTransform(vpT);
        vpT.get(vpTr);

        final Transform3D pickedT = new Transform3D();
        final Vector3f pickedTr = new Vector3f();
        pickedNode.getLocalToVworld(pickedT);
        pickedT.get(pickedTr);

        final float [] knots = new float[]{0f, 0.33f, 0.66f, 1f};
        final Point3f [] points = new Point3f[]{
            new Point3f(vpTr.x, vpTr.y, vpTr.z),
            new Point3f(vpTr.x, vpTr.y, vpTr.z),
            new Point3f(pickedTr.x * 0.99f, pickedTr.y * 0.99f, pickedTr.z * 0.99f),
            new Point3f(pickedTr.x * 0.999f, pickedTr.y * 0.999f, pickedTr.z * 0.999f)
        };
        final Quat4f [] quats = new Quat4f[]{
            new Quat4f(), // Start at orientation of view platform.
            new Quat4f(), // Turn orientation towards object.
            new Quat4f(),
            new Quat4f()
        };

        // Start with current orientation.
        vpT.get(quats[0]);

        // Now look at object.
        vpT.lookAt(new Point3d(vpTr), new Point3d(pickedTr), new Vector3d(0, 1, 0));
        vpT.invert();
        vpT.get(quats[1]);

        vpT.lookAt(new Point3d(vpTr), new Point3d(pickedTr), new Vector3d(0, 1, 0));
        vpT.invert();
        vpT.get(quats[2]);

        vpT.lookAt(new Point3d(points[2]), new Point3d(points[3]), new Vector3d(0, 1, 0));
        vpT.invert();
        vpT.get(quats[3]);

        final RotPosPathInterpolator interp = new RotPosPathInterpolator(getAlpha(), vpTG, new Transform3D(), knots, quats, points);
        interp.setSchedulingBounds(getSchedulingBounds());

        behaviorBranchGroup = new BranchGroup();
        behaviorBranchGroup.setCapability(BranchGroup.ALLOW_DETACH); // Detached above.
        behaviorBranchGroup.addChild(interp);

        interp.setEnable(true);

        vpTG.addChild(behaviorBranchGroup);
    }

    Alpha getAlpha() {
        final Alpha alpha = new Alpha (1,
                                       0,
                                       0,
                                       5000, // Run for 10 seconds.
                                       1500, 1000); // Accelerate/Decellerate time.
        alpha.setStartTime(System.currentTimeMillis());
        return alpha;
    }
}

/*
        float frameNdx, heading, pitch, bank, tension = 0f, continuity = 0f, bias = 0f;
        Point3f position, scale;

        // Get starting translation.
        final Transform3D vpT = new Transform3D();
        //                                vpTG.getLocalToVworld(vpT);
        vpTG.getTransform(vpT);
        final Vector3f vpTranslation = new Vector3f();
        vpT.get(vpTranslation);
        final Point3f from = new Point3f(vpTranslation.x, vpTranslation.y, vpTranslation.z);

        // Get destination translation.
        final Transform3D pickedTransform = new Transform3D();
        pickedNode.getLocalToVworld(pickedTransform);
        final Vector3f pickedTranslation = new Vector3f();
        pickedTransform.get(pickedTranslation);
        float toX = pickedTranslation.x * 0.995f;
        float toY = pickedTranslation.y * 0.995f;
        float toZ = pickedTranslation.z * 0.995f;
        final Point3f to = new Point3f(toX, toY, toZ);
        
        // Make the interpolator.
        position = from;
        System.err.println("HEADING towards:  " + vpTranslation.z);
        System.err.println("CHANGING towards: " + toZ);
        final int splineFlag = 0; // {0 == spline, 1 == linear}
        heading = vpTranslation.angle(new Vector3f());
        pitch = 0f;
        bank = 0f;
        scale = new Point3f(1f, 1f, 1f);

        final KBKeyFrame [] keyFrames = new KBKeyFrame[3];
        keyFrames[0] = new KBKeyFrame(0.0f, splineFlag, position, heading, pitch, bank, scale, tension, continuity, bias);
        keyFrames[1] = new KBKeyFrame(0.5f, splineFlag, position, heading = (float) Math.PI * 0.5f, pitch, bank, scale, tension, continuity, bias);
        keyFrames[2] = new KBKeyFrame(1.0f, splineFlag, position, heading = (float) Math.PI * 1.0f, pitch, bank, scale, tension, continuity, bias);

        
        frameNdx = 0.5f;
        position = new Point3f(vpTranslation.x + (toX - vpTranslation.x),
                               vpTranslation.y + (toY - vpTranslation.y),
                               vpTranslation.z + (toZ - vpTranslation.z));
        // heading = (float)pickedTransform.rotY();
        keyFrames[1] = new KBKeyFrame(frameNdx, splineFlag, position, heading, pitch, bank, scale, tension, continuity, bias);

        frameNdx = 1f;
        position = to;
        // heading = (float)pickedTransform.rotY();
        keyFrames[2] = new KBKeyFrame(frameNdx, splineFlag, position, heading, pitch, bank, scale, tension, continuity, bias);
        
        Alpha interpAlpha = new Alpha (1,
                                       0,
                                       0,
                                       10000, // Run for 10 seconds.
                                       3000, 1000); // Accelerate/Decellerate time.

        interpAlpha.setStartTime(System.currentTimeMillis());

        splineInterp = new KBRotPosScaleSplinePathInterpolator(interpAlpha, vpTG, new Transform3D(), keyFrames);
        splineInterp.setSchedulingBounds(getSchedulingBounds());
        behaviorBranchGroup = new BranchGroup();
        behaviorBranchGroup.setCapability(BranchGroup.ALLOW_DETACH); // Detached above.
        behaviorBranchGroup.addChild(splineInterp);
        splineInterp.setEnable(true);
        vpTG.addChild(behaviorBranchGroup);
 */
