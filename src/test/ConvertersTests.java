package test;

import com.opencsv.exceptions.CsvException;
import main.converters.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConvertersTests {
    public static final double DELTA = 0.0001;

    private Date date(int month, int date, int year) {
        return new Date(year - 1900, month - 1, date);
    }

    @Test
    public void testDate() {
        try {
            DateConverter converter = new DateConverter();
            assertNull(converter.convert(""));
            assertNull(converter.convert("No date"));
            assertEquals(date(5, 12, 2006), converter.convert("5/12/2006"));
            assertEquals(null, converter.convert("12/6?"));
            assertEquals(null, converter.convert("2/?/09"));
            assertEquals(date(7, 27, 2009), converter.convert("7/27/2009?"));
            assertEquals(date(6, 5, 2009), converter.convert("6/5/09?"));
            assertEquals(date(12, 13, 2014), converter.convert("*12/13/2014"));
            assertEquals(date(4, 29, 2017), converter.convert("4/29/17"));
        } catch(CsvException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testSiteAndSheet() {
        // covered by opencsv, no custom Converter
    }

    @Test
    public void testCoordinates() {
        // hard to check because custom Coordinates class
    }

    @Test
    public void testTurbidity() {
        try {
            DoubleConverter2 converter = new DoubleConverter2(); // should be TurbidityConverter
            assertNull(converter.convertToRead(""));
            assertEquals(60, (double) converter.convertToRead("60"), DELTA); // JTU
            assertEquals(55.2, (double) converter.convertToRead("55.2"), DELTA); // JTU
            assertEquals(60, (double) converter.convertToRead("60+"), DELTA); // NTU?
            assertEquals(3, (double) converter.convertToRead("<3"), DELTA); // NTU?
            assertNull(converter.convertToRead("unreadable"));
            assertEquals(10, (double) converter.convertToRead("<10"), DELTA); // NTU
            assertEquals(1.13, (double) converter.convertToRead("1.13"), DELTA); // m
            assertEquals(1.2, (double) converter.convertToRead(">1.2m"), DELTA); // m
            assertEquals(1.2, (double) converter.convertToRead("1.2m"), DELTA); // m
            assertEquals(1.2, (double) converter.convertToRead("1.2m<"), DELTA); // m
            assertEquals(10, (double) converter.convertToRead(">10 NTU"), DELTA); // NTU
            assertEquals(10, (double) converter.convertToRead("<10 NTU"), DELTA); // NTU
            assertEquals(10, (double) converter.convertToRead("10> NTU"), DELTA); // NTU
            assertEquals(1.20, (double) converter.convertToRead(">1.20"), DELTA); // m
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testTurbidityUnits() {
        try {
            UnitsTextConverter converter = new UnitsTextConverter(); // units converters maybe
            assertNull(converter.convert(""));
            assertEquals("JTU", converter.convert("JTU"));
            assertNull(converter.convert("?"));
            assertNull(converter.convert("(mean, units?)"));
            assertEquals("JTU", converter.convert("JTU/NTU"));
            assertEquals("sec", converter.convert("sec?"));
            assertEquals("NTU", converter.convert("NTU"));
            assertEquals("cm", converter.convert("cm"));
            assertEquals("m", converter.convert("m"));
            // also think about what happens when units are in the other column
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testAirTemperatureAndWater() {
        try {
            TemperatureConverter converter = new TemperatureConverter();
            assertNull(converter.convertToRead(""));
            assertEquals(33, (double) converter.convertToRead("33"), DELTA);
            assertEquals(13.5, (double) converter.convertToRead("13.5"), DELTA);
            assertEquals(-0.2, (double) converter.convertToRead("-0.2"), DELTA);
            assertNull(converter.convertToRead("No record"));
            assertEquals(converter.fahrenheitToCelsius(82.), (double) converter.convertToRead("82F"), DELTA);
            assertEquals(9, (double) converter.convertToRead("9?"), DELTA);
            assertNull(converter.convertToRead("Broken thermometer"));
            assertNull(converter.convertToRead("Not recorded"));
            assertEquals(4, (double) converter.convertToRead("4 therm./0.6 laser"), DELTA);
            assertEquals(7.8, (double) converter.convertToRead("7.8 -laser"), DELTA);
            // might want error checking for Fahrenheit converting
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testPH() {
        try {
            SimpleDoubleConverter converter = new SimpleDoubleConverter();
            assertNull(converter.convertToRead(""));
            assertNull(converter.convertToRead("Not recorded"));
            assertEquals(7, (double)converter.convertToRead("7"), DELTA);
            assertEquals(7.5, (double)converter.convertToRead("7.5"), DELTA);
            assertEquals(7.25, (double)converter.convertToRead("7.25"), DELTA);
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testDO() {
        try {
            DoubleConverter2 converter = new DoubleConverter2();
            assertNull(converter.convertToRead(""));
            assertNull(converter.convertToRead("Not recorded"));
            assertNull(converter.convertToRead("missing"));
            assertEquals(11, (double)converter.convertToRead("11"), DELTA);
            assertEquals(7.5, (double)converter.convertToRead("7.5"), DELTA);
            assertEquals(11.33, (double)converter.convertToRead("11.33"), DELTA);
            // need to use DoubleConverter for this one:
            assertEquals(19, (double) converter.convertToRead("19\"ish\""), DELTA);
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testFlow() {
        try {
            DoubleConverter2 converter = new DoubleConverter2(); // flow converter
            assertNull(converter.convertToRead(""));
            assertEquals(0.839, (double) converter.convertToRead("0.839"), DELTA); // m/s
            assertEquals(1.15, (double) converter.convertToRead("1.15"), DELTA);
            assertEquals(171, (double) converter.convertToRead("171"), DELTA); // /min (rpm)
            assertEquals(20.42, (double) converter.convertToRead("20.42"), DELTA);
            assertEquals(1372, (double) converter.convertToRead("1372"), DELTA); // rpm
            assertEquals(997882, (double) converter.convertToRead("997882"), DELTA); // rpm; this is bad
            assertNull(converter.convertToRead("N/A too shallow"));
            assertNull(converter.convertToRead("Too shallow"));
            assertNull(converter.convertToRead("Broken meter"));
            assertNull(converter.convertToRead("Not taken"));
            assertNull(converter.convertToRead("No record"));
            assertEquals(630, (double) converter.convertToRead("630rpm"), DELTA); // rpm
            assertEquals(1157, (double) converter.convertToRead("1157rpm"), DELTA); // rpm
            assertEquals(115, (double) converter.convertToRead("115cm/s"), DELTA); // cm/s
            assertEquals(617671, (double) converter.convertToRead("617671rpm"), DELTA); // rpm; this is bad
            assertEquals(326, (double) converter.convertToRead("326 rpm"), DELTA); // rpm
            assertNull(converter.convertToRead("Spawning season, did not enter stream"));
            assertEquals(50, (double) converter.convertToRead("50 cm/sec"), DELTA); // cm/s
            assertNull(converter.convertToRead("Site Two too high to enter stream"));
            assertNull(converter.convertToRead("N/A"));
            // basically all weird strings are null
            assertEquals(50, (double) converter.convertToRead("~50 cm/sec"), DELTA); // cm/s
            assertEquals(1068, (double) converter.convertToRead("1068 rpm (only right, site 2)"), DELTA); // rpm
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testFlowUnits() {
        try {
            UnitsTextConverter converter = new UnitsTextConverter();
            assertNull(converter.convert(""));
            assertEquals("m/s", converter.convert("m/s"));
            assertNull(converter.convert("?"));
            assertEquals("rpm", converter.convert("/min"));
            assertEquals("rpm", converter.convert("rpm"));
            assertEquals("m/s", converter.convert("m/s?"));
            assertEquals("cm/s", converter.convert("cm/s"));
            // also think about what happens when units are in the other column
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testPhosphateAndNitr() {
        // no converter yet (or really ever probably)
        // all values recorded are 0 basically
    }

    @Test
    public void testConductivity() {
        try {
            SimpleDoubleConverter converter = new SimpleDoubleConverter();
            assertNull(converter.convertToRead(""));
            assertEquals(205, (double) converter.convertToRead("205"), DELTA);
            assertEquals(199.5, (double) converter.convertToRead("199.5"), DELTA);
            assertNull(converter.convertToRead("not recorded"));
            assertNull(converter.convertToRead("No record"));
            // same with other Strings
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testRain() {
        // all Strings, might want to use numeric scale?
    }

    @Test
    public void testBenthicMacros() {
        // Strings
    }

    @Test
    public void testDataRecorder() {
        // Strings
    }

    @Test
    public void testNotes() {
        // Strings
    }

}
