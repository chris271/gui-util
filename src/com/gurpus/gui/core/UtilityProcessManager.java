package com.gurpus.gui.core;

import com.gurpus.gui.connection.DataAccessor;
import com.gurpus.gui.util.DateFormatter;
import com.gurpus.gui.util.Logger;

import java.io.*;
import java.sql.Connection;
import java.util.Date;

public class UtilityProcessManager {

    private File inputFile;
    static final int MAX_THREADS = 10;
    static volatile int runningThreads = 0;
    private Connection connection = null;
    private File outputFile;

    UtilityProcessManager(File inputFile) {
        this.inputFile = inputFile;
        outputFile = new File(
                "output/OUTPUT_" +
                        new DateFormatter().fileFormat(new Date()) +
                        "_" + inputFile.getName());
    }

    final Runnable performTests = () -> {
        connection = DataAccessor.getConnection();
        try {
            Logger.info("Beginning Test...");
        } catch (Exception ex) {
            Logger.severe("Problem With Tests: Error => '" + ex.toString() + "'");
            ex.printStackTrace();
        } finally {
            runningThreads = runningThreads - 1;
            Logger.info("Process Stopped Current Number Of Running Processes '" + runningThreads + "'");
        }
    };

    public String getOutputFileName() {
        return outputFile.getName();
    }

    public static int getRunningThreads() {
        return runningThreads;
    }

}
