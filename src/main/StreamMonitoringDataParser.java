package main;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StreamMonitoringDataParser {
    public static List<StreamMonitoringData> parseData(String filename) {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filename));
            CsvToBean<StreamMonitoringData> csvToBean = new CsvToBeanBuilder<StreamMonitoringData>(reader)
                    .withSeparator('\t')
                    .withType(StreamMonitoringData.class)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
            return new ArrayList<StreamMonitoringData>();
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
}
