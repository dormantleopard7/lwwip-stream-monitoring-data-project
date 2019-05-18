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
