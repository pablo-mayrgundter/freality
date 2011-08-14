package org.freality.gui.three;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix4d;

/**
 * @author Pablo Mayrgundter
 */
public class MouseLook extends MouseBehavior {

  double x_angle, y_angle;
  double factor = .001;
  Vector3d translation = new Vector3d();
  
  private MouseBehaviorCallback callback = null;

  /**
   * Creates a zoom behavior given the transform group.
   * @param transformGroup The transformGroup to operate on.
   */
  public MouseLook(TransformGroup transformGroup) {
    super(transformGroup);
  }

  /**
   * Creates a default mouse zoom behavior.
   **/
  public MouseLook() {
    super(0);
  }

  /**
   * Creates a zoom behavior.
   * Note that this behavior still needs a transform
   * group to work on (use setTransformGroup(tg)) and
   * the transform group must add this behavior.
   * @param flags
   */
  public MouseLook(int flags) {
    super(flags);
  }

  /**
   * Creates a zoom behavior that uses AWT listeners and behavior
   * posts rather than WakeupOnAWTEvent.  The behavior is added to the
   * specified Component.  A null component can be passed to specify
   * the behavior should use listeners.  Components can then be added
   * to the behavior with the addListener(Component c) method.
   * @param c The Component to add the MouseListener
   * and MouseMotionListener to.
   * @since Java 3D 1.2.1
   */
  public MouseLook(Component c) {
    super(c, 0);
  }

  /**
   * Creates a zoom behavior that uses AWT listeners and behavior
   * posts rather than WakeupOnAWTEvent.  The behaviors is added to
   * the specified Component and works on the given TransformGroup.
   * @param c The Component to add the MouseListener and
   * MouseMotionListener to.  A null component can be passed to specify
   * the behavior should use listeners.  Components can then be added
   * to the behavior with the addListener(Component c) method.
   * @param transformGroup The TransformGroup to operate on.
   * @since Java 3D 1.2.1
   */
  public MouseLook(Component c, TransformGroup transformGroup) {
    super(c, transformGroup);
  }

  /**
   * Creates a zoom behavior that uses AWT listeners and behavior
   * posts rather than WakeupOnAWTEvent.  The behavior is added to the
   * specified Component.  A null component can be passed to specify
   * the behavior should use listeners.  Components can then be added
   * to the behavior with the addListener(Component c) method.
   * Note that this behavior still needs a transform
   * group to work on (use setTransformGroup(tg)) and the transform
   * group must add this behavior.
   * @param flags interesting flags (wakeup conditions).
   * @since Java 3D 1.2.1
   */
  public MouseLook(Component c, int flags) {
    super(c, flags);
    if ((flags & 0xFFFFFF) == INVERT_INPUT)
      invert = true;
  }

  public void mouseEntered(MouseEvent e) {
    x_last = e.getX();
    y_last = e.getY();
  }

  public void mouseReleased(MouseEvent e) {
    x_last = e.getX();
    y_last = e.getY();
  }

  @SuppressWarnings(value="unchecked")
  public void mouseMoved(MouseEvent e) {
    synchronized (mouseq) {
      mouseq.add(e);
      // only need to post if this is the only event in the queue
      if (mouseq.size() == 1) postId(MouseEvent.MOUSE_PRESSED);
    }
  }

  @SuppressWarnings(value="unchecked")
  public void processStimulus (Enumeration criteria) {
    WakeupCriterion wakeup;
    AWTEvent[] events;
    MouseEvent evt;
    // int id;
    // int dx, dy;
    
    while (criteria.hasMoreElements()) {
      wakeup = (WakeupCriterion) criteria.nextElement();
      if (wakeup instanceof WakeupOnAWTEvent) {
        events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
        if (events.length > 0) {
          evt = (MouseEvent) events[events.length - 1];
          doProcess(evt);
        }
      }

      else if (wakeup instanceof WakeupOnBehaviorPost) {
        while (true) {
          synchronized (mouseq) {
            if (mouseq.isEmpty()) break;
            evt = (MouseEvent) mouseq.remove(0);
            // consolodate MOUSE_DRAG events
            while ((evt.getID() == MouseEvent.MOUSE_DRAGGED)
                   && !mouseq.isEmpty()
                   && (((MouseEvent)mouseq.get(0)).getID()
                       == MouseEvent.MOUSE_DRAGGED)) {
              evt = (MouseEvent) mouseq.remove(0);
            }
          }
          doProcess(evt);
        }
      }
            
    }
    wakeupOn (mouseCriterion);
  }

  void doProcess(MouseEvent evt) {
    int id;
    int dx, dy;

    processMouseEvent(evt);
        
    if (true || ((buttonPress) && ((flags & MANUAL_WAKEUP) == 0)) || // "true ||" is a HACK
        ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0))) {

      id = evt.getID();

      if (id == MouseEvent.MOUSE_MOVED && id != MouseEvent.MOUSE_DRAGGED) {

        x = evt.getX();
        y = evt.getY();

        dx = x - x_last;
        dy = y - y_last;
                
        if (!reset) {
          x_angle = dy * factor;
          y_angle = -1.0 * dx * factor;

          transformX.rotX(x_angle);
          transformY.rotY(y_angle);
                    
          transformGroup.getTransform(currXform);
                    
          Matrix4d mat = new Matrix4d();
          // Remember old matrix
          currXform.get(mat);
                    
          // Translate to origin
          currXform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
          if (invert) {
            currXform.mul(currXform, transformX);
            currXform.mul(currXform, transformY);
          } else {
            currXform.mul(transformX, currXform);
            currXform.mul(transformY, currXform);
          }
                    
          // Set old translation back
          Vector3d tr = new 
            Vector3d(mat.m03, mat.m13, mat.m23);
          currXform.setTranslation(tr);
                    
          // Update xform
          transformGroup.setTransform(currXform);
                    
          transformChanged(currXform);
                    
          if (callback != null)
            callback.transformChanged(MouseBehaviorCallback.ROTATE, currXform);
        }
        else {
          reset = false;
        }
                
        x_last = x;
        y_last = y;
      }
      else if (id == MouseEvent.MOUSE_PRESSED) {
        x_last = evt.getX();
        y_last = evt.getY();
      }
    }
  }

  /**
    * Users can overload this method  which is called every time
    * the Behavior updates the transform
    *
    * Default implementation does nothing
    */
  public void transformChanged( Transform3D transform ) {
  }
 
  /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */
  public void setupCallback( MouseBehaviorCallback callback ) {
      this.callback = callback;
  }
}
