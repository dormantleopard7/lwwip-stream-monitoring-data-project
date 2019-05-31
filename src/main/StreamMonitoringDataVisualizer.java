package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static main.StreamMonitoringDataModel.DATA_TYPES;
import static main.StreamMonitoringDataParser.DELTA;

public class StreamMonitoringDataVisualizer {
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

    // constants for scatter plotting
    private static final int DOT_SIZE = 6;
    private static final int FONT_SIZE = 14;
    private static final int BUFFER = 70;
    private static final int BUFF = BUFFER - 1;
    private static final int AXES_BUFFER = 10;
    private static final int TOTAL_BUFF = BUFF - AXES_BUFFER;
    private static final int NOTCH = 10;
    // scaling factors for data points; MULTIPLIERS[i] = multiplier for DATA_TYPES.get(i + 1)
    private static final int[] MULTIPLIERS = { 10, 100, 10, 10, 10, 10, 1 };

    // constants for histogram
    private static final int HIST_BAR_WIDTH = 40;
    private static final int HIST_MULTIPLIER = 10;

    private StreamMonitoringDataModel streamModel;

    public StreamMonitoringDataVisualizer(StreamMonitoringDataModel streamModel) {
        this.streamModel = streamModel;
    }

    public StreamMonitoringDataVisualizer(String inputFilePath) {
        this(new StreamMonitoringDataModel(inputFilePath));
    }

    public void drawHistogram(int dataType, int site, Date startDate, Date endDate, double bucketSize) {
        List<Integer> counts = new ArrayList<>();
        double firstBucket = textHistogram(dataType, site, startDate, endDate, bucketSize, counts);
        System.out.println("counts (from " + firstBucket + "): " + counts);

        int maxCount = 0;
        for (int count : counts) {
            maxCount = Math.max(maxCount, count);
        }
        DrawingPanel panel = new DrawingPanel(counts.size() * HIST_BAR_WIDTH, maxCount * HIST_MULTIPLIER);
        Graphics g = panel.getGraphics();
        int heightBuffer = panel.getHeight() - BUFFER;
        for (int i = 0; i < counts.size(); i++) {
            //g.setColor(Color.BLACK);
            //g.fillRect(i * HIST_BAR_WIDTH, 0, HIST_BAR_WIDTH, counts.get(i) * HIST_MULTIPLIER);
            g.setColor(Color.BLUE);
            g.drawRect(i * HIST_BAR_WIDTH, panel.getHeight() - (counts.get(i) * HIST_MULTIPLIER),
                    HIST_BAR_WIDTH, panel.getHeight());
        }
    }

    public void textHistogram(int dataType, int site, Date startDate, Date endDate, double bucketSize) {
        textHistogram(dataType, site, startDate, endDate, bucketSize, null);
    }

    private double textHistogram(int dataType, int site, Date startDate, Date endDate, double bucketSize,
                               List<Integer> counts) {
        List<Double> sortedData = streamModel.getData(dataType, site, startDate, endDate);
        Double curr = null;
        double firstBucket = 0;
        int index = -1;
        for (Double datum : sortedData) {
            if (curr == null || Math.abs(datum - curr) >= bucketSize) {
                if (curr == null) {
                    curr = 0.;
                    while ((curr < datum - bucketSize) || ((Math.abs(datum - bucketSize - curr) < DELTA))) {
                        curr += bucketSize;
                    }
                    // curr = first multiple of bucketSize below datum
                    firstBucket = curr;
                } else {
                    curr += bucketSize;
                }
                System.out.println();
                System.out.printf("[%-7.2f - %7.2f) : ", curr, curr + bucketSize);
                if (counts != null) {
                    counts.add(0);
                }
                index++;
            }
            System.out.print("*");
            if (counts != null) {
                counts.set(index, counts.get(index) + 1);
            }
        }
        System.out.println();
        return firstBucket;
    }

    public void drawScatterPlot(int dataType, int site, Date startDate, Date endDate) {
        List<StreamMonitoringData> streamData = streamModel.getData();
        if (startDate.compareTo(endDate) > 0) {
            System.out.println("Invalid date range: start date is after end date");
        }
        int startIndex = streamModel.leftBinarySearch(startDate);
        if (startIndex >= streamData.size()) {
            System.out.println("Invalid date range: no data available from after start date");
        }

        int diff = differenceBetweenDates(startDate, endDate);
        double max = streamModel.getMax(streamModel.getData(dataType, site, startDate, endDate));
        DrawingPanel panel = new DrawingPanel(diff + 2 * BUFFER,
                (int)(max * MULTIPLIERS[dataType - 1]) + 2 * BUFFER);
        Graphics g = panel.getGraphics();

        drawAxes(panel, g);
        drawTitle(dataType, panel, g);
        drawLabelNotches(startDate, endDate, panel, g, max);
        drawDateAxisTitle(panel, g);

        plotDataPoints(dataType, site, startDate, endDate, streamData, startIndex, panel, g);
    }

    private void drawAxes(DrawingPanel panel, Graphics g) {
        assert g.equals(panel.getGraphics());

        int heightBuff = panel.getHeight() - BUFF;
        g.drawLine(TOTAL_BUFF, TOTAL_BUFF, TOTAL_BUFF, heightBuff);
        g.drawLine(TOTAL_BUFF, heightBuff, panel.getWidth() - TOTAL_BUFF, heightBuff);
        // draw again to increase weight
        g.drawLine(TOTAL_BUFF - 1, TOTAL_BUFF, TOTAL_BUFF - 1, heightBuff + 1);
        g.drawLine(TOTAL_BUFF - 1, heightBuff + 1, panel.getWidth() - TOTAL_BUFF, heightBuff + 1);
    }

    private void drawTitle(int dataType, DrawingPanel panel, Graphics g) {
        assert g.equals(panel.getGraphics());

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(FONT_SIZE * 1.4)));
        String titleStr = DATA_TYPES.get(dataType);
        int sides = (panel.getWidth() - (titleStr.length() * 10)) / 2;
        g.drawString(titleStr, sides, BUFFER / 2);
    }

    private void drawLabelNotches(Date startDate, Date endDate, DrawingPanel panel, Graphics g, double max) {
        assert g.equals(panel.getGraphics());

        int heightBuff = panel.getHeight() - BUFF;
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));

        g.drawLine(TOTAL_BUFF - NOTCH / 2, BUFF, TOTAL_BUFF + NOTCH / 2, BUFF);
        int dig = (int)max / 100;
        g.drawString(String.format("%.1f", max), TOTAL_BUFF - NOTCH / 2 - (dig == 0 ? 30 : 40), BUFF + 5);
        g.drawLine(TOTAL_BUFF - NOTCH, panel.getHeight() - BUFFER,
                TOTAL_BUFF, panel.getHeight() - BUFFER);
        g.drawString("0.0", TOTAL_BUFF - NOTCH / 2 - 30, panel.getHeight() - BUFFER + 5);

        g.drawLine(BUFFER, heightBuff - NOTCH / 2, BUFFER, heightBuff + NOTCH / 2);
        g.drawString(dateToString(startDate), TOTAL_BUFF, heightBuff + 10 + 10);
        g.drawLine(panel.getWidth() - BUFFER, heightBuff - NOTCH / 2,
                panel.getWidth() - BUFFER, heightBuff + NOTCH / 2);
        g.drawString(dateToString(endDate), panel.getWidth() - BUFF - 50, heightBuff + 10 + 10);
    }

    private void drawDateAxisTitle(DrawingPanel panel, Graphics g) {
        assert g.equals(panel.getGraphics());

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD + Font.ITALIC, (int)(FONT_SIZE * 1.2)));
        g.drawString("Date", (panel.getWidth() - (4 * 10)) / 2, panel.getHeight() - BUFFER / 2);
    }

    private void plotDataPoints(int dataType, int site, Date startDate, Date endDate,
                                List<StreamMonitoringData> streamData, int startIndex,
                                DrawingPanel panel, Graphics g) {
        assert g.equals(panel.getGraphics());

        g.setColor(Color.BLUE);
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
            int multiplier = MULTIPLIERS[dataType - 1];
            for (Double value : dataTypes.values()) {
                if (value != null) {
                    g.fillOval((differenceBetweenDates(startDate, date) - 1 + BUFFER) - DOT_SIZE / 2,
                            panel.getHeight() - ((int)(double)(value * multiplier) + BUFFER)
                                    - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
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
