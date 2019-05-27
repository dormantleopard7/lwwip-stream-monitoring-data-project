package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.awt.*;
import java.util.*;
import java.util.List;

public class StreamMonitoringDataVisualizer {
    private static final int DAYS_IN_YEAR = 365;
    private static final int[] DAYS_PER_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    private static int getDaysInYear(int year) {
        return (year + 1900) % 4 == 0 ? 366 : 365;
    }
    private static int getDaysInMonth(int m, int y) {
        if (m == 3 || m == 5 || m == 8 || m == 10) {
            return 30;
        } else if (m != 2) {
            return 31;
        } else {
            return getDaysInYear(y) == 366 ? 29 : 28;
        }
    }

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
        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            if (data.getDate().compareTo(endDate) > 0) {
                break;
            }
            if (site == 1 || site == 2) {
                if (data.getSite() != site) {
                    continue;
                }
            }
            MultiValuedMap<Integer, Double> dataTypes = data.getAirTemps();
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
                int x = 0;
                // days in months prior
                for (int i = 0; i < month; i++) {
                    //x += DAYS_PER_MONTH[i];
                    x += getDaysInMonth(month, date.getYear());
                }
                x += day;
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
        int month = date.getMonth();
        int x = 0;
        // days in months prior
        for (int i = 0; i < month; i++) {
            //x += DAYS_PER_MONTH[i];
            x += getDaysInMonth(month, date.getYear());
        }
        x += date.getDate();
        return x;
    }

    // for now only if different years
    private int differenceBetweenDates(Date start, Date end) {
        int result = 0;
        int startYear = start.getYear();
        int endYear = end.getYear();
        if (startYear != endYear) {
            int startInt = convertDateToInt(start);
            int endInt = convertDateToInt(end);
            result += getDaysInYear(startYear) - startInt;
            for (int year = startYear + 1; year < endYear; year++) {
                result += getDaysInYear(year);
            }
            result += endInt;
        }
        return result;
    }
}
