package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

public class StreamMonitoringDataWriter {
    public static void write(String inputFilePath, String outputFileName)
            throws FileNotFoundException {
        List<StreamMonitoringData> streamData =
                StreamMonitoringDataParser.parseData(inputFilePath);
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
            output.print(data.getDate() + "\t");
            output.print(data.getSite() + "\t");
            output.print(data.getCoordinates() + "\t");
            output.print(data.getSheet() + "\t");
            output.print(data.getTurbiditiesFirst() + "\t");
            output.print(data.getTurbidityUnitsFirst() + "\t");
            output.print(data.getTurbiditiesSecond() + "\t");
            output.print(data.getTurbidityUnitsSecond() + "\t");
            output.print(data.getAirTemps() + "\t");
            output.print(data.getWaterTemps() + "\t");
            output.print(data.getPHs() + "\t");
            output.print(data.getOxygens() + "\t");
            output.print(data.getFlowRights() + "\t");
            output.print(data.getFlowCenters() + "\t");
            output.print(data.getFlowLefts() + "\t");
            output.print(data.getFlowUnits() + "\t");
            output.print(data.getPhosphate() + "\t");
            output.print(data.getNitr() + "\t");
            output.print(data.getConductivities() + "\t");
            output.print(data.getRain() + "\t");
            output.print(data.getBenthicMacros() + "\t");
            output.print(data.getDataRecorder() + "\t");
            output.print(data.getNotes() + "\t");
            output.println();
        }
        output.close();
    }
}
