package main.converters;

import com.opencsv.bean.AbstractBeanField;
import main.Coordinates;

/*
 * Converts given String into a Coordinates object.
 * Meant to be used with the 'Coordinates' column; example String: 47°33'04.16"N 122°09'51.67"W .
 */
public class CoordinatesConverter extends AbstractBeanField<String> {
    @Override
    protected Object convert(String s) {
        try {
            Coordinates result = new Coordinates(s);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
