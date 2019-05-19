package test;

import main.StreamMonitoringDataModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.StreamMonitoringMain.STREAM_FILE_PATH;

public class ModelTests {
    public static void main(String[] args) {
        ModelTests modelTests = new ModelTests();
        StreamMonitoringDataModel streamModel = new StreamMonitoringDataModel(STREAM_FILE_PATH);

        double[] arr1 = { 2, 4, 4, 5, 6, 7, 8 };
        double[] arr2 = { 1, 3, 3, 4, 5, 6, 6, 7, 8, 8 };
        double[] arr3 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        double[] arr4 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        System.out.println(Arrays.toString(streamModel.getQuartiles(modelTests.arrayToList(arr1))));
        System.out.println(Arrays.toString(streamModel.getQuartiles(modelTests.arrayToList(arr2))));
        System.out.println(Arrays.toString(streamModel.getQuartiles(modelTests.arrayToList(arr3))));
        System.out.println(Arrays.toString(streamModel.getQuartiles(modelTests.arrayToList(arr4))));

        double[] arr5 = { -9, -8, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 20, 25 };
        List<Double> list5 = modelTests.arrayToList(arr5);
        double mean = streamModel.getMean(list5);
        double stdev = streamModel.getStdDev(list5, mean);
        System.out.println(mean + " " + stdev);
        System.out.println(streamModel.getOutliersStdDev(list5, mean, stdev));
        double[] quartiles = streamModel.getQuartiles(list5);
        System.out.println(streamModel.getOutliersIQR(list5, quartiles));

        double[] arr6 = { 1, 2, 2, 2, 3, 3, 4, 4, 5, 6, 7, 7, 7, 7, 8, 9, 10, 10, 10, 10 };
        System.out.println("mode: " + streamModel.getMode(modelTests.arrayToList(arr6)));
    }

    private List<Double> arrayToList(double[] arr) {
        List<Double> result = new ArrayList<>();
        for (double d : arr) {
            result.add(d);
        }
        return result;
    }

    @Test
    public void testQuartiles() {
        StreamMonitoringDataModel streamModel = new StreamMonitoringDataModel(STREAM_FILE_PATH);
    }
}
