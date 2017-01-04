package odu.lane_detection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MotionReader {
    private String filePath;

    public MotionReader(String filePath) {
        this.filePath = filePath;
    }

    public TraveledPath process() throws IOException {
        TraveledPath path = new TraveledPath();

        Reader in = new FileReader(filePath);
        //Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader());
        for (CSVRecord record : parser) {
            double latitude = Double.parseDouble(record.get("latitude"));
            double longitude = Double.parseDouble(record.get("longitude"));

            VehicleStatus vehicleStatus = new VehicleStatus(null, longitude, latitude, null);
            path.add(vehicleStatus);
        }
        return path;
    }
}
