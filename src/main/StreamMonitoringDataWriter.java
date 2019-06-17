package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

/*
 * Writes cleaned data to a file.
 * Note that the format of the output file is similar to input, in that it is a
 * .tsv file with similar header ordering, but the output is not the same, in
 * that MultiValuedMaps are printed as is.
 */
public class StreamMonitoringDataWriter {
    // can provide a model and output file name
    public static void write(StreamMonitoringDataModel streamModel, String outputFileName) {
        printToFile(streamModel.getData(), outputFileName);
    }

    // or provide the input file path itself and output file name
    public static void write(String inputFilePath, String outputFileName) {
        List<StreamMonitoringData> streamData =
                StreamMonitoringDataParser.parseData(inputFilePath);
        printToFile(streamData, outputFileName);
    }

    // or provide the data list itself and output file name
    public static void write(List<StreamMonitoringData> streamData, String outputFileName) {
        printToFile(streamData, outputFileName);
    }

    // prints the data to the output file
    private static void printToFile(List<StreamMonitoringData> streamData, String outputFileName) {
        try {
            PrintStream output = new PrintStream(new File(outputFileName));
            // headers:
            output.print("Date\t");
            output.print("Site\t");
            output.print("Coordinates\t");
            output.print("Sheet\t");
            output.print("First Turbidities\t");
            output.print("First Turbidities Units\t");
            output.print("Second Turbidities\t");
            output.print("Second Turbidities Units\t");
            output.print("Air Temps (C)\t");
            output.print("Water Temps (C)\t");
            output.print("pHs\t");
            output.print("DOs (ppm)\t");
            output.print("Right Flows\t");
            output.print("Center Flows\t");
            output.print("Left Flows\t");
            output.print("Flow Units\t");
            output.print("Phosphate\t");
            output.print("Nitr\t");
            output.print("Conductivities (uS/cm)\t");
            output.print("Rain\t");
            output.print("Benthic Macros\t");
            output.print("Data Recorder(s)\t");
            output.print("Notes\t");
            output.println();
            for (StreamMonitoringData data : streamData) {
                output.println(data);
            }
            output.close();
        } catch (FileNotFoundException e) {
            System.out.println("Whoops! There was an issue with the file.");
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }
}
