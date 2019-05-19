package main;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static main.StreamMonitoringDataModel.OUTLIERS_IQR;
import static main.StreamMonitoringDataModel.OUTLIER_SDS;

public class StreamMonitoringMain {
    public static final String STREAM_FILE_PATH = "src/main/data/coal_creek_data.tsv";

    public static void main(String[] args) throws FileNotFoundException {
        // Notes:
        // - double quotes character on row 305/6 (it was a typo anyway)
        // - might need any boxes with just notes and no numbers in data columns to be gone
        //   (like AQ:282 "Site 2 too high...")
        // - changed 1.2m to 10NTU at G:284 and G:285
        // Using BindByName (old):
        // - to preserve column names, need to change the turbidity column names
        //   to distinguish between the two
        // - ideal to put space after degree symbol in column S
        //      or I could figure out regexes
        // - also ideal to change AQ column header
        // Using BindByPosition (new):
        // - remove headers altogether

        StreamMonitoringDataModel streamModel = new StreamMonitoringDataModel(STREAM_FILE_PATH);

        //StreamMonitoringDataWriter.write(streamModel, "data.tsv");

        // options:
        //  start and end date
        //  site 1 or 2
        //  data type
        //  also should be able to check a particular date and entry and examine it
        // things to see (*data types):
        // - date
        // - site
        // - coordinates
        // - sheet #
        // * turbidity (basically fixed)
        // * air temp (should be good to go)
        // * water temp (should be good to go)
        // * pH (should be good)
        // * DO (should be good)
        // * flow (needs fixing)
        // - phosphate
        // - nitrate/nitrite
        // * conductivity (should be good)
        // - rain
        // - benthic macro
        // - data recorder
        // - notes

        Scanner console = new Scanner(System.in);
        String resp = "continue";
        while (!resp.equalsIgnoreCase("q")) {
            System.out.print("Start date: ");
            Date start = new Date(console.nextLine());
            System.out.print("End date: ");
            Date end = new Date(console.nextLine());
            System.out.println("Data type: " +
                    "(1) Turbidity (NTU), (2) Turbidity (m), " +
                    "(3) Air Temperature (°C), (4) Water Temperature (°C), " +
                    "(5) pH, (6) Dissolved Oxygen (ppm), (7) Conductivity (μS/cm)");
            int dataType = Integer.parseInt(console.nextLine());

            //System.out.println("Average: " + streamModel.getMean(dataType, start, end));

            System.out.println();
            System.out.println("Resulting Statistics");
            List<Double> sortedData = streamModel.getData(dataType, start, end);
            double mean = streamModel.getMean(sortedData);
            double stdDev = streamModel.getStdDev(sortedData, mean);
            List<Double> modes = streamModel.getMode(sortedData);
            double min = streamModel.getMin(sortedData);
            double[] quartiles = streamModel.getQuartiles(sortedData);
            double max = streamModel.getMax(sortedData);
            List<Double> outliersStdDev = streamModel.getOutliersStdDev(sortedData, mean, stdDev);
            List<Double> outliersIQR = streamModel.getOutliersIQR(sortedData, quartiles);
            System.out.println("Mean: " + mean);
            System.out.println("Standard Deviation: " + stdDev);
            System.out.println("Mode: " + modes);
            double[] boxplot = new double[] { min, quartiles[0], quartiles[1], quartiles[2], max };
            System.out.println("Min, Q1, Median, Q3, Max: " + Arrays.toString(boxplot));
            System.out.println("Outliers (based on " + OUTLIER_SDS + " std devs): " + outliersStdDev);
            System.out.println("Outliers (based on " + OUTLIERS_IQR + " IQRs): " + outliersIQR);
            System.out.println();

            System.out.print("Continue? (Type q to quit) ");
            resp = console.nextLine();
        }

        // Future:
        // - need to fix flow units stuff
        // - visualizing (histogram, scatter plot, line graph, bar graph, pie chart)
    }
}
