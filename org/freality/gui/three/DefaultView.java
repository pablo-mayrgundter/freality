package org.freality.gui.three;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Bounds;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.View;

/**
 * Sets up a view with some generally useful parameters.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class DefaultView extends View {

    final Canvas3D canvas;

    public DefaultView(int visibilityPolicy, double backClipDistance) {
        addCanvas3D(canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration()));
        setPhysicalBody(new PhysicalBody());
        setPhysicalEnvironment(new PhysicalEnvironment());
        setVisibilityPolicy(visibilityPolicy);
        setBackClipDistance(backClipDistance);
        setFieldOfView(Math.PI / 4.0);
    }

    public DefaultView(double backClipDistance) {
        this(View.VISIBILITY_DRAW_ALL, backClipDistance);
    }

    public DefaultView() {
        this(View.VISIBILITY_DRAW_ALL, 1000000000000.0);
    }

    public Canvas3D getCanvas3D() {
        return canvas;
    }
}
