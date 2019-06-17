package main;

/*
 * Represents a Coordinate location on the Earth.
 * Meant to be used for the 'Coordinates' column.
 */
public class Coordinates {
    // latitude
    private int latDeg;
    private int latMin;
    private double latSec;
    private char latDir;

    // longitude
    private int longDeg;
    private int longMin;
    private double longSec;
    private char longDir;

    // constructor based on input String
    // format: [lat]deg°min'sec"dir [long]deg°min'sec"dir
    // site 1: 47°33'07.16"N 122°09'54.26"W
    // site 2: 47°33'04.16"N 122°09'51.67"W
    public Coordinates(String coordinates) {
        String[] latLong = coordinates.split(" ");

        String latitude = latLong[0];
        int laDeg = latitude.indexOf("°");
        int laMin = latitude.indexOf("'");
        int laSec = latitude.indexOf("\"");
        latDeg = Integer.parseInt(latitude.substring(0, laDeg));
        latMin = Integer.parseInt(latitude.substring(laDeg + 1, laMin));
        latSec = Double.parseDouble(latitude.substring(laMin + 1, laSec));
        latDir = latitude.charAt(latitude.length() - 1);

        String longitude = latLong[1];
        int loDeg = longitude.indexOf("°");
        int loMin = longitude.indexOf("'");
        int loSec = longitude.indexOf("\"");
        longDeg = Integer.parseInt(longitude.substring(0, loDeg));
        longMin = Integer.parseInt(longitude.substring(loDeg + 1, loMin));
        longSec = Double.parseDouble(longitude.substring(loMin + 1, loSec));
        longDir = longitude.charAt(longitude.length() - 1);
    }

    public int getLatDeg() {
        return latDeg;
    }

    public int getLatMin() {
        return latMin;
    }

    public double getLatSec() {
        return latSec;
    }

    public char getLatDir() {
        return latDir;
    }

    public int getLongDeg() {
        return longDeg;
    }

    public int getLongMin() {
        return longMin;
    }

    public double getLongSec() {
        return longSec;
    }

    public char getLongDir() {
        return longDir;
    }

    @Override
    public String toString() {
        return latDeg + "°" + latMin + "'" + latSec + "\"" + latDir + " " +
               longDeg + "°" + longMin + "'" + longSec + "\"" + longDir;
    }
}
