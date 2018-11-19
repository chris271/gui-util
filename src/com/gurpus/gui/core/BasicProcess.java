package com.gurpus.gui.core;

import com.gurpus.gui.util.Logger;

public class BasicProcess {

    private Runnable processBody = null;
    private Thread process = null;

    public BasicProcess() {
    }

    public BasicProcess(Runnable processBody) {
        this.processBody = processBody;
        process = new Thread(this.processBody);
    }

    public void setProcessBody(Runnable processBody) {
        this.processBody = processBody;
        process = new Thread(this.processBody);
    }

    public Runnable getProcessBody() {
        return processBody;
    }

    public Thread getProcess() {
        return process;
    }

    public void forceStartProcess(boolean showInLog) {
        this.process.start();
        UtilityProcessManager.runningThreads++;
        if (showInLog) {
            Logger.info("Force Started Process... Current Number Of Running Processes: '" + UtilityProcessManager.runningThreads + "'");
        }
    }

    public void startProcess() {
        while (UtilityProcessManager.runningThreads >= UtilityProcessManager.MAX_THREADS) {
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.process.start();
        UtilityProcessManager.runningThreads++;
        Logger.info("Started process... Current Number Of Running Processes: '" + UtilityProcessManager.runningThreads + "'");
    }

    public boolean isAlive() {
        return process.isAlive();
    }

}
