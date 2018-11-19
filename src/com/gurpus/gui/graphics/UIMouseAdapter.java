package com.gurpus.gui.graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public final class UIMouseAdapter implements MouseListener, MouseWheelListener {

    private volatile int xDown = -1;
    private volatile int yDown = -1;
    private int scrollAmount = 0;

    @Override
    public void mousePressed(MouseEvent e) {
        //Set mouse position to current panel location.
        xDown = e.getX();
        yDown = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //When released mouse position is no longer on panel.
        xDown = -1;
        yDown = -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            int unitsToScroll = e.getUnitsToScroll();
            if (unitsToScroll != 0) {
                scrollAmount = scrollAmount + unitsToScroll * 10;
            }
        }

    }

    public int getXDown() {
        return xDown;
    }

    //Allow for hard mouse reset.
    public void resetXDown() {
        xDown = -1;
    }

    public int getYDown() {
        return yDown;
    }

    //Allow for hard mouse reset.
    public void resetYDown() {
        yDown = -1;
    }

    public int getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = scrollAmount;
    }

}