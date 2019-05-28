package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.awt.*;
import java.util.*;
import java.util.List;

public class StreamMonitoringDataVisualizer {
    //private static final int DAYS_IN_YEAR = 365;
    //private static final int[] DAYS_PER_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    public static int getDaysInYear(int year) {
        year += 1900;
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ? 366 : 365;
    }
    public static int getDaysInMonth(int m, int y) {
        if (m == 3 || m == 5 || m == 8 || m == 10) {
            return 30;
        } else if (m != 1) {
            return 31;
        } else {
            return getDaysInYear(y) == 366 ? 29 : 28;
        }
    }

    private static final int DOT_SIZE = 5;
    private static final int BUFFER = 60;
    private static final int BUFF = BUFFER - 1;
    private static final int EXTRA_WIDTH_BUFFER = 10;
    private static final int TOTAL_BUFF = BUFF - EXTRA_WIDTH_BUFFER;
    private static final int NOTCH = 10;

    private static final int[] MULTIPLIERS = { 10, 100, 10, 10, 10, 10, 1 };

    private StreamMonitoringDataModel streamModel;

    public StreamMonitoringDataVisualizer(StreamMonitoringDataModel streamModel) {
        this.streamModel = streamModel;
    }

    public StreamMonitoringDataVisualizer(String inputFilePath) {
        this(new StreamMonitoringDataModel(inputFilePath));
    }

    public void drawLineGraph(int dataType, int site, Date startDate, Date endDate) {
        List<StreamMonitoringData> streamData = streamModel.getData();
        if (startDate.compareTo(endDate) > 0) {
            return;
        }
        int startIndex = streamModel.leftBinarySearch(startDate);
        if (startIndex >= streamData.size()) {
            return;
        }

        int diff = differenceBetweenDates(startDate, endDate);
        double max = streamModel.getMax(streamModel.getData(dataType, site, startDate, endDate));
        DrawingPanel panel = new DrawingPanel(diff + 2 * BUFFER,
                (int)(max * MULTIPLIERS[dataType - 1]) + 2 * BUFFER);
        Graphics g = panel.getGraphics();

        int heightBuff = panel.getHeight() - BUFF;
        g.drawLine(TOTAL_BUFF, TOTAL_BUFF, TOTAL_BUFF, heightBuff);
        g.drawLine(TOTAL_BUFF, heightBuff, panel.getWidth() - TOTAL_BUFF, heightBuff);

        // default font size of 10
        g.drawLine(TOTAL_BUFF - NOTCH / 2, BUFF, TOTAL_BUFF + NOTCH / 2, BUFF);
        g.drawString(String.format("%.1f", max), TOTAL_BUFF - 35, BUFF + 5);
        g.drawLine(BUFFER, heightBuff - NOTCH / 2, BUFFER, heightBuff + NOTCH / 2);
        g.drawString(dateToString(startDate), TOTAL_BUFF, heightBuff + 10 + 10);
        g.drawLine(panel.getWidth() - BUFFER, heightBuff - NOTCH / 2,
                panel.getWidth() - BUFFER, heightBuff + NOTCH / 2);
        g.drawString(dateToString(endDate), panel.getWidth() - BUFF - 50, heightBuff + 10 + 10);

        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            Date date = data.getDate();
            if (date.compareTo(endDate) > 0) {
                break;
            }
            if (site == 1 || site == 2) {
                if (data.getSite() != site) {
                    continue;
                }
            }
            MultiValuedMap<Integer, Double> dataTypes;
            switch (dataType) {
                case 1:
                    dataTypes = data.getTurbiditiesFirst();
                    break;
                case 2:
                    dataTypes = data.getTurbiditiesSecond();
                    break;
                case 3:
                    dataTypes = data.getAirTemps();
                    break;
                case 4:
                    dataTypes = data.getWaterTemps();
                    break;
                case 5:
                    dataTypes = data.getPHs();
                    break;
                case 6:
                    dataTypes = data.getOxygens();
                    break;
                case 7:
                    dataTypes = data.getConductivities();
                    break;
                default:
                    // flow -- not good yet
                    dataTypes = data.getFlowLefts();
                    break;
            }
            // could use .values() instead
            for (Double value : dataTypes.values()) {
                if (value != null) {
                    g.fillOval(differenceBetweenDates(startDate, date) - 1 + BUFFER,
                            panel.getHeight() - ((int)(double)(value * MULTIPLIERS[dataType - 1]) + BUFFER),
                            DOT_SIZE, DOT_SIZE);
                }
            }
        }
    }

    public void drawAirTempLineGraph(int site, Date startDate, Date endDate) {
        List<StreamMonitoringData> streamData = streamModel.getData();
        if (startDate.compareTo(endDate) > 0) {
            return;
        }
        int startIndex = streamModel.leftBinarySearch(startDate);
        if (startIndex >= streamData.size()) {
            return;
        }

        int multiplier = 10;
        int diff = differenceBetweenDates(startDate, endDate);
        double max = streamModel.getMax(streamModel.getData(3, site, startDate, endDate));
        DrawingPanel panel = new DrawingPanel(diff + 2 * BUFFER, (int)(max * multiplier) + 2 * BUFFER);
        Graphics g = panel.getGraphics();

        int heightBuff = panel.getHeight() - BUFF;
        g.drawLine(TOTAL_BUFF, TOTAL_BUFF, TOTAL_BUFF, heightBuff);
        g.drawLine(TOTAL_BUFF, heightBuff, panel.getWidth() - TOTAL_BUFF, heightBuff);

        // default font size of 10
        g.drawLine(TOTAL_BUFF - NOTCH / 2, BUFF, TOTAL_BUFF + NOTCH / 2, BUFF);
        g.drawString(String.format("%.1f", max), TOTAL_BUFF - 35, BUFF + 5);
        g.drawLine(BUFFER, heightBuff - NOTCH / 2, BUFFER, heightBuff + NOTCH / 2);
        g.drawString(dateToString(startDate), TOTAL_BUFF, heightBuff + 10 + 10);
        g.drawLine(panel.getWidth() - BUFFER, heightBuff - NOTCH / 2,
                panel.getWidth() - BUFFER, heightBuff + NOTCH / 2);
        g.drawString(dateToString(endDate), panel.getWidth() - BUFF - 50, heightBuff + 10 + 10);

        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            Date date = data.getDate();
            if (date.compareTo(endDate) > 0) {
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
                            panel.getHeight() - ((int)(double)(value * multiplier) + BUFFER),
                            DOT_SIZE, DOT_SIZE);
                }
            }
        }
    }

    // for now only if different years
    private int differenceBetweenDates(Date start, Date end) {
        int startYear = start.getYear();
        int endYear = end.getYear();
        int startInt = dateToInt(start);
        int endInt = dateToInt(end);
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

    // returns integer for day number in the year (1 to 366)
    private int dateToInt(Date date) {
        int x = 0;
        // days in months prior
        for (int i = 0; i < date.getMonth(); i++) {
            //x += DAYS_PER_MONTH[i];
            x += getDaysInMonth(i, date.getYear());
        }
        x += date.getDate();
        return x;
    }

    private String dateToString(Date date) {
        return (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
    }
}
