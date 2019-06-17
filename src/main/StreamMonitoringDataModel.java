package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.*;

import static main.StreamMonitoringDataParser.DELTA;

/*
 * Provides the meat of data retrieval and analysis.
 * Basically includes all the statistics and provides access to raw data.
 */
public class StreamMonitoringDataModel {
    // number of SDs away from the mean that is considered outlier
    public static final int OUTLIER_SDS = 3;
    // number of IQRs away from the median that is considered outlier
    public static final double OUTLIERS_IQR = 1.5;

    // map from data type option number to the String representing the data type
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

    // returns a MultiValuedMap of the given dataType if the data entry is at site site (null otherwise)
    static MultiValuedMap<Integer, Double> getDataTypes(int dataType, int site, StreamMonitoringData data) {
        if (site == 1 || site == 2) {
            if (data.getSite() == null) { // no site recorded
                return null;
            }
            if (data.getSite() != site) { // wrong site
                return null;
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
            default: // sad default
                // flow -- not good at all
                dataTypes = data.getFlowLefts();
                break;
        }
        return dataTypes;
    }

    // the raw parsed data
    private List<StreamMonitoringData> streamData;

    // parses the data on construction
    public StreamMonitoringDataModel(String inputFilePath) {
        streamData = StreamMonitoringDataParser.parseData(inputFilePath);
        // sort
        streamData.sort(Comparator.comparing(StreamMonitoringData::getDate));
    }

    // sorted parsed/cleaned stream data available for client
    public List<StreamMonitoringData> getData() {
        return Collections.unmodifiableList(streamData);
    }

    // returns a sorted list of dataTypes's data at site (1 or 2, 0 means both) from
    // startDate to endDate
    public List<Double> getData(int dataType, int site, Date startDate, Date endDate) {
        // start date after end date
        if (startDate.compareTo(endDate) > 0) {
            System.out.println("Invalid date range: start date is after end date");
            return null;
        }
        // find index of first entry
        int startIndex = leftBinarySearch(startDate);
        // start date too late
        if (startIndex >= streamData.size()) {
            System.out.println("Invalid date range: no data available from after start date");
            return null;
        }

        List<Double> result = new ArrayList<Double>();
        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            if (data.getDate().compareTo(endDate) > 0) {
                // we have reached the end
                break;
            }
            MultiValuedMap<Integer, Double> dataTypes = getDataTypes(dataType, site, data);
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

    // returns the minimum value of the given sorted list
    public double getMin(List<Double> sortedData) {
        return sortedData.get(0);
    }

    // returns the maximum value of the given sorted list
    public double getMax(List<Double> sortedData) {
        return sortedData.get(sortedData.size() - 1);
    }

    // returns an array of the form [q1, median, q3] with the first quartile,
    // median, and third quartile of the given sorted list
    public double[] getQuartiles(List<Double> sortedData) {
        int size = sortedData.size();
        double median = getMiddle(sortedData, 0, sortedData.size());
        double q1 = getMiddle(sortedData,0, size / 2);
        double q3 = getMiddle(sortedData, size / 2 + size % 2, size);
        return new double[] { q1, median, q3 };
    }

    // returns the middle value from fromIndex inclusive to toIndex exclusive
    private double getMiddle(List<Double> sortedData, int fromIndex, int toIndex) {
        int size = toIndex - fromIndex;
        int mid = fromIndex + size / 2;
        if (size % 2 == 0) {
            return (sortedData.get(mid - 1) + sortedData.get(mid)) / 2;
        }
        return sortedData.get(mid);
    }

    // returns a list of the modes of the given sorted list, with the last
    // element being the frequency count of the mode(s).
    // if all data are unique (every data point is a mode, with frequency 1),
    // then list contains one element, 1 (frequency count)
    public List<Double> getMode(List<Double> sortedData) {
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
                // new mode
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

    // returns the mean of the given list (does not have to be sorted)
    public double getMean(List<Double> data) {
        double sum = 0.0;
        for (double datum : data) {
            sum += datum;
        }
        return sum / data.size();
    }

    // returns the standard deviation of the given list with mean mean
    // mean must be previously calculated from the same list
    public double getStdDev(List<Double> data, double mean) {
        double sumSqDiff = 0.0;
        for (double datum : data) {
            double diff = datum - mean;
            sumSqDiff += (diff * diff);
        }
        return Math.sqrt(sumSqDiff / data.size());
    }

    // returns a list of outliers in the given sorted list based on the given
    // mean and standard deviation
    public List<Double> getOutliersStdDev(List<Double> sortedData, double mean, double stdDev) {
        double low = mean - OUTLIER_SDS * stdDev;
        double high = mean + OUTLIER_SDS * stdDev;
        return getOutliers(sortedData, low, high);
    }

    // returns a list of outliers in the given sorted list based on the given
    // quartiles (1.5 IQR from the median)
    public List<Double> getOutliersIQR(List<Double> sortedData, double[] quartiles) {
        double iqr = quartiles[2] - quartiles[0];
        double low = quartiles[0] - OUTLIERS_IQR * iqr;
        double high = quartiles[2] + OUTLIERS_IQR * iqr;
        return getOutliers(sortedData, low, high);
    }

    // returns a list of outliers in the given sorted list based on the given
    // low and high boundaries (which are both excluded from the outlier list)
    private List<Double> getOutliers(List<Double> sortedData, double low, double high) {
        List<Double> outliers = new ArrayList<>();
        int i = 0;
        // low end
        while (sortedData.get(i) < low) {
            outliers.add(sortedData.get(i));
            i++;
        }
        // high end
        i = sortedData.size() - 1;
        while (sortedData.get(i) > high) {
            outliers.add(sortedData.get(i));
            i--;
        }
        Collections.sort(outliers);
        return outliers;
    }

    // find index of leftmost instance of date in streamData list
    // works when date not in list (first date after)
    //  if date too early, returns first
    //  if date too late, then invalid (returns size of streamData list)
    public int leftBinarySearch(Date date) {
        // early
        if (date.compareTo(streamData.get(0).getDate()) <= 0) {
            return 0;
        }
        // too late
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

    // find index of rightmost instance of date in streamData list
    // works when date not in list (first date before)
    //  if date too early, then invalid (returns -1)
    //  if date too late, then returns last
    private int rightBinarySearch(Date date) {
        // too early
        if (date.compareTo(streamData.get(0).getDate()) <= 0) {
            return -1;
        }
        // late
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
