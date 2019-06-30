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
            System.out.println();
            processOption(option, streamModel, visualizer, console);
        } while (!option.equalsIgnoreCase("q"));
    }

    // prints the menu of options
    public static void printMenu() {
        System.out.println("Menu:");
        System.out.println("\tw to write cleaned data to file");
        System.out.println("\ta to analyze data statistically");
        System.out.println("\tv to visualize the data");
        System.out.println("\tq to quit");
    }

    // processes option given by user
    public static void processOption(String option, StreamMonitoringDataModel streamModel,
                                     StreamMonitoringDataVisualizer visualizer, Scanner console) {
        if (option.equalsIgnoreCase("m")) { // menu
            printMenu();
        } else if (option.equalsIgnoreCase("w")) { // write
            writeData(streamModel, console);
        } else if (option.equalsIgnoreCase("a") || option.equalsIgnoreCase("v")) {
            // analyze or visualize
            System.out.println("Enter dates in format mm/dd/yy or mm/dd/yyyy.");
            Date start = promptForDate(streamModel, console, true);
            Date end = promptForDate(streamModel, console, false);
            int site = promptForSite(console);
            int dataType = promptForDataType(console);

            if (option.equalsIgnoreCase("a")) { // analyze
                System.out.println();
                List<Double> sortedData = streamModel.getData(dataType, site, start, end);
                try {
                    printStats(streamModel, sortedData);
                } catch (NullPointerException e) {
                    System.out.println("No statistics available.");
                }
            } else { // visualize
                boolean tryAgain = true;
                boolean firstTry = true;
                while (tryAgain) {
                    try {
                        if (firstTry) {
                            System.out.print("(S)catter plot OR (H)istogram: ");
                            firstTry = false;
                        } else {
                            System.out.print("Invalid choice, try again. Enter S or H: ");
                        }
                        String choice = console.nextLine().substring(0, 1);
                        if (choice.equalsIgnoreCase("H")) { // histogram
                            tryAgain = false;
                            System.out.println("Bucket Size means the range of values contained within one bar of the histogram.");
                            double bucketSize = promptForBucketSize(console);
                            System.out.println();
                            if (bucketSize == 0) {
                                System.out.println("Printing individual counts (no graphics window).");
                                visualizer.simpleTextHistogram(dataType, site, start, end);
                            } else {
                                System.out.println("Histogram:");
                                visualizer.drawHistogram(dataType, site, start, end, bucketSize);
                                System.out.println("Histogram generated! See new open window for graphics.");
                            }
                        } else if (choice.equalsIgnoreCase("S")) { // scatter plot
                            tryAgain = false;
                            System.out.println();
                            visualizer.drawScatterPlot(dataType, site, start, end);
                            System.out.println("Scatter Plot Generated! See new open window for graphics.");
                        }
                    } catch (NullPointerException e) {
                        System.out.println("No visualization available.");
                    }
                }
            }
        } else if (!option.equalsIgnoreCase("q")) { // invalid option
            System.out.println("Unknown option");
        }
    }

    // writes the data to an output file
    public static void writeData(StreamMonitoringDataModel streamModel, Scanner console) {
        System.out.print("Output file name (end with .tsv): ");
        String outputFileName = console.nextLine();
        StreamMonitoringDataWriter.write(streamModel, outputFileName);
        System.out.println("Done! Output file " + outputFileName + " created.");
    }

    // prompts for and returns the chosen date
    public static Date promptForDate(StreamMonitoringDataModel streamModel, Scanner console, boolean start) {
        String startOrEnd = start ? "Start" : "End";
        Date date = null;
        boolean firstTry = true;
        while (date == null) { // continue prompting until valid
            if (firstTry) {
                firstTry = false;
            } else {
                System.out.print("Invalid date, try again. ");
            }
            String def = start ? "first data date" : "today's date";
            System.out.print(startOrEnd + " date (leave blank if want " + def + "): ");
            try {
                String dateString = console.nextLine();
                if (dateString.isEmpty()) { // blank
                    // first data date or today's date
                    date = start ? streamModel.getData().get(0).getDate() : new Date();
                } else {
                    date = new Date(dateString);
                }
                System.out.println(startOrEnd + " date set to " +
                        StreamMonitoringDataVisualizer.dateToString(date));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return date;
    }

    // prompts for and returns the chosen site
    public static int promptForSite(Scanner console) {
        int site = -1;
        boolean firstTry = true;
        while (site < 0 || site > 2) { // continue prompting until valid
            if (firstTry) {
                System.out.print("Site (1 or 2; 0 if want both): ");
                firstTry = false;
            } else {
                System.out.print("Invalid site number, try again. Enter 1, 2, or 0: ");
            }
            try {
                site = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException ignored) {
            }
        }
        return site;
    }

    // prompts for and returns the chosen data type
    public static int promptForDataType(Scanner console) {
        int dataType = 0;
        boolean firstTry = true;
        while (dataType < 1 || dataType > 7) { // continue prompting until valid
            if (firstTry) {
                System.out.print("Data type " + DATA_TYPES + ": ");
                firstTry = false;
            } else {
                System.out.print("Invalid data type, try again. Enter a number from 1 to 7: ");
            }
            try {
                dataType = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException ignored) {
            }
        }
        System.out.println("Chosen data type: " + DATA_TYPES.get(dataType));
        return dataType;
    }

    // prompts for and returns the chosen bucket size
    public static double promptForBucketSize(Scanner console) {
        double bucketSize = -1;
        boolean firstTry = true;
        while (bucketSize < DELTA && bucketSize != 0) {
            if (firstTry) {
                firstTry = false;
            } else {
                System.out.print("Invalid bucket size; make sure it is a number above " + DELTA + ". ");
            }
            System.out.print("Bucket Size (0 for individual counts): ");
            try {
                bucketSize = Double.parseDouble(console.nextLine());
            } catch (NumberFormatException ignored) {
            }
        }
        return bucketSize;
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
