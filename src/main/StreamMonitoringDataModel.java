package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.*;

import static main.StreamMonitoringDataParser.DELTA;

public class StreamMonitoringDataModel {
    public static final int OUTLIER_SDS = 3;
    public static final double OUTLIERS_IQR = 1.5;

    private static final Map<Integer, String> DATA_TYPES_INTERNAL = new TreeMap<Integer, String>()
    {
        {
            put(1, "Turbidity (NTU)");
            put(2, "Turbidity (m)");
            put(3, "Air Temp (°C)");
            put(4, "Water Temp (°C)");
            put(5, "pH");
            put(6, "DO (ppm)");
            put(7, "Conductivity (μS/cm)");
        };
    };
    public static final Map<Integer, String> DATA_TYPES = Collections.unmodifiableMap(DATA_TYPES_INTERNAL);

    private List<StreamMonitoringData> streamData;

    public StreamMonitoringDataModel(String inputFilePath) {
        streamData = StreamMonitoringDataParser.parseData(inputFilePath);
        // sort
        streamData.sort(Comparator.comparing(StreamMonitoringData::getDate));
    }

    public List<StreamMonitoringData> getData() {
        return Collections.unmodifiableList(streamData);
    }

    public List<Double> getData(int dataType, int site, Date startDate, Date endDate) {
        if (startDate.compareTo(endDate) > 0) {
            System.out.println("Invalid date range: start date is after end date");
            return null;
        }
        int startIndex = leftBinarySearch(startDate);
        if (startIndex >= streamData.size()) {
            System.out.println("Invalid date range: no data available from after start date");
            return null;
        }
        /*int endIndex = rightBinarySearch(endDate);
        if (endIndex < 0) {
            return null;
        }*/

        List<Double> result = new ArrayList<Double>();
        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            if (data.getDate().compareTo(endDate) > 0) {
                break;
            }
            MultiValuedMap<Integer, Double> dataTypes = StreamMonitoringMain.getDataTypes(dataType, site, data);
            if (dataTypes != null) {
                for (Double value : dataTypes.values()) {
                    if (value != null) {
                        result.add(value);
                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    // might consider adding date?
    public double getMin(List<Double> sortedData) {
        return sortedData.get(0);
    }

    // might consider adding date?
    public double getMax(List<Double> sortedData) {
        return sortedData.get(sortedData.size() - 1);
    }

    public double[] getQuartiles(List<Double> sortedData) {
        int size = sortedData.size();
        double median = getMiddle(sortedData, 0, sortedData.size());
        double q1 = getMiddle(sortedData,0, size / 2);
        double q3 = getMiddle(sortedData, size / 2 + size % 2, size);
        return new double[] { q1, median, q3 };
    }

    // from inclusive to exclusive
    private double getMiddle(List<Double> sortedData, int fromIndex, int toIndex) {
        int size = toIndex - fromIndex;
        int mid = fromIndex + size / 2;
        if (size % 2 == 0) {
            return (sortedData.get(mid - 1) + sortedData.get(mid)) / 2;
        }
        return sortedData.get(mid);
    }

    // account for multiple modes!!!
    public List<Double> getMode(List<Double> sortedData) {
        //double most = Double.NaN;
        List<Double> mosts = new ArrayList<>();
        int mostCount = 1;
        int i = 0;
        while (i < sortedData.size()) {
            double curr = sortedData.get(i);
            int currCount = 1;
            while ((i + 1) < sortedData.size() &&
                   Math.abs(sortedData.get(i + 1) - curr) < DELTA) {
                currCount++;
                i++;
            }
            if (currCount >= mostCount && currCount > 1) {
                if (currCount > mostCount) {
                    mostCount = currCount;
                    mosts.clear();
                }
                mosts.add(curr);
            }
            i++;
        }
        mosts.add((double) mostCount);
        return mosts;
    }

    public double getMean(List<Double> data) {
        //Double sum = sortedData.stream().reduce(0.0, Double::sum);
        double sum = 0.0;
        for (double datum : data) {
            sum += datum;
        }
        return sum / data.size();
    }

    public double getStdDev(List<Double> data, double mean) {
        double sumSqDiff = 0.0;
        for (double datum : data) {
            double diff = datum - mean;
            sumSqDiff += (diff * diff);
        }
        return Math.sqrt(sumSqDiff / data.size());
    }

    public List<Double> getOutliersStdDev(List<Double> sortedData, double mean, double stdDev) {
        double low = mean - OUTLIER_SDS * stdDev;
        double high = mean + OUTLIER_SDS * stdDev;
        return getOutliers(sortedData, low, high);
    }

    public List<Double> getOutliersIQR(List<Double> sortedData, double[] quartiles) {
        double iqr = quartiles[2] - quartiles[0];
        double low = quartiles[0] - OUTLIERS_IQR * iqr;
        double high = quartiles[2] + OUTLIERS_IQR * iqr;
        return getOutliers(sortedData, low, high);
    }

    private List<Double> getOutliers(List<Double> sortedData, double low, double high) {
        List<Double> outliers = new ArrayList<>();
        int i = 0;
        while (sortedData.get(i) < low) { //&& i < sortedData.size()) {
            outliers.add(sortedData.get(i));
            i++;
        }
        i = sortedData.size() - 1;
        while (sortedData.get(i) > high) { //&& i >= 0) {
            outliers.add(sortedData.get(i));
            i--;
        }
        Collections.sort(outliers);
        return outliers;
    }

    // find index of leftmost instance of date
    // works when date not in list (first date after)
    //  if date too early, returns first
    //  if date too late, then invalid
    public int leftBinarySearch(Date date) {
        if (date.compareTo(streamData.get(0).getDate()) <= 0) {
            return 0;
        }
        if (date.compareTo(streamData.get(streamData.size() - 1).getDate()) > 0) {
            return streamData.size();
        }
        int left = -1;
        int mid;
        int right = streamData.size() - 1;
        while (right - left > 1) {
            mid = left + (right - left) / 2;
            if (streamData.get(mid).getDate().compareTo(date) >= 0) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return right;
    }

    // find index of rightmost instance of date
    // works when date not in list (first date before)
    //  if date too early, then invalid
    //  if date too late, then returns last
    private int rightBinarySearch(Date date) {
        if (date.compareTo(streamData.get(0).getDate()) <= 0) {
            return -1;
        }
        if (date.compareTo(streamData.get(streamData.size() - 1).getDate()) > 0) {
            return streamData.size() - 1;
        }
        int left = -1;
        int mid;
        int right = streamData.size() - 1;
        while (right - left > 1) {
            mid = left + (right - left) / 2;
            if (streamData.get(mid).getDate().compareTo(date) > 0) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return left;
    }
}
