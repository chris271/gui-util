package com.gurpus.gui.core;

import com.gurpus.gui.graphics.GUIUtilUI;
import com.gurpus.gui.util.Logger;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class GUIUtilMain {

    private static GUIUtilUI utilUi = null;
    private static GUIUtilCore utilCore = null;
    private static JFrame frame = null;

    public static void main(String[] args) {
        try {
            //Attempts to create a Thread which opens a JFrame window and add a graphical panel component.
            Logger.info("GUIUtil Application Started.");
            Logger.info("Opening GUI...");
            SwingUtilities.invokeLater(() -> {

                //Creates a new window named OS Sim
                frame = new JFrame("GUI Utility");

                //Removes the JFrame on clicking close.
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                utilUi = new GUIUtilUI();
                utilCore = new GUIUtilCore(utilUi);
                frame.setContentPane(utilUi);
                Logger.info("GUI Running.");
                frame.pack();
                frame.setVisible(true);
            });
            while (utilUi == null || !utilUi.isShowing()) {
                //Wait until the UI shows up.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            new BasicProcess(utilCore).forceStartProcess(false);
            while (UtilityProcessManager.getRunningThreads() > 0) {
                //Wait until the core threads end.
                try {
                    Thread.sleep(2000);
                    if (!utilUi.isShowing()) {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            while (frame.isActive()) {
                //Wait until the GUI window closes.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Logger.info("GUIUtil Application Closed.");
        }
        System.exit(1);
    }


}
