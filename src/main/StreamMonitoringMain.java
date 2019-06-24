package main;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static main.StreamMonitoringDataModel.*;
import static main.StreamMonitoringDataParser.DELTA;

/*
 * Contains the full user interface and options available to the user.
 */
public class StreamMonitoringMain {
    // constant for input file
    public static final String STREAM_FILE_PATH = "src/main/data/coal_creek_data_3-2019.tsv";

    // prints menu and prompts for option
    public static void main(String[] args) {
        System.out.println();
        Scanner console = new Scanner(System.in);
        System.out.println("Enter the stream data .tsv file name/path.\n" +
                "Specify the path from the root directory or from the current directory.");
        System.out.print("File name/path: ");
        String streamFilePath = console.nextLine();
        StreamMonitoringDataModel streamModel = new StreamMonitoringDataModel(streamFilePath);
        StreamMonitoringDataVisualizer visualizer = new StreamMonitoringDataVisualizer(streamModel);
        System.out.println();

        printMenu();
        String option;
        do {
            System.out.println();
            System.out.print("Enter an option (m to see the menu): ");
            option = console.nextLine();
            processOption(option, streamModel, visualizer, console);
        } while (!option.equalsIgnoreCase("q"));
    }

    // processes option given by user
    public static void processOption(String option, StreamMonitoringDataModel streamModel,
                                     StreamMonitoringDataVisualizer visualizer, Scanner console) {
        if (option.equalsIgnoreCase("m")) { // menu
            printMenu();
        } else if (option.equalsIgnoreCase("w")) { // write
            System.out.print("Output file name (end with .tsv): ");
            String outputFileName = console.nextLine();
            StreamMonitoringDataWriter.write(streamModel, outputFileName);
        } else if (option.equalsIgnoreCase("a") || option.equalsIgnoreCase("v")) {
            // analyze or visualize
            System.out.println("Enter dates in format mm/dd/yy or mm/dd/yyyy");
            System.out.print("Start date (leave blank if want first data date): ");
            Date start;
            try {
                start = new Date(console.nextLine());
            } catch (IllegalArgumentException e) {
                start = streamModel.getData().get(0).getDate();
                System.out.println("Start date set to " + StreamMonitoringDataVisualizer.dateToString(start));
            }
            System.out.print("End date (leave blank if want today's date): ");
            Date end;
            try {
                end = new Date(console.nextLine());
            } catch (IllegalArgumentException e) {
                end = new Date();
                System.out.println("End date set to " + StreamMonitoringDataVisualizer.dateToString(end));
            }
            System.out.print("Site (1 or 2; 0 if want both): ");
            int site = Integer.parseInt(console.nextLine());
            if (site != 1 && site != 2 && site != 0) {
                site = 0;
                System.out.println("Invalid site number. Interpreted as both.");
            }
            System.out.print("Data type " + DATA_TYPES + ": ");
            int dataType = Integer.parseInt(console.nextLine());

            if (dataType < 1 || dataType > 7) {
                System.out.print("Invalid data type; try again: ");
                dataType = Integer.parseInt(console.nextLine());
                if (dataType < 1 || dataType > 7) {
                    dataType = 8;
                    System.out.println("Invalid data type (again); now interpreted as flow (left), which is not cleaned; Too Bad!");
                }
            }
            System.out.println("Chosen data type: " + DATA_TYPES.get(dataType));

            if (option.equalsIgnoreCase("a")) { // analyze
                List<Double> sortedData = streamModel.getData(dataType, site, start, end);
                System.out.println();
                printStats(streamModel, sortedData);
            } else { // visualize
                System.out.print("(S)catter plot OR (H)istogram: ");
                String choice = console.nextLine().substring(0, 1);
                if (choice.equalsIgnoreCase("H")) { // histogram
                    System.out.println("Bucket Size means the range of values contained within one bar.");
                    System.out.print("Bucket Size (0 for individual counts): ");
                    double bucketSize = Double.parseDouble(console.nextLine());
                    if (bucketSize < 0) {
                        bucketSize = 1;
                        System.out.println("Invalid bucket size; changed to 1");
                    }
                    if (bucketSize < DELTA) {
                        if (bucketSize != 0) {
                            System.out.println("Bucket size too low; printing individual counts:");
                        }
                        visualizer.simpleTextHistogram(dataType, site, start, end);
                    } else {
                        visualizer.drawHistogram(dataType, site, start, end, bucketSize);
                    }
                    System.out.println("Histogram Generated!");
                } else { // scatter plot (default)
                    if (!choice.equalsIgnoreCase("S")) {
                        System.out.println("Invalid choice, but still...");
                    }
                    visualizer.drawScatterPlot(dataType, site, start, end);
                    System.out.println("Scatter Plot Generated!");
                }
            }
        } else if (!option.equalsIgnoreCase("q")) { // invalid option
            System.out.println("Unknown option");
        }
    }

    // prints the menu of options
    public static void printMenu() {
        System.out.println("Menu:");
        System.out.println("\tw to write cleaned data to file");
        System.out.println("\ta to analyze data statistically");
        System.out.println("\tv to visualize the data");
        System.out.println("\tq to quit");
    }

    // prints the statistics of the streamModel's sortedData
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
