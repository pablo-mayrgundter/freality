package org.freality.gui.three;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import javax.media.j3d.Canvas3D;
import javax.swing.JFrame;
import javax.media.j3d.Bounds;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Locale;
import javax.media.j3d.VirtualUniverse;

/**
 *  VirtualUniverse -> Locale -> BranchGroups -> TransformGroup ->
 *  ViewPlatform <- View
 */
class SceneTest {

    static int width = 640, height = 480;

    public static void main(String [] args) {

        final SimpleScene scene = new SimpleScene();

        final DefaultView view = new DefaultView();

        final BranchGroup bg = scene.createSceneGraph();

        final ViewPlatformGroup vpg = new ViewPlatformGroup(view, scene.getSceneBounds());
        //    view.makeViewBehaviors(vpg, scene.sceneBounds);
        view.attachViewPlatform(vpg.getViewPlatform());

        final VirtualUniverse universe = new VirtualUniverse();
        final Locale locale = new Locale(universe);

        bg.addChild(vpg);

        // Make the scene live.
        locale.addBranchGraph(bg);

        final Canvas3D canvas = view.getCanvas3D();
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Dimension windowSize;

        if (args.length == 1 && args[0].equalsIgnoreCase("-fs")) {
            final Toolkit tk = Toolkit.getDefaultToolkit();
            windowSize = tk.getScreenSize();
            final Window window = new Window(frame);
            window.setSize(windowSize);
            window.add(canvas);
            window.setVisible(true);
        } else {
            windowSize = new Dimension(width, height);
            frame.setSize(windowSize);
            frame.getContentPane().add(canvas);
            frame.setVisible(true);
        }
    }
}
