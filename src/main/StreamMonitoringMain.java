package main;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static main.StreamMonitoringDataModel.*;

public class StreamMonitoringMain {
    public static final String STREAM_FILE_PATH = "src/main/data/coal_creek_data.tsv";

    public static void main(String[] args) {
        StreamMonitoringDataModel streamModel = new StreamMonitoringDataModel(STREAM_FILE_PATH);

        printMenu();
        Scanner console = new Scanner(System.in);
        String option;
        do {
            System.out.println();
            System.out.print("Enter an option (m to see the menu): ");
            option = console.nextLine();
            processOption(option, streamModel, console);
        } while (!option.equalsIgnoreCase("q"));

        // Future:
        // - need to fix flow units stuff
        // - visualizing (histogram, scatter plot, line graph, bar graph, pie chart)
    }

    public static void processOption(String option, StreamMonitoringDataModel streamModel, Scanner console) {
        if (option.equalsIgnoreCase("m")) {
            printMenu();
        } else if (option.equalsIgnoreCase("w")) {
            System.out.print("Output file name: ");
            String outputFileName = console.nextLine();
            StreamMonitoringDataWriter.write(streamModel, outputFileName);
        } else if (option.equalsIgnoreCase("a") || option.equalsIgnoreCase("s")) {
            System.out.print("Start date: ");
            Date start = new Date(console.nextLine());
            System.out.print("End date: ");
            Date end = new Date(console.nextLine());
            System.out.print("Site (1 or 2; 0 if want both): ");
            int site = Integer.parseInt(console.nextLine());
            System.out.print("Data type (" + DATA_TYPES + "): ");
            int dataType = Integer.parseInt(console.nextLine());
            List<Double> sortedData = streamModel.getData(dataType, site, start, end);

            System.out.println();
            printStats(streamModel, sortedData);
        } else if (!option.equalsIgnoreCase("q")) {
            System.out.println("Unknown option");
        }
    }

    public static void printMenu() {
        System.out.println("Menu:");
        System.out.println("\tw to write cleaned data to file");
        System.out.println("\ta to analyze data statistically");
        //System.out.println("\tv to visualize the data");
        System.out.println("\tq to quit");
    }

    public static void printStats(StreamMonitoringDataModel streamModel, List<Double> sortedData) {
        System.out.println("Resulting Statistics");
        System.out.println("--------------------");

        double mean = streamModel.getMean(sortedData);
        double stdDev = streamModel.getStdDev(sortedData, mean);
        List<Double> modes = streamModel.getMode(sortedData);
        int modeFreq = (int) (double) modes.get(modes.size() - 1);
        modes.remove(modes.size() - 1);
        double min = streamModel.getMin(sortedData);
        double[] quartiles = streamModel.getQuartiles(sortedData);
        double max = streamModel.getMax(sortedData);
        List<Double> outliersStdDev = streamModel.getOutliersStdDev(sortedData, mean, stdDev);
        List<Double> outliersIQR = streamModel.getOutliersIQR(sortedData, quartiles);

        System.out.println("Mean: " + mean);
        System.out.println("Standard Deviation: " + stdDev);
        if (modes.isEmpty()) {
            System.out.println("Mode (frequency 1): all data unique");
        } else {
            System.out.println("Mode (frequency " + modeFreq + "): " + modes);
        }
        double[] boxplot = new double[] { min, quartiles[0], quartiles[1], quartiles[2], max };
        System.out.println("Min, Q1, Median, Q3, Max: " + Arrays.toString(boxplot));
        System.out.println("Outliers (based on " + OUTLIER_SDS + " std devs): " + outliersStdDev);
        System.out.println("Outliers (based on " + OUTLIERS_IQR + " IQRs): " + outliersIQR);
    }
}
