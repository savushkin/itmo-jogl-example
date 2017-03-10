package ru.savushkin;

import java.awt.Point;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseInputAdapter {


    private Renderer renderer;

    private Application thePanel;

    private boolean leftButtonDown = false;

    private boolean middleButtonDown = false;

    private boolean rightButtonDown = false;

    public MouseHandler( Renderer renderer, Application thePanel ) {
        this.renderer = renderer;
        this.thePanel = thePanel;
    }

    public void mouseClicked( MouseEvent e ) {
        thePanel.requestFocusInWindow();

        // Update the scene - redraw the display following a mouse event, when
        // not using an animator, else the animator automatically performs updates.
        if (!thePanel.useAnimator) {
            thePanel.drawabledisplay();
        }
    }

    public void mousePressed( MouseEvent e ) {
        thePanel.requestFocusInWindow();

        if (SwingUtilities.isLeftMouseButton(e)) {
            leftButtonDown = true;
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            middleButtonDown = true;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightButtonDown = true;
        }

        // Update the scene - redraw the display following a mouse event, when
        // not using an animator, else the animator automatically performs updates.
        if (!thePanel.useAnimator) {
            thePanel.drawabledisplay();
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftButtonDown = false;
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            middleButtonDown = false;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightButtonDown = false;
        }

        // Update the scene - redraw the display following a mouse event, when
        // not using an animator, else the animator automatically performs updates.
        if (!thePanel.useAnimator) {
            thePanel.drawabledisplay();
        }
    }

    public void mouseMoved(MouseEvent e) {
        // Get the current mouse point
        Point mousePoint = e.getPoint();
        renderer.currentMousePoint.x = mousePoint.x;
        renderer.currentMousePoint.y = mousePoint.y;

        // swap the mouse points to use for the next comparison
        renderer.lastMousePoint.x = renderer.currentMousePoint.x;
        renderer.lastMousePoint.y = renderer.currentMousePoint.y;

        // Update the scene - redraw the display following a mouse event, when
        // not using an animator, else the animator automatically performs updates.
        if (!thePanel.useAnimator) {
            thePanel.drawabledisplay();
        }
    }

    public void mouseDragged(MouseEvent e) {
        thePanel.requestFocusInWindow();

        if (SwingUtilities.isLeftMouseButton(e)
                || SwingUtilities.isMiddleMouseButton(e)
                || SwingUtilities.isRightMouseButton(e)) {

            // Get the current mouse point
            Point mousePoint = e.getPoint();
            renderer.currentMousePoint.x = mousePoint.x;
            renderer.currentMousePoint.y = mousePoint.y;

            // Calculate the difference in mouse movement
            int xDiff = renderer.currentMousePoint.x - renderer.lastMousePoint.x;
            int yDiff = renderer.currentMousePoint.y - renderer.lastMousePoint.y;

            if (leftButtonDown) {
                // If the mouse moved a little along the y direction (up/down) then
                // cause the view to rotate about the 'right' vector of the view - ie
                // pitch the view up/down.
                if (yDiff != 0) {
                    renderer.camera.rotateAroundRightVector(-yDiff / 3.0);
                }

                // If the moouse moved a little along the x direction (left/right) then
                // cause the view to rotate about the 'up' vector of the view - ie
                // yaw the view left/right
                if (xDiff != 0) {
                    renderer.camera.rotateAroundUpVector(-xDiff / 3.0);
                }
            }


            if (middleButtonDown) {
                if (yDiff != 0) {
                    renderer.camera.moveFwd(yDiff*renderer.camera.getMoveSpeed());
                }
            }

            // This only works for the modified FPS mode to get proper motion up/down.
            if(rightButtonDown) {
                if (xDiff != 0) {
                    renderer.camera.moveLeft(xDiff*renderer.camera.getMoveSpeed()*0.1);
                }

                if (yDiff != 0) {
                    renderer.camera.moveUp(yDiff*renderer.camera.getMoveSpeed()*0.1);
                }
            }

            // swap the mouse points to use for the next comparison
            renderer.lastMousePoint.x = renderer.currentMousePoint.x;
            renderer.lastMousePoint.y = renderer.currentMousePoint.y;
        }

        // Update the scene - redraw the display following a mouse event, when
        // not using an animator, else the animator automatically performs updates.
        if (!thePanel.useAnimator) {
            thePanel.drawabledisplay();
        }
    }
}