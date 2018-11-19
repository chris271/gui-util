package com.gurpus.gui.util;

public class Logger {

    public static final int FINE = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int SEVERE = 3;
    private static int CURRENT_LEVEL = FINE;

    public static void info(String in) {
        if (CURRENT_LEVEL <= INFO) {
            System.out.println("INFO - " + in);
        }
    }

    public static void warn(String in) {
        if (CURRENT_LEVEL <= WARN) {
            System.out.println("WARN - " + in);
        }
    }

    public static void severe(String in) {
        if (CURRENT_LEVEL <= SEVERE) {
            System.out.println("SEVERE - " + in);
        }
    }

    public static void fine(String in) {
        if (CURRENT_LEVEL <= FINE) {
            System.out.println("FINE - " + in);
        }
    }

    public static void info(long in) {
        if (CURRENT_LEVEL <= INFO) {
            System.out.println("INFO - " + in);
        }
    }

    public static void warn(long in) {
        if (CURRENT_LEVEL <= WARN) {
            System.out.println("WARN - " + in);
        }
    }

    public static void severe(long in) {
        if (CURRENT_LEVEL <= SEVERE) {
            System.out.println("SEVERE - " + in);
        }
    }

    public static void fine(long in) {
        if (CURRENT_LEVEL <= FINE) {
            System.out.println("FINE - " + in);
        }
    }

    public static void info(double in) {
        if (CURRENT_LEVEL <= INFO) {
            System.out.println("INFO - " + in);
        }
    }

    public static void warn(double in) {
        if (CURRENT_LEVEL <= WARN) {
            System.out.println("WARN - " + in);
        }
    }

    public static void severe(double in) {
        if (CURRENT_LEVEL <= SEVERE) {
            System.out.println("SEVERE - " + in);
        }
    }

    public static void fine(double in) {
        if (CURRENT_LEVEL <= FINE) {
            System.out.println("FINE - " + in);
        }
    }

    public static void info(Object in) {
        if (CURRENT_LEVEL <= INFO) {
            System.out.println("INFO - " + in);
        }
    }

    public static void warn(Object in) {
        if (CURRENT_LEVEL <= WARN) {
            System.out.println("WARN - " + in);
        }
    }

    public static void severe(Object in) {
        if (CURRENT_LEVEL <= SEVERE) {
            System.out.println("SEVERE - " + in);
        }
    }

    public static void fine(Object in) {
        if (CURRENT_LEVEL <= FINE) {
            System.out.println("FINE - " + in);
        }
    }

    public static void setLoggerLevel(int level) {
        CURRENT_LEVEL = level;
    }

    public static int getLoggerLevel() {
        return CURRENT_LEVEL;
    }

}
