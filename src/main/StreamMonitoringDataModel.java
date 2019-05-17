package main;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

    // find average of dateType between startDate and endDate from streamData
    public double calculateAverage(int dataType, Date startDate, Date endDate) {
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
}
