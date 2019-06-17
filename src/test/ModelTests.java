package test;

import main.StreamMonitoringDataModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static main.StreamMonitoringDataParser.DELTA;
import static main.StreamMonitoringMain.STREAM_FILE_PATH;
import static org.junit.Assert.assertEquals;

/*
 * Testing file for various methods in StreamMonitoringDataModel.
 */
public class ModelTests {
    // converts arr to a List
    private List<Double> arrayToList(double[] arr) {
        List<Double> result = new ArrayList<>();
        for (double d : arr) {
            result.add(d);
        }
        return result;
    }

    private StreamMonitoringDataModel streamModel;

    @Before
    public void initializeModel() {
        streamModel = new StreamMonitoringDataModel(STREAM_FILE_PATH);
    }

    // nothing to test for getDataTypes, constructor, getData

    @Test
    public void testMin() {
        double[] arr1 = { 1, 2, 3, 4, 5, 6 };
        assertEquals(1, streamModel.getMin(arrayToList(arr1)), DELTA);
        double[] arr2 = { 1, 1, 2, 3, 4, 4 };
        assertEquals(1, streamModel.getMin(arrayToList(arr2)), DELTA);
    }

    @Test
    public void testMax() {
        double[] arr1 = { 1, 2, 3, 4, 5, 6 };
        assertEquals(6, streamModel.getMax(arrayToList(arr1)), DELTA);
        double[] arr2 = { 1, 1, 2, 3, 4, 4, 5, 5, 5, 5, 5, 5 };
        assertEquals(5, streamModel.getMax(arrayToList(arr2)), DELTA);
    }

    @Test
    public void testQuartiles() {
        double[] arr1 = { 2, 4, 4, 5, 6, 7, 8 };
        double[] quartiles1 = streamModel.getQuartiles(arrayToList(arr1));
        assertEquals(4, quartiles1[0], DELTA);
        assertEquals(5, quartiles1[1], DELTA);
        assertEquals(7, quartiles1[2], DELTA);

        double[] arr2 = { 1, 3, 3, 4, 5, 6, 6, 7, 8, 8 };
        double[] quartiles2 = streamModel.getQuartiles(arrayToList(arr2));
        assertEquals(3, quartiles2[0], DELTA);
        assertEquals(5.5, quartiles2[1], DELTA);
        assertEquals(7, quartiles2[2], DELTA);

        double[] arr3 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        double[] quartiles3 = streamModel.getQuartiles(arrayToList(arr3));
        assertEquals(3, quartiles3[0], DELTA);
        assertEquals(5.5, quartiles3[1], DELTA);
        assertEquals(8, quartiles3[2], DELTA);

        double[] arr4 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        double[] quartiles4 = streamModel.getQuartiles(arrayToList(arr4));
        assertEquals(2.5, quartiles4[0], DELTA);
        assertEquals(4.5, quartiles4[1], DELTA);
        assertEquals(6.5, quartiles4[2], DELTA);
    }

    @Test
    public void testMode() {
        double[] arr1 = { 1, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 7, 8, 9, 9, 9, 9 };
        assertEquals(arrayToList(new double[]{9, 4}), streamModel.getMode(arrayToList(arr1)));
        double[] arr2 = { 1, 2, 2, 2, 3, 3, 4, 4, 5, 6, 7, 7, 7, 7, 8, 9, 10, 10, 10, 10 };
        assertEquals(arrayToList(new double[]{7, 10, 4}), streamModel.getMode(arrayToList(arr2)));
        double[] arr3 = { 1, 2, 3, 4, 5, 6 };
        assertEquals(arrayToList(new double[]{1}), streamModel.getMode(arrayToList(arr3)));
    }

    @Test
    public void testMean() {
        double[] arr1 = { 1, 2, 3, 4, 5, 6 };
        assertEquals(3.5, streamModel.getMean(arrayToList(arr1)), DELTA);
        double[] arr2 = { 5, 2, 3, 6, 1, 4 };
        assertEquals(3.5, streamModel.getMean(arrayToList(arr2)), DELTA);
        double[] arr3 = { 1, 1, 3, 4, 5, 2, 6, 7, 8, 3 };
        assertEquals(4, streamModel.getMean(arrayToList(arr3)), DELTA);
    }

    @Test
    public void testStdDev() {
        double[] arr1 = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        List<Double> list1 = arrayToList(arr1);
        assertEquals(0, streamModel.getStdDev(list1, streamModel.getMean(list1)), DELTA);
        double[] arr2 = { -5, 1, 8, 7, 2 };
        List<Double> list2 = arrayToList(arr2);
        assertEquals(4.673328578, streamModel.getStdDev(list2, streamModel.getMean(list2)), DELTA);
        double[] arr3 = { 1, 5, 6, 4, 3, 7, 8, 9, 3, 4, 5, 4, 5, 4, 5, 3, 2, 3, 5 };
        List<Double> list3 = arrayToList(arr3);
        assertEquals(1.929505556, streamModel.getStdDev(list3, streamModel.getMean(list3)), DELTA);
    }

    @Test
    public void testOutliers() {
        double[] arr1 = { -9, -8, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 20, 25 };
        List<Double> list1 = arrayToList(arr1);
        double mean1 = streamModel.getMean(list1);
        assertEquals(arrayToList(new double[] { 25 }), streamModel.getOutliersStdDev(list1, mean1, streamModel.getStdDev(list1, mean1)));
        assertEquals(arrayToList(new double[] { -9, -8, 20, 25 }), streamModel.getOutliersIQR(list1, streamModel.getQuartiles(list1)));

        double[] arr2 = { 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4 };
        List<Double> list2 = arrayToList(arr2);
        double mean2 = streamModel.getMean(list2);
        assertEquals(new ArrayList<Double>(), streamModel.getOutliersStdDev(list2, mean2, streamModel.getStdDev(list2, mean2)));
        assertEquals(arrayToList(new double[] { 0, 0 }), streamModel.getOutliersIQR(list2, streamModel.getQuartiles(list2)));
    }
}
