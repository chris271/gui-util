package com.gurpus.gui.graphics;

import com.gurpus.gui.util.DateFormatter;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Custom OutputStream class to hijack Console output.
 */
class TextAreaOutputStream extends OutputStream {

    //The text area in the JPanel
    private final JTextArea textArea;
    //StringBuilder for each line of output.
    private final StringBuilder sb = new StringBuilder();

    TextAreaOutputStream(final JTextArea textArea) {
        this.textArea = textArea;
        sb.append("[").append(new DateFormatter().extFormat(new Date())).append("] ");
    }

    @Override
    public void write(int b) throws IOException {

        //if \r then do nothing.
        if (b == '\r')
            return;

        //if there is a new line of console output then append it.
        if (b == '\n') {
            final String text = sb.toString() + "\n";
            //As not to interfere with the runtime of the JPanel.
            SwingUtilities.invokeLater(() -> textArea.append(text));
            //Reset the StringBuilder to be on the next line.
            sb.setLength(0);
            sb.append("[").append(new DateFormatter().extFormat(new Date())).append("] ");
            return;
        }

        sb.append((char) b);
    }
}