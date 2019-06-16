package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.awt.*;
import java.util.*;
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

    private static final int FONT_SIZE = 14;
    private static final int BUFFER = 70;
    private static final int BUFF = BUFFER - 1;
    private static final int AXES_BUFFER = 10;
    private static final int TOTAL_BUFF = BUFF - AXES_BUFFER;
    private static final int NOTCH = 10;

    // constants for scatter plotting
    private static final int DOT_SIZE = 4;
    // scaling factors for data points; MULTIPLIERS[i] = multiplier for DATA_TYPES.get(i + 1)
    private static final int[] MULTIPLIERS = { 10, 100, 10, 10, 10, 10, 1 };
    private static final Color GREEN = new Color(20, 160, 30);

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
        String firstVal = String.format("%.2f", firstBucket);
        String lastVal = String.format("%.2f", (firstBucket + counts.size() * bucketSize));
        System.out.println("counts (from " + firstVal + " to " + lastVal + "): " + counts);

        int maxCount = 0;
        for (int count : counts) {
            maxCount = Math.max(maxCount, count);
        }
        DrawingPanel panel = new DrawingPanel(counts.size() * HIST_BAR_WIDTH + 2 * (BUFFER + AXES_BUFFER),
                maxCount * HIST_MULTIPLIER + 2 * (BUFFER + AXES_BUFFER));
        Graphics g = panel.getGraphics();
        int heightBuffer = panel.getHeight() - BUFFER;

        // draw bars
        g.setColor(Color.BLUE);
        for (int i = 0; i < counts.size(); i++) {
            g.drawRect(i * HIST_BAR_WIDTH + BUFFER + AXES_BUFFER, heightBuffer - (counts.get(i) * HIST_MULTIPLIER),
                    HIST_BAR_WIDTH, counts.get(i) * HIST_MULTIPLIER);
        }

        // line to show average
        g.setColor(GREEN);
        double avg = streamModel.getMean(streamModel.getData(dataType, site, startDate, endDate));
        g.drawLine((int)(((avg - firstBucket) / bucketSize) * HIST_BAR_WIDTH + BUFFER + AXES_BUFFER),
                maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER,
                (int)(((avg - firstBucket) / bucketSize) * HIST_BAR_WIDTH + BUFFER + AXES_BUFFER),
                heightBuffer - maxCount * HIST_MULTIPLIER - AXES_BUFFER);

        // axes
        g.setColor(Color.BLACK);
        g.drawLine(BUFF, BUFF, BUFF, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER);
        g.drawLine(BUFF, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER,
                BUFFER + counts.size() * HIST_BAR_WIDTH + 1 + 2 * AXES_BUFFER, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER);

        drawTitle(dataType, panel, g);

        // notches/labels
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));
        g.drawLine(BUFF - NOTCH / 2, BUFF + 2 * AXES_BUFFER, BUFF + NOTCH / 2, BUFF + 2 * AXES_BUFFER);
        g.drawString(maxCount + "", BUFF - NOTCH / 2 - 20, BUFF + 2 * AXES_BUFFER + 5);
        g.drawLine(BUFFER + AXES_BUFFER, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER - NOTCH / 2,
                BUFFER + AXES_BUFFER, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER + NOTCH / 2);
        g.drawString(firstVal,BUFFER + AXES_BUFFER - 10, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER + 10 + 10);
        g.drawLine(BUFFER + AXES_BUFFER + counts.size() * HIST_BAR_WIDTH, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER - NOTCH / 2,
                BUFFER + AXES_BUFFER + counts.size() * HIST_BAR_WIDTH, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER + NOTCH / 2);
        g.drawString(lastVal,BUFFER + AXES_BUFFER + counts.size() * HIST_BAR_WIDTH - 10, maxCount * HIST_MULTIPLIER + BUFFER + 2 * AXES_BUFFER + 10 + 10);

        // axis label
        String dataTypeStr = DATA_TYPES.get(dataType);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD + Font.ITALIC, (int)(FONT_SIZE * 1.2)));
        g.drawString(dataTypeStr, (panel.getWidth() - (dataTypeStr.length() * 10)) / 2, panel.getHeight() - BUFFER / 2);

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
                    System.out.println();
                    System.out.printf("[%-7.2f - %7.2f) : ", curr, curr + bucketSize);
                    if (counts != null) {
                        counts.add(0);
                    }
                    index++;
                } else {
                    while (Math.abs(datum - curr) >= bucketSize) {
                        System.out.println();
                        curr += bucketSize;
                        System.out.printf("[%-7.2f - %7.2f) : ", curr, curr + bucketSize);
                        if (counts != null) {
                            counts.add(0);
                        }
                        index++;
                    }
                }
            }
            System.out.print("*");
            if (counts != null) {
                counts.set(index, counts.get(index) + 1);
            }
        }
        System.out.println();
        return firstBucket;
    }

    public void simpleTextHistogram(int dataType, int site, Date startDate, Date endDate) {
        List<Double> sortedData = streamModel.getData(dataType, site, startDate, endDate);
        Double curr = null;
        for (Double datum : sortedData) {
            if (curr == null || Math.abs(datum - curr) > DELTA) {
                curr = datum;
                System.out.println();
                System.out.printf("%-7.2f: ", datum);
            }
            System.out.print("*");
        }
        System.out.println();
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

        drawScatterAxes(panel, g);
        drawTitle(dataType, panel, g);
        drawScatterLabelNotches(startDate, endDate, panel, g, max);
        drawDateAxisTitle(panel, g);

        plotDataPoints(dataType, site, startDate, endDate, streamData, startIndex, panel, g);
    }

    private void drawScatterAxes(DrawingPanel panel, Graphics g) {
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

    private void drawScatterLabelNotches(Date startDate, Date endDate, DrawingPanel panel, Graphics g, double max) {
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

        int multiplier = MULTIPLIERS[dataType - 1];
        List<int[]> averageCoords = new ArrayList<>();
        Date currDate = streamData.get(startIndex).getDate(); //startDate;
        List<Double> currVals = new ArrayList<>();
        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            Date date = data.getDate();
            if (date.compareTo(endDate) > 0) {
                break;
            }
            MultiValuedMap<Integer, Double> dataTypes = StreamMonitoringMain.getDataTypes(dataType, site, data);
            if (dataTypes != null) {
                Collection<Double> vals = dataTypes.values();
                vals.removeIf(Objects::isNull);

                if (!date.equals(currDate)) {
                    // plot currDate's points first
                    Collections.sort(currVals);
                    g.setColor(Color.BLUE);
                    double sum = 0.0;
                    int count = 0;
                    // dot size increases based on frequency
                    Double curr = null;
                    int dotIncrease = -1;
                    int x = differenceBetweenDates(startDate, currDate) - 1 + BUFFER;
                    for (Double value : currVals) {
                        if (curr == null || Math.abs(value - curr) > DELTA) {
                            curr = value;
                            dotIncrease = -1;
                        }
                        dotIncrease++;
                        int dotSize = DOT_SIZE + dotIncrease * 2;
                        g.fillOval(x - dotSize / 2,
                                panel.getHeight() - ((int) (double) (value * multiplier) + BUFFER)
                                        - dotSize / 2, dotSize, dotSize);
                        count++;
                        sum += value;
                    }
                    if (!currVals.isEmpty()) {
                        // plot average
                        double avg = sum / count;
                        g.setColor(GREEN);
                        int[] coords = {x, panel.getHeight() - ((int) (double) (avg * multiplier) + BUFFER)};
                        averageCoords.add(coords);
                        g.fillOval(coords[0] - DOT_SIZE / 2, coords[1] - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
                    }

                    // re-initialize for next date
                    currDate = date;
                    currVals = new ArrayList<>();
                }
                currVals.addAll(vals);
            }
        }

        // line to connect averages
        g.setColor(GREEN);
        for (int i = 0; i < averageCoords.size() - 1; i++) {
            int[] coord1 = averageCoords.get(i);
            int[] coord2 = averageCoords.get(i + 1);
            g.drawLine(coord1[0], coord1[1], coord2[0], coord2[1]);
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

    public static String dateToString(Date date) {
        return (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
    }
}
