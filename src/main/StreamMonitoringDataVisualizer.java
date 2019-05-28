package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.awt.*;
import java.util.*;
import java.util.List;

public class StreamMonitoringDataVisualizer {
    private static final int DAYS_IN_YEAR = 365;
    private static final int[] DAYS_PER_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    private static int getDaysInYear(int year) {
        year += 1900;
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ? 366 : 365;
    }
    private static int getDaysInMonth(int m, int y) {
        if (m == 3 || m == 5 || m == 8 || m == 10) {
            return 30;
        } else if (m != 1) {
            return 31;
        } else {
            return getDaysInYear(y) == 366 ? 29 : 28;
        }
    }

    private static final int BUFFER = 60;
    private static final int EXTRA_WIDTH_BUFFER = 10;

    private StreamMonitoringDataModel streamModel;

    public StreamMonitoringDataVisualizer(String inputFilePath) {
        streamModel = new StreamMonitoringDataModel(inputFilePath);
    }

    public void drawAirTempGraph(int site, Date startDate, Date endDate) {
        List<StreamMonitoringData> streamData = streamModel.getData();
        if (startDate.compareTo(endDate) > 0) {
            return;
        }
        int startIndex = streamModel.leftBinarySearch(startDate);
        if (startIndex >= streamData.size()) {
            return;
        }

        int diff = differenceBetweenDates(startDate, endDate);
        double max = streamModel.getMax(streamModel.getData(3, site, startDate, endDate));
        DrawingPanel panel = new DrawingPanel(diff + 2 * BUFFER, (int)max * 10 + 2 * BUFFER);
        Graphics g = panel.getGraphics();
        g.drawLine(BUFFER - 1 - EXTRA_WIDTH_BUFFER, BUFFER - 1 - EXTRA_WIDTH_BUFFER,
                BUFFER - 1 - EXTRA_WIDTH_BUFFER, panel.getHeight() - (BUFFER - 1));
        g.drawLine(BUFFER - 1 - EXTRA_WIDTH_BUFFER, panel.getHeight() - (BUFFER - 1),
                panel.getWidth() - (BUFFER - 1) + EXTRA_WIDTH_BUFFER, panel.getHeight() - (BUFFER - 1));
        // default font size of 10
        g.drawLine(BUFFER - 1 - EXTRA_WIDTH_BUFFER - 5,BUFFER - 1,
                BUFFER - 1 - EXTRA_WIDTH_BUFFER + 5, BUFFER - 1);
        g.drawString(String.format("%.1f", max), BUFFER - 1 - EXTRA_WIDTH_BUFFER - 35, BUFFER - 1 + 5);
        g.drawLine(BUFFER, panel.getHeight() - (BUFFER - 1) - 5,
                BUFFER, panel.getHeight() - (BUFFER - 1) + 5);
        g.drawString(dateToString(startDate), BUFFER - 1 - EXTRA_WIDTH_BUFFER, panel.getHeight() - (BUFFER - 1) + 10 + 10);
        g.drawLine(panel.getWidth() - BUFFER, panel.getHeight() - (BUFFER - 1) - 5,
                panel.getWidth() - BUFFER, panel.getHeight() - (BUFFER - 1) + 5);
        g.drawString(dateToString(endDate), panel.getWidth() - (BUFFER - 1) - 50, panel.getHeight() - (BUFFER - 1) + 10 + 10);

        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            Date date = data.getDate();
            if (data.getDate().compareTo(endDate) > 0) {
                break;
            }
            if (site == 1 || site == 2) {
                if (data.getSite() != site) {
                    continue;
                }
            }
            MultiValuedMap<Integer, Double> dataTypes = data.getAirTemps();
            for (Double value : dataTypes.values()) {
                if (value != null) {
                    g.fillOval(differenceBetweenDates(startDate, date) - 1 + BUFFER,
                            panel.getHeight() - ((int)(double)(value * 10) + BUFFER),
                            5, 5);
                }
            }
        }
    }

    public void drawAirTempGraph() {
        DrawingPanel panel = new DrawingPanel(); // 500x400
        Graphics g = panel.getGraphics();
        List<StreamMonitoringData> streamData = streamModel.getData();
        for (StreamMonitoringData datum : streamData) {
            if (datum.getDate().compareTo(new Date("1/1/18")) >= 0) {
                Date date = datum.getDate();
                int month = date.getMonth();
                int day = date.getDate();
                //System.out.println(month + " " + day + " " + date.getYear());
                int x = convertDateToInt(date);
                MultiValuedMap<Integer, Double> dataTypes = datum.getAirTemps();
                for (Double value : dataTypes.values()) {
                    if (value != null) {
                        g.fillOval(x, panel.getHeight() - (int)(double)(value * 10), 5, 5);
                    }
                }
            }
        }
    }

    // returns integer for day number in the year (1 to 366)
    private int convertDateToInt(Date date) {
        int x = 0;
        // days in months prior
        for (int i = 0; i < date.getMonth(); i++) {
            //x += DAYS_PER_MONTH[i];
            x += getDaysInMonth(i, date.getYear());
        }
        x += date.getDate();
        return x;
    }

    // for now only if different years
    private int differenceBetweenDates(Date start, Date end) {
        int startYear = start.getYear();
        int endYear = end.getYear();
        int startInt = convertDateToInt(start);
        int endInt = convertDateToInt(end);
        if (startYear == endYear) {
            return endInt - startInt + 1;
        }
        int result = getDaysInYear(startYear) - startInt;
        for (int year = startYear + 1; year < endYear; year++) {
            result += getDaysInYear(year);
        }
        result += endInt;
        return result + 1;
    }

    private String dateToString(Date date) {
        return (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
    }
}
