package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.*;

public class StreamMonitoringDataModel {
    private List<StreamMonitoringData> streamData;

    public StreamMonitoringDataModel(String inputFilePath) {
        streamData = StreamMonitoringDataParser.parseData(inputFilePath);
        // sort
        streamData.sort(Comparator.comparing(StreamMonitoringData::getDate));
    }

    public List<StreamMonitoringData> getData() {
        return Collections.unmodifiableList(streamData);
    }

    private List<Double> getData(int dataType, Date startDate, Date endDate) {
        if (startDate.compareTo(endDate) > 0) {
            return null;
        }
        int startIndex = leftBinarySearch(startDate);
        if (startIndex >= streamData.size()) {
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
                    result.add(value);
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

    // find average of dateType between startDate and endDate from streamData
    public double getMean(int dataType, Date startDate, Date endDate) {
        if (startDate.compareTo(endDate) > 0) {
            return Double.NaN;
        }
        int startIndex = leftBinarySearch(startDate);
        if (startIndex >= streamData.size()) {
            return Double.NaN;
        }
        double sum = 0.0;
        int num = 0;
        for (int i = startIndex; i < streamData.size(); i++) {
            StreamMonitoringData data = streamData.get(i);
            if (data.getDate().compareTo(endDate) > 0) {
                break;
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
                    sum += value;
                    num++;
                }
            }
        }
        return sum / num;
    }

    // find index of leftmost instance of date
    // works when date not in list (first date after)
    //  if date too early, returns first
    //  if date too late, then invalid
    private int leftBinarySearch(Date date) {
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
