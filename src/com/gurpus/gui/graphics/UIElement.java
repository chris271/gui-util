package com.gurpus.gui.graphics;

import java.awt.*;

/**
 * Class GUIElement
 * Draw a graphical element onto the UI of a certain type
 * Needs a location, width, height, color, and default string.
 */

public class UIElement {

    private int xLoc, yLoc, w, h;
    private Color color;
    private String text = "";
    private UIElement child = null;
    private volatile boolean clicked = false;
    private volatile boolean keyPressed = false;

    public UIElement() {
    }

    public UIElement(int xLoc, int yLoc, int w, int h, Color color, String text) {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.w = w;
        this.h = h;
        this.text = text;
        this.color = color;
    }

    public int getX() {
        return xLoc;
    }

    public int getY() {
        return yLoc;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public Color getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public UIElement getChild() {
        return child;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public UIElement setChild(UIElement child) {
        this.child = child;
        return this.child;
    }

    public int getxLoc() {
        return xLoc;
    }

    public void setxLoc(int xLoc) {
        this.xLoc = xLoc;
    }

    public int getyLoc() {
        return yLoc;
    }

    public void setyLoc(int yLoc) {
        this.yLoc = yLoc;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setLocation(int xLoc, int yLoc) {
        setxLoc(xLoc);
        setyLoc(yLoc);
    }

    public boolean isKeyPressed() {
        return keyPressed;
    }

    public void setKeyPressed(boolean keyPressed) {
        this.keyPressed = keyPressed;
    }
}
