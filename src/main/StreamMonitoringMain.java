package main;

import org.apache.commons.collections4.MultiValuedMap;
import test.ConvertersTests;

import java.io.FileNotFoundException;
import java.util.*;

public class StreamMonitoringMain {
    private static final String STREAM_FILE_PATH = "src/main/data/coal_creek_data.tsv";

    private static final int CENT = 100;
    private static final int MIN_NTU = 10;
    private static final double MAX_M = 1.2;
    private static final double COEFF = 244.13;
    private static final double EXP = 0.662;

    private static final int TURB_OFFSET = 7;

    public static final String NTU = "NTU";
    public static final String M = "m";

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

        // initial parsing
        List<StreamMonitoringData> streamData =
                StreamMonitoringDataParser.parseData(STREAM_FILE_PATH);

        // make sure there are units for everything
        for (StreamMonitoringData data : streamData) {
            fixTurbidityUnits(data); // basically standardized, issue with 60 JTU tho
            fixFlowUnits(data); // not quite working (need to standardize)
        }

        // removes null dates
        streamData.removeIf(o -> o.getDate() == null);
        // sort
        //Collections.sort(streamData);
        streamData.sort(Comparator.comparing(StreamMonitoringData::getDate));

        // stats:
        //  min (Date), Q1, median, Q3, max (Date)
        //   note that these work best when the data is sorted first
        //  mean, variance/stdev, outliers
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
        while (!resp.equals("q")) {
            System.out.print("Start date: ");
            Date start = new Date(console.nextLine());
            System.out.print("End date: ");
            Date end = new Date(console.nextLine());
            System.out.println("Data type: " +
                    "(1) Turbidity (NTU), (2) Turbidity (m), " +
                    "(3) Air Temperature (°C), (4) Water Temperature (°C), " +
                    "(5) pH, (6) Dissolved Oxygen (ppm), (7) Conductivity (μS/cm)");
            int dataType = Integer.parseInt(console.nextLine());
            System.out.println("Average: " + calculateAverage(dataType, streamData, start, end));
            System.out.print("Continue? (Type q to quit) ");
            resp = console.nextLine();
        }

        /*PrintStream output = new PrintStream(new File("data.txt"));
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
        output.close();*/

        // Future:
        // - need to fix flow units stuff
        // - visualizing (histogram, scatter plot, line graph, bar graph, pie chart)
    }

    // find average of dateType between startDate and endDate from streamData
    private static double calculateAverage(int dataType, List<StreamMonitoringData> streamData,
                                           Date startDate, Date endDate) {
        if (startDate.compareTo(endDate) > 0) {
            return Double.NaN;
        }
        int startIndex = leftBinarySearch(startDate, streamData);
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
    private static int leftBinarySearch(Date date, List<StreamMonitoringData> streamData) {
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

    // standardize turbidity units
    // first is in NTU (issue with 60 JTU tho)
    // second is in m
    private static void fixTurbidityUnits(StreamMonitoringData data) {
        // turbidity units:
        // issue with different units on same line
        //  m and NTU on 2/2/2018 (fixed)
        // JTU: 60 to 21
        // NTU: 20 to 3
        // cm: 90 and above
        // m: 1.2 and below

        MultiValuedMap<Integer, Double> tOnes = data.getTurbiditiesFirst();
        MultiValuedMap<Integer, Double> tTwos = data.getTurbiditiesSecond();

        Map<Integer, Double> oneReplacements = new HashMap<>();
        for (Integer pos: tOnes.keySet()) {
            Iterator<Double> iter = tOnes.get(pos).iterator();
            // could also use toArray()
            Double val = iter.next();
            if (val != null) {
                if (val > 65) {
                    val = mToNTU(val / CENT);
                    oneReplacements.put(pos, val);
                } else if (val < 2) {
                    val = mToNTU(val);
                    oneReplacements.put(pos, val);
                }
                if (tTwos.get(pos + TURB_OFFSET).iterator().next() == null) {
                    tTwos.remove(pos + TURB_OFFSET);
                    double convertToM = ntuToM(val);
                    tTwos.put(pos + TURB_OFFSET, convertToM);
                    data.setTurbidityUnitsSecond(M);
                }
                data.setTurbidityUnitsFirst(NTU);
            }
        }
        for (Integer pos : oneReplacements.keySet()) {
            tOnes.remove(pos);
            tOnes.put(pos, oneReplacements.get(pos));
        }

        // this one is always cm or m
        Map<Integer, Double> twoReplacements = new HashMap<>();
        for (Integer pos: tTwos.keySet()) {
            Object[] vals = tTwos.get(pos).toArray();
            Double val = (Double) vals[0];
            if (val != null) {
                if (val > 65) {
                    twoReplacements.put(pos, val / CENT);
                } else if (val > 2) {
                    val = ntuToM(val);
                    twoReplacements.put(pos, val);
                }
                if (tOnes.get(pos - TURB_OFFSET).toArray()[0] == null) {
                    tOnes.remove(pos - TURB_OFFSET);
                    double convertToNTU = mToNTU(val);
                    tOnes.put(pos - TURB_OFFSET, convertToNTU);
                    data.setTurbidityUnitsFirst(NTU);
                }
                data.setTurbidityUnitsSecond(M);
            }
        }
        for (Integer pos : twoReplacements.keySet()) {
            tTwos.remove(pos);
            tTwos.put(pos, twoReplacements.get(pos));
        }
    }

    // might consider applying the mins and maxes to all turbidity data,
    // not just while converting

    // convert from m to NTU
    // NTU is never less than 10
    private static double mToNTU(double m) {
        double ntu = Math.pow((m * CENT) / COEFF, - 1 / EXP);
        return ntu < MIN_NTU ? MIN_NTU : ntu;
    }

    // convert from NTU to m
    // m is never more than 1.2
    private static double ntuToM(double ntu) {
        if (ntu < MIN_NTU + ConvertersTests.DELTA && ntu > MIN_NTU - ConvertersTests.DELTA) {
            return MAX_M;
        }
        double m = (COEFF * Math.pow(ntu, -EXP)) / CENT;
        return m > MAX_M ? MAX_M : m;
    }

    private static void fixFlowUnits(StreamMonitoringData data) {
        // flow units
        // issue with different units on same line
        //  starting 4/29/17 and goes on for a while
        //  solution would be to standardize but tough
        // m/s: 0.4 to 1.2
        // cm/s: 5 to 150
        // rpm: 170 to 2045; over 60000 and such should be invalid

        MultiValuedMap<Integer, Double> flowRs = data.getFlowRights();
        fixFlowUnits(data, flowRs);

        MultiValuedMap<Integer, Double> flowCs = data.getFlowCenters();
        fixFlowUnits(data, flowCs);

        MultiValuedMap<Integer, Double> flowLs = data.getFlowLefts();
        fixFlowUnits(data, flowLs);

        // currently sets units to flowLs' units
    }

    private static void fixFlowUnits(StreamMonitoringData data,
                                     MultiValuedMap<Integer, Double> flows) {
        for (Integer pos : flows.keySet()) {
            Iterator<Double> iter = flows.get(pos).iterator();
            // could also use toArray()
            Double val = iter.next();
            if (val != null) {
                if (val < 2) {
                    //data.setFlowUnits("m/s");
                    flows.removeMapping(pos, val);
                    flows.put(pos, val * 100);
                    data.setFlowUnits("cm/s");
                    break;
                } else if (val < 160) {
                    data.setFlowUnits("cm/s");
                    break;
                } else if (val < 5000) {
                    data.setFlowUnits("rpm");
                    break;
                } else {
                    // this is the case when the number is too high
                    // ideally set val to null
                }
            }
        }
    }
}
