package main.converters;

/*
 * Converts given String to a temperature Java Double.
 * Meant for air and water temperature.
 * Includes conversion from Fahrenheit to Celsius.
 */
public class TemperatureConverter extends DoubleConverter2 {
    @Override
    public Object convertToRead(String s) {
        try {
            if (s.endsWith("F") || s.endsWith("Fahrenheit")) {
                // fahrenheit value
                Double value = Double.parseDouble(s.substring(0, s.indexOf("F")));
                return fahrenheitToCelsius(value);
            }
            return super.convertToRead(s);
        } catch (Exception e) {
            return null;
        }
    }

    // converts from fahrenheit to celsius
    public Double fahrenheitToCelsius(Double f) {
        return (f - 32.0) * (5.0 / 9.0);
    }
}
