package main;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class StreamMonitoringDataParser {
    // delta for testing equality with doubles
    public static final double DELTA = 0.0001;

    /*** TURBIDITY CONSTANTS: ***/

    // constant for 100
    private static final int CENT = 100;
    // minimum NTU value allowed (based on turbidity tube)
    private static final int MIN_NTU = 10;
    // maximum m value allowed (based on turbidity tube)
    private static final double MAX_M = 1.2;
    // coefficient when converting between m and NTU
    private static final double COEFF = 244.13;
    // exponent when converting between m and NTU
    private static final double EXP = 0.662;

    // offset for turbidity data, based on input file format
    private static final int TURB_OFFSET = 7;

    // constant for NTU units
    private static final String NTU = "NTU";
    // constant for meters units
    private static final String M = "m";

    public static List<StreamMonitoringData> parseData(String filename) {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filename));
            CsvToBean<StreamMonitoringData> csvToBean = new CsvToBeanBuilder<StreamMonitoringData>(reader)
                    .withSeparator('\t')
                    .withType(StreamMonitoringData.class)
                    .build();
            List<StreamMonitoringData> streamData = csvToBean.parse();

            /* DATA CLEANUP */
            // make sure there are units for everything
            for (StreamMonitoringData data : streamData) {
                fixTurbidityUnits(data); // basically standardized, issue with 60 JTU tho
                fixFlowUnits(data); // not quite working (need to standardize)
            }
            // removes null dates
            streamData.removeIf(o -> o.getDate() == null);

            return streamData;
        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
            return new ArrayList<>();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println(e.toString());
                    e.printStackTrace(System.err);
                }
            }
        }
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
        if (ntu < MIN_NTU + DELTA && ntu > MIN_NTU - DELTA) {
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
