package com.gurpus.gui.graphics;

import com.gurpus.gui.connection.DataAccessor;
import com.gurpus.gui.core.GUIUtilCore;
import com.gurpus.gui.core.UtilityProcessManager;
import com.gurpus.gui.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.CopyOnWriteArrayList;

public class GUIUtilUI extends JPanel{

    private final CopyOnWriteArrayList<UIElement> guiElements = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Integer> keyCodes = new CopyOnWriteArrayList<>();
    private final UIMouseAdapter uiMouseAdapter;
    private final int BASE_SCREEN_WIDTH = 1280;
    private final int BASE_SCREEN_HEIGHT = 720;
    private final double SCALING = 1.0;
    private final int BASE_WIDTH = (int) (SCALING * 300);
    private final int BASE_PADDING = (int) (SCALING * 80);
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private volatile int currentWidth = BASE_SCREEN_WIDTH;
    private volatile int currentHeight = BASE_SCREEN_HEIGHT;
    private double aspectXRatio = 1.0;
    private double aspectYRatio = 1.0;
    private int newRows = 8;
    private int xLocBase = currentWidth / 2 - (int)(BASE_WIDTH * aspectXRatio) - (int)(BASE_WIDTH / 4 * aspectXRatio);
    private int yLocBase = currentHeight / 8;
    private int keyDelayer = 0;
    private final File output = new File("output/");
    private String[] fileList;

    /**
     * Custom constructor for OSPanel Object.
     *
     * Creates and adds all UIElements to an ArrayList.
     * Sets the children for each button.
     *
     */
    public GUIUtilUI() {
        //Self explanatory
        this.setBackground(Color.WHITE);
        this.setSize(new Dimension(BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT));
        this.setPreferredSize(new Dimension(BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        if (!output.exists() && !output.mkdirs()) {
            Logger.warn("Could Not Create Output Directory.");
        }
        fileList = output.list();
        if (fileList == null) {
            fileList = new String[0];
        }

        addUIElements();

        //Allow adding of new components to the panel in particular positions.
        setLayout(new BorderLayout());
        addScrollPane();



        //Add custom MouseListener using MouseAdapter.
        uiMouseAdapter = new UIMouseAdapter();
        addMouseListener(uiMouseAdapter);
        addMouseWheelListener(uiMouseAdapter);

        //KeyListeners only work on focused windows.
        this.setFocusable(true);
        this.requestFocusInWindow();
        //Anonymous class for KeyListener.
        addKeyListener(getKeyListener());

        DataAccessor.setConnection(DataAccessor.LOCAL_TEST);

    }

    private KeyListener getKeyListener() {
        return new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyCodes.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    Component component = e.getComponent();
                    while (component != null && !(component instanceof GUIUtilUI)) {
                        component = component.getParent();
                    }
                    if (component != null) {
                        GUIUtilUI toolUI = (GUIUtilUI) component;
                        toolUI.remove(scrollPane);
                        toolUI.setNewRows(8);
                        toolUI.addScrollPane();
                        toolUI.refreshWindow();
                        toolUI.greetings();
                    }
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //Sometimes multiple instances of the keycode get added so remove all.
                while(keyCodes.contains(e.getKeyCode()))
                    keyCodes.remove(keyCodes.indexOf(e.getKeyCode()));
            }

            @Override
            public void keyTyped (KeyEvent e) {
                //Must be implemented to satisfy KeyListener.
            }
        };
    }

    private void addUIElements() {
        //Procedurally add all elements to the GUI.
        final int BASE_HEIGHT = (int) (SCALING * 60);
        guiElements.add(new UIElement(xLocBase, yLocBase, BASE_WIDTH, BASE_HEIGHT, Color.WHITE, "Select Input File"));
        guiElements.add(new UIElement(xLocBase, yLocBase + (BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.WHITE, "Execute Tests"));
        guiElements.add(new UIElement(xLocBase, yLocBase + (2 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.WHITE, "Open Output CSV"));
        guiElements.add(new UIElement(xLocBase, yLocBase + (3 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.WHITE, "Open Output Folder"));
        guiElements.add(new UIElement(xLocBase, yLocBase + (4 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.WHITE, "Set Console Size"));
        guiElements.add(new UIElement(xLocBase, yLocBase + (5 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.WHITE, "Exit Application [Key Command ESC]"));


        //Add all children to buttons.
        guiElements.add(guiElements.get(0).setChild(new UIElement(xLocBase + BASE_WIDTH * 2, yLocBase, BASE_WIDTH, BASE_HEIGHT, Color.LIGHT_GRAY, "No File Chosen...")));
        guiElements.add(guiElements.get(1).setChild(new UIElement(xLocBase + BASE_WIDTH * 2, yLocBase + (BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.LIGHT_GRAY, "Please Select A File...")));
        guiElements.add(guiElements.get(2).setChild(new UIElement(xLocBase + BASE_WIDTH * 2, yLocBase + (2 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.LIGHT_GRAY, "No Output Ready...")));
        guiElements.add(guiElements.get(3).setChild(new UIElement(xLocBase + BASE_WIDTH * 2, yLocBase + (3 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.LIGHT_GRAY, "Files In Folder: 0")));
        guiElements.add(guiElements.get(4).setChild(new UIElement(xLocBase + BASE_WIDTH * 2, yLocBase + (4 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.LIGHT_GRAY, "Current Size: " + newRows)));
        guiElements.add(guiElements.get(5).setChild(new UIElement(xLocBase + BASE_WIDTH * 2, yLocBase + (5 * BASE_PADDING), BASE_WIDTH, BASE_HEIGHT, Color.LIGHT_GRAY, "...")));
    }

    private void addScrollPane() {
        textArea = new JTextArea(newRows, 15);
        textArea.setEditable(false);
        textArea.setFocusable(true);
        textArea.addKeyListener(getKeyListener());
        //Set up the custom OutputStream attached to the textArea and title it.
        TextAreaOutputStream guiOutputStream = new TextAreaOutputStream(textArea);
        //Add scrollbars for the TextArea
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //Add the textArea object to the bottom of the page
        add(scrollPane, BorderLayout.SOUTH);
        //Redirect System.out console to the GUI's JTextArea.
        System.setOut(new PrintStream(guiOutputStream));
        System.setErr(new PrintStream(guiOutputStream));
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        //Calls System Graphics Component.
        super.paintComponent(g2d);
        //Iterate over the GUI in order to repaint changes.
        updateUIElements();
        drawUI(g2d);
        //Effectively recalls paintComponent(g);
        repaint();
        try {
            Thread.sleep(16);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method will iteratively draw all UIElements and print any font.
     */
    private void updateUIElements(){

        //Calculate Aspect Ratios
        aspectXRatio = (double)currentWidth / (double)BASE_SCREEN_WIDTH;
        aspectYRatio = (double)currentHeight / (double)BASE_SCREEN_HEIGHT;
        detectWindowChanges();
        if (textArea.getRows() != newRows) {
            textArea.setRows(newRows);
            refreshWindow();
        }
        performKeyEvents();

    }

    private void drawUI(Graphics2D g2d) {
        //Menu Title
        g2d.setFont(new Font("Large Font", Font.BOLD, (int)(32 * ((aspectXRatio + aspectYRatio) / 2))));
        g2d.setColor(Color.RED.darker());
        final int headerWidth = g2d.getFontMetrics().stringWidth("GUIUtil Tool");
        g2d.drawString("GUIUtil Tool",
                currentWidth / 2 - (headerWidth / 2),
                (currentHeight / 16) - uiMouseAdapter.getScrollAmount());
        //End Menu Title
        //Scroll Bar
        g2d.setColor(Color.BLACK);
        g2d.fillRect((int)(currentWidth - 110 * aspectXRatio),
                0,
                (int)(45 * aspectXRatio),
                (int)((currentHeight * 3 / 4) * aspectYRatio));
        g2d.setColor(Color.RED);
        g2d.fillRoundRect((int)(currentWidth - 100 * aspectXRatio),
                (int)(currentHeight / 3 + uiMouseAdapter.getScrollAmount() / 8 * aspectYRatio),
                (int)(25 * aspectXRatio),
                (int)(100 * aspectYRatio),
                (int)(50 * aspectXRatio),
                (int)(50 * aspectYRatio));
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 3; i++) {
            g2d.fillRect((int)(currentWidth - 96 * aspectXRatio),
                    (int)(currentHeight / 3 + (uiMouseAdapter.getScrollAmount() / 8 + (45 + i * 5)) * aspectYRatio),
                    (int)(17 * aspectXRatio),
                    2);
        }
        //End Scroll Bar
        if (uiMouseAdapter.getXDown() != -1) {
            this.requestFocus();
        }

        //Iterate through each UIElement.
        for (UIElement ge : guiElements) {

            // Draws a darker box first then a slightly smaller lighter box over top.
            g2d.setColor(ge.getColor().darker());
            if (ge.isClicked()) {
                g2d.setColor(Color.BLUE.darker());
            }
            if (ge.getChild() == null) {
                g2d.fillRect(ge.getX() - (int)(4 * aspectXRatio),
                        ge.getY() - (int)(4 * aspectYRatio) - uiMouseAdapter.getScrollAmount(),
                        (int)(ge.getWidth() * aspectXRatio + 8 * aspectXRatio),
                        (int)(ge.getHeight() * aspectYRatio + 8 * aspectYRatio));
            } else {
                g2d.fillRoundRect(ge.getX() - (int)(4 * aspectXRatio),
                        ge.getY() - (int)(4 * aspectYRatio) - uiMouseAdapter.getScrollAmount(),
                        (int)(ge.getWidth() * aspectXRatio + 8 * aspectXRatio),
                        (int)(ge.getHeight() * aspectYRatio + 8 * aspectYRatio),
                        (int)(100 * aspectXRatio),
                        (int)(100 * aspectYRatio));
            }
            g2d.setColor(ge.getColor());
            if (ge.isClicked()) {
                g2d.setColor(Color.CYAN);
            }
            if (ge.getChild() == null) {
                g2d.fillRect(ge.getX(),
                        ge.getY() - uiMouseAdapter.getScrollAmount(),
                        (int)(ge.getWidth() * aspectXRatio),
                        (int)(ge.getHeight() * aspectYRatio));
            } else {
                g2d.fillRoundRect(ge.getX(),
                        ge.getY() - uiMouseAdapter.getScrollAmount(),
                        (int)(ge.getWidth() * aspectXRatio),
                        (int)(ge.getHeight() * aspectYRatio),
                        (int)(64 * aspectXRatio),
                        (int)(64 * aspectYRatio));
            }
            // Finished drawing boxes...

            //Draw all font with a black color.
            int textSize = (int)((16 * aspectYRatio + 16 * aspectXRatio) / 2);
            g2d.setFont(new Font("Bold" + textSize, Font.BOLD, textSize));
            g2d.setColor(Color.BLACK);
            int width = g2d.getFontMetrics().stringWidth(ge.getText());
            int height = g2d.getFontMetrics().getHeight();
            while (width >= ge.getWidth() * aspectXRatio && textSize > 0) {
                textSize--;
                g2d.setFont(new Font("Bold" + textSize, Font.BOLD, textSize));
                width = g2d.getFontMetrics().stringWidth(ge.getText());
                height = g2d.getFontMetrics().getHeight();
            }
            g2d.drawString(ge.getText(),
                    ge.getX() + (int)(ge.getWidth() / 2 * aspectXRatio) - (width / 2),
                    ge.getY() + (int)(ge.getHeight() / 2 * aspectYRatio) + (height / 4) - uiMouseAdapter.getScrollAmount());

            //Perform mouse updates on buttons only.
            if (ge.getChild() != null) {
                performMouseUpdates(ge);
            }
        }
    }

    private void performKeyEvents() {
        final int KEY_TIMER_THRESHOLD = 6;
        if (keyDelayer < KEY_TIMER_THRESHOLD) {
            keyDelayer++;
        }
        if (keyCodes.contains(KeyEvent.VK_UP) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            textArea.setRows(++newRows);
            refreshWindow();
        }
        if (keyCodes.contains(KeyEvent.VK_DOWN) && newRows > 1 && keyDelayer >= KEY_TIMER_THRESHOLD) {
            textArea.setRows(--newRows);
            refreshWindow();
        }
        if (keyCodes.contains(KeyEvent.VK_PAGE_UP) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            textArea.setRows(newRows += 10);
            refreshWindow();
        }
        if (keyCodes.contains(KeyEvent.VK_PAGE_DOWN) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            if (newRows > 10) {
                textArea.setRows(newRows -= 10);
            } else {
                textArea.setRows(newRows = 1);
            }
            refreshWindow();
        }
        if (keyCodes.contains(KeyEvent.VK_Q) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            Logger.setLoggerLevel(Logger.FINE);
            Logger.fine("Set Logger Level To FINE");
            keyDelayer = 0;
        }
        if (keyCodes.contains(KeyEvent.VK_W) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            Logger.setLoggerLevel(Logger.INFO);
            Logger.info("Set Logger Level To INFO");
            keyDelayer = 0;
        }
        if (keyCodes.contains(KeyEvent.VK_E) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            Logger.setLoggerLevel(Logger.WARN);
            Logger.warn("Set Logger Level To WARN");
            keyDelayer = 0;
        }
        if (keyCodes.contains(KeyEvent.VK_R) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            Logger.setLoggerLevel(Logger.SEVERE);
            Logger.severe("Set Logger Level To SEVERE");
            keyDelayer = 0;
        }
        if (keyCodes.contains(KeyEvent.VK_ESCAPE)) {
            Logger.info("Key Command Pressed: '" + guiElements.get(16).getText() + "'");
            guiElements.get(17).setClicked(true);
            guiElements.get(17).setKeyPressed(true);
            keyDelayer = 0;
        }
        if (keyCodes.contains(KeyEvent.VK_1) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            if (UtilityProcessManager.getRunningThreads() == 1) {
                DataAccessor.setConnection(DataAccessor.LOCAL_DEV);
                Logger.info("Set Data Target To Dev Schema: " + DataAccessor.LOCAL_DEV);
            } else {
                Logger.warn("Functionality Unavailable While Running Tests.");
            }
            keyDelayer = 0;
        }
        if (keyCodes.contains(KeyEvent.VK_2) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            if (UtilityProcessManager.getRunningThreads() == 1) {
                DataAccessor.setConnection(DataAccessor.LOCAL_TEST);
                Logger.info("Set Data Target To Test Schema: " + DataAccessor.LOCAL_TEST);
            } else {
                Logger.warn("Functionality Unavailable While Running Tests.");
            }
            keyDelayer = 0;
        }
        if (keyCodes.contains(KeyEvent.VK_3) && keyDelayer >= KEY_TIMER_THRESHOLD) {
            if (UtilityProcessManager.getRunningThreads() == 1) {
                DataAccessor.setConnection(DataAccessor.PROD);
                Logger.info("Set Data Target To PROD Schema: " + DataAccessor.PROD);
            } else {
                Logger.warn("Functionality Unavailable While Running Tests.");
            }
            keyDelayer = 0;
        }
    }

    public void refreshWindow() {
        fileList = output.list();
        if (fileList == null) {
            fileList = new String[0];
        }
        guiElements.get(3).getChild().setText("Files In Folder: " + fileList.length);
        guiElements.get(4).getChild().setText("Current Size: " + newRows);
        keyDelayer = 0;
        Window frame = SwingUtilities.getWindowAncestor(this);
        frame.setSize(frame.getWidth() - 1, frame.getHeight() - 1);
        frame.repaint();
        frame.setSize(frame.getWidth() + 1, frame.getHeight() + 1);
        frame.repaint();
    }

    private void performMouseUpdates(UIElement guiElement){
        //Check to see if the mouse is clicked inside of the bounding box of an element and set clicked.
        if(uiMouseAdapter.getXDown() > guiElement.getX() &&
                uiMouseAdapter.getYDown() > guiElement.getY() - uiMouseAdapter.getScrollAmount() &&
                uiMouseAdapter.getXDown() < guiElement.getX() + guiElement.getWidth() * aspectXRatio &&
                uiMouseAdapter.getYDown() < guiElement.getY() + guiElement.getHeight() * aspectYRatio - uiMouseAdapter.getScrollAmount()){
            guiElement.setClicked(true);
        } else if (!guiElement.isKeyPressed()) {
            guiElement.setClicked(false);
        }
    }

    private void detectWindowChanges() {
        //On window resize update element draw positions...
        if (currentHeight != getWidth() || currentHeight != getHeight()) {
            xLocBase = currentWidth / 2 - (int)(BASE_WIDTH * aspectXRatio) - (int)(BASE_WIDTH / 4 * aspectXRatio);
            yLocBase = currentHeight / 8;
            for (int i = 0; i < guiElements.size(); i++) {
                UIElement uiElement = guiElements.get(i);
                if (uiElement.getChild() != null) {
                    guiElements.get(i).setLocation(
                            xLocBase,
                            yLocBase + (int)(BASE_PADDING * i * aspectYRatio));
                } else {
                    guiElements.get(i).setLocation(
                            xLocBase + (int)(BASE_WIDTH * 1.5 * aspectXRatio),
                            yLocBase + (int)(BASE_PADDING * (i - guiElements.size() / 2) * aspectYRatio));
                }
            }
            currentWidth = getWidth();
            currentHeight = getHeight();
        }
    }

    public CopyOnWriteArrayList<UIElement> getGuiElements(){
        return guiElements;
    }

    public UIMouseAdapter getUiMouseAdapter() {
        return uiMouseAdapter;
    }

    public int getNewRows() {
        return newRows;
    }

    public void setNewRows(int newRows) {
        this.newRows = newRows;
    }

    private void greetings() {
        Logger.info("Running Application On " + GUIUtilCore.OS);
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

    public CopyOnWriteArrayList<Integer> getKeyCodes() {
        return keyCodes;
    }
}

