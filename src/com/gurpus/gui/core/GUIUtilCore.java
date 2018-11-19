package com.gurpus.gui.core;

import com.gurpus.gui.connection.DataAccessor;
import com.gurpus.gui.graphics.GUIUtilUI;
import com.gurpus.gui.graphics.UIElement;
import com.gurpus.gui.util.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class GUIUtilCore implements Runnable {

    private final GUIUtilUI utilUI;
    public final static String OS = System.getProperty("os.name").toLowerCase();
    private final JFileChooser fc = new JFileChooser();
    private boolean testFlag = false;
    private boolean testComplete = false;
    private boolean startTest = false;
    private boolean exitApp = false;
    private BasicProcess testProcess = null;
    private Connection connection = null;
    private File csvInputFile = null;
    private File pdfInputFile = null;
    public static String outputFileName = null;

    public GUIUtilCore(GUIUtilUI utilUI) {
        this.utilUI = utilUI;
        this.connection = DataAccessor.getConnection();
    }

    public void run() {

        greetings();

        while (utilUI.isShowing() && !exitApp) {
            updateCurrentGUIState();
            if (testFlag) {
                try {
                    if (startTest) {
                        testProcess.forceStartProcess(true);
                        startTest = false;
                    } else {
                        testComplete = !testProcess.isAlive();
                    }
                    if (testComplete) {
                        Logger.info("Tests Complete!");
                        testFlag = false;
                        utilUI.refreshWindow();
                    }
                } catch (Exception ex) {
                    Logger.severe("Could Not Complete Tests!");
                    ex.printStackTrace();
                }
            }
            try {
                Thread.sleep(16);
            } catch (Exception ex) {
                Logger.severe(ex.getMessage());
                ex.printStackTrace();
            }
        }
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UtilityProcessManager.runningThreads--;

    }

    private void greetings() {
        Logger.info("Running Application On " + OS);
        Logger.info("GUIUtil Key Controls: ");
        Logger.info("Q => Set Console To Log FINE, INFO, WARN, and SEVERE Level Content");
        Logger.info("W => Set Console To Log INFO, WARN, and SEVERE Level Content");
        Logger.info("E => Set Console To Log WARN and SEVERE Level Content");
        Logger.info("R => Set Console To Log SEVERE Level Content Only");
        Logger.info("1 => Set Data Target To Dev Schema (" + DataAccessor.LOCAL_DEV + ")");
        Logger.info("2 => Set Data Target To Test Schema (" + DataAccessor.LOCAL_TEST + ")");
        Logger.info("3 => Set Data Target To CRS Schema (" + DataAccessor.PROD + ")");
        Logger.info("SPACE => Refresh Window (Refreshes Output Folder File Count And Resets Console Window)");
        Logger.info("UP / PAGE UP => Increase Console Window Size (Page Up Is 10X As Much)");
        Logger.info("DOWN / PAGE DOWN => Decrease Console Window Size (Page Down Is 10X As Much)");
        Logger.info("ESCAPE => Exit Out Program (Once Background Processes End)");
        Logger.warn("TRY NOT TO STRETCH THE CONSOLE WINDOW TO MAX HEIGHT. PRESS SPACE TO RESET!");
    }

    private void updateCurrentGUIState() {
        //Iterate over each element.
        for (UIElement e : utilUI.getGuiElements()) {
            if (testComplete && e.getText().equals("Open Output CSV"))
                e.getChild().setText("Output File Ready!");
            if (testFlag && e.getText().equals("Open Output CSV"))
                e.getChild().setText("No Output Ready...");
            if (e.getChild() != null && e.getChild().getText().equals("Please Select A File...") && csvInputFile != null)
                e.getChild().setText("Ready For Test!");
            if (testComplete && e.getText().equals("Execute Tests")) {
                e.getChild().setText("All Tests Complete!");
                testProcess = new BasicProcess(new UtilityProcessManager(csvInputFile).performTests);
                testFlag = false;
            }
            if (e.isClicked()) {
                switch (e.getText()) {
                    case "Select Input File":
                        if (!testFlag)
                            e.getChild().setText(showFileInput("CSV"));
                        else
                            Logger.warn("Not functional at the moment... check again later.");
                        utilUI.getUiMouseAdapter().resetXDown();
                        utilUI.getUiMouseAdapter().resetYDown();
                        while (utilUI.getKeyCodes().contains(KeyEvent.VK_A))
                            utilUI.getKeyCodes().remove(utilUI.getKeyCodes().indexOf(KeyEvent.VK_A));
                        e.setKeyPressed(false);
                        e.setClicked(false);
                        break;
                    case "Execute Tests":
                        if ((e.getChild().getText().equals("All Tests Complete!") || e.getChild().getText().equals("Ready For Test!")) && !testFlag) {
                            testFlag = true;
                            startTest = true;
                            testComplete = false;
                            e.getChild().setText("Running Tests...");
                            Logger.info("Running tests...");
                            //Allow click animation for one second to show fully.
                            //This is due to disabling of this button after running the test.
                            try {
                                Thread.sleep(1000);
                            } catch (Exception ex) {
                                Logger.severe(ex.getMessage());
                                ex.printStackTrace();
                            }
                        } else {
                            //Disables button when test is running or when files are not selected.
                            Logger.warn("Not functional at the moment... check again later.");
                            e.setClicked(false);
                        }
                        while (utilUI.getKeyCodes().contains(KeyEvent.VK_S))
                            utilUI.getKeyCodes().remove(utilUI.getKeyCodes().indexOf(KeyEvent.VK_S));
                        e.setKeyPressed(false);
                        break;
                    case "Open Output CSV":
                        if (testComplete) {
                            try {
                                //Open file in default csv parser if on windows.
                                if (OS.contains("win"))
                                    Desktop.getDesktop().open(new File(outputFileName));
                                Logger.info("Launching Default CSV reader.");
                                //So that the click will not launch multiple files...
                                while (e.isClicked() && !e.isKeyPressed())
                                    Thread.sleep(100);
                                if (e.isKeyPressed()) {
                                    Thread.sleep(1000);
                                }
                            } catch (Exception ex) {
                                Logger.severe(ex.getMessage());
                                ex.printStackTrace();
                            }
                        } else {
                            Logger.warn("Not functional at the moment... check again later.");
                            e.setClicked(false);
                        }
                        while (utilUI.getKeyCodes().contains(KeyEvent.VK_D))
                            utilUI.getKeyCodes().remove(utilUI.getKeyCodes().indexOf(KeyEvent.VK_D));
                        e.setKeyPressed(false);
                        break;
                    case "Open Output Folder":
                        try {
                            //Open file in default file explorer.
                            if (OS.contains("win")) {
                                Desktop.getDesktop().open(new File("output/"));
                                Logger.info("Launching File Explorer.");
                            }
                            //So that the click will not launch multiple files...
                            while (e.isClicked() && !e.isKeyPressed())
                                Thread.sleep(100);
                            if (e.isKeyPressed()) {
                                Thread.sleep(1000);
                            }
                        } catch (Exception ex) {
                            Logger.severe(ex.getMessage());
                            ex.printStackTrace();
                        }
                        while (utilUI.getKeyCodes().contains(KeyEvent.VK_F))
                            utilUI.getKeyCodes().remove(utilUI.getKeyCodes().indexOf(KeyEvent.VK_F));
                        e.setKeyPressed(false);
                        break;
                    case "Set Console Size":
                        setTextAreaFromInput();
                        e.getChild().setText("Current Size: " + utilUI.getNewRows());
                        utilUI.getUiMouseAdapter().resetXDown();
                        utilUI.getUiMouseAdapter().resetYDown();
                        while (utilUI.getKeyCodes().contains(KeyEvent.VK_Z))
                            utilUI.getKeyCodes().remove(utilUI.getKeyCodes().indexOf(KeyEvent.VK_Z));
                        e.setKeyPressed(false);
                        e.setClicked(false);
                        break;

                    case "Exit Application [Key Command ESC]":
                        e.getChild().setText("Goodbye!");
                        Logger.info("Exiting Application...");
                        exitApp = true;
                        break;
                }
            }
        }
        if (!utilUI.isShowing()) {
            Logger.severe("Cannot find GUI...");
            exitApp = true;
        }
    }

    private String showFileInput(String fileType) {

        //Changes the title dialog to be more user friendly.
        fc.setDialogTitle("Please choose an input file.");
        //Sets the default directory to be in current place of execution.
        fc.setCurrentDirectory(new File("./"));
        switch (fileType) {
            case "CSV":
                fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "CSV"));
                break;
            case "PDF":
                fc.setFileFilter(new FileNameExtensionFilter("PDF Files", "PDF"));
                break;
        }
        //Shows the file chooser and ensures a file is chosen before moving on.
        int returnVal = fc.showOpenDialog(null);
        while (returnVal != JFileChooser.APPROVE_OPTION)
            returnVal = fc.showOpenDialog(null);

        Logger.info("Selected File - " + fc.getSelectedFile().getAbsolutePath());
        switch (fileType) {
            case "CSV":
                csvInputFile = fc.getSelectedFile();
                testProcess = new BasicProcess(new UtilityProcessManager(csvInputFile).performTests);
                break;
            case "PDF":
                pdfInputFile = fc.getSelectedFile();
                break;
        }

        return fc.getSelectedFile().getName();
    }

    private void setTextAreaFromInput() {
        int parsedNumber = 12;
        try {
            parsedNumber = Integer.parseInt(JOptionPane.showInputDialog(null, "Choose An Integer Value", 0));
        } catch (Exception ex) {
            Logger.severe("Error Setting To Default Value: " + ex.getMessage());
        }
        utilUI.setNewRows(parsedNumber);
    }

    private int getIntFromInput(String promptMessage) {
        int parsedNumber = 0;
        try {
            parsedNumber = Integer.parseInt(JOptionPane.showInputDialog(null, promptMessage, 0));
        } catch (Exception ex) {
            Logger.severe("Error Setting To Default Value: " + ex.getMessage());
        }
        return parsedNumber;
    }

    private String getStringFromInput(String promptMessage) {
        String str = null;
        try {
            str = JOptionPane.showInputDialog(null, promptMessage, null);
        } catch (Exception ex) {
            Logger.severe("Error Setting To Default Value: " + ex.getMessage());
        }
        return str;
    }



}
