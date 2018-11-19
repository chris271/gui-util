package com.gurpus.gui.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    public DateFormatter() {
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final SimpleDateFormat extFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
    private final SimpleDateFormat fileFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss-SSS");
    private final SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String standardFormat(Date date) {
        return dateFormat.format(date);
    }

    public String extFormat(Date date) {
        return extFormat.format(date);
    }

    public String sqlFormat(Date date) {
        return sqlFormat.format(date);
    }

    public String fileFormat(Date date) {
        return fileFormat.format(date);
    }

    public Date parseStandardFormat(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    public Date parseSqlFormat(String date) throws ParseException {
        return sqlFormat.parse(date);
    }

    public Date parseFileFormat(String date) throws ParseException {
        return fileFormat.parse(date);
    }

    public String otherToStandardFormat(String oldFormatString, SimpleDateFormat oldFormat) throws ParseException {
        return dateFormat.format(oldFormat.parse(oldFormatString));
    }

    public String otherToFileFormat(String oldFormatString, SimpleDateFormat oldFormat) throws ParseException {
        return fileFormat.format(oldFormat.parse(oldFormatString));
    }

}
