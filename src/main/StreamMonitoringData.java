package main;

import com.opencsv.bean.*;
import main.converters.*;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.Date;

public class StreamMonitoringData /*implements Comparable<StreamMonitoringData>*/ {
    // date
    @CsvCustomBindByPosition(position = 0, converter = DateConverter.class)
    private Date date;

    // site
    @CsvBindByPosition(position = 1)
    private Integer site;

    // coordinates
    @CsvCustomBindByPosition(position = 2, converter = CoordinatesConverter.class)
    private Coordinates coordinates;

    // sheet number
    @CsvBindByPosition(position = 3)
    private Integer sheet;

    /* Turbidity */

    // turbidity trials 1 to 6 with first unit
    // 60+ (60), <3 (3), <10 (10), >1.2m (1.2...), <10 NTU (10...), 1.2m (1.2...),
    // unreadable (null), 1.2m< (1.2...), >10 NTU (10...), 10> NTU (10...)
    @CsvBindAndJoinByPosition(position = "4-9", elementType = Double.class,
            converter = DoubleConverter2.class)
    private MultiValuedMap<Integer, Double> turbiditiesFirst;

    // usually NTU
    @CsvCustomBindByPosition(position = 10, converter = UnitsTextConverter.class)
    private String turbidityUnitsFirst;

    // turbidity trials 1 to 6 with second unit
    // >1.20 (1.2)
    // other: 73 m???, 120 m???
    @CsvBindAndJoinByPosition(position = "11-16", elementType = Double.class,
            converter = DoubleConverter2.class)
    private MultiValuedMap<Integer, Double> turbiditiesSecond;

    // usually cm
    @CsvCustomBindByPosition(position = 17, converter = UnitsTextConverter.class)
    private String turbidityUnitsSecond;

    // air temp (trials 1 to 6)
    @CsvBindAndJoinByPosition(position = "18-23", elementType = Double.class,
                              converter = TemperatureConverter.class)
    private MultiValuedMap<Integer, Double> airTemps;

    // water temp
    @CsvBindAndJoinByPosition(position = "24-29", elementType = Double.class,
                              converter = TemperatureConverter.class)
    private MultiValuedMap<Integer, Double> waterTemps;

    // pH
    @CsvBindAndJoinByPosition(position = "30-35", elementType = Double.class,
                              converter = SimpleDoubleConverter.class)
    private MultiValuedMap<Integer, Double> pHs;

    // DO
    @CsvBindAndJoinByPosition(position = "36-41", elementType = Double.class,
                              converter = DoubleConverter2.class)
    private MultiValuedMap<Integer, Double> oxygens;

    /* Flow */

    // flow right trials 1 to 6
    // Strings (null), 745 rpm (745...), 678rpm (678...), 45 cm/s (45...),
    // 50 cm/sec (50...), ~50cm/sec (50...), 1068 rpm (only right, site 2) [1068...]
    // other: 617671 rpm???
    @CsvBindAndJoinByPosition(position = "42-47", elementType = Double.class,
            converter = DoubleConverter2.class)
    private MultiValuedMap<Integer, Double> flowRights;

    // flow center trials 1 to 6
    // rpm, cm/s, Strings, cm/sec, ~ (nothing new)
    // other: 617678 rpm??? sm/sec???
    @CsvBindAndJoinByPosition(position = "48-53", elementType = Double.class,
            converter = DoubleConverter2.class)
    private MultiValuedMap<Integer, Double> flowCenters;

    // flow left trials 1 to 6
    // nothing new
    // other: 617702 rpm
    @CsvBindAndJoinByPosition(position = "54-59", elementType = Double.class,
            converter = DoubleConverter2.class)
    private MultiValuedMap<Integer, Double> flowLefts;

    // flow units (m/s, /min, rpm, cm/s, etc.)
    @CsvCustomBindByPosition(position = 60, converter = UnitsTextConverter.class)
    private String flowUnits;

    // phosphate (seems to be 0 always or not measured)
    @CsvCustomBindByPosition(position = 61, converter = DoubleConverter.class)
    private Double phosphate;

    // nitrate/nitrite (seems to be 0 always or not measured)
    @CsvCustomBindByPosition(position = 62, converter = DoubleConverter.class)
    private Double nitr;

    // conductivity
    @CsvBindAndJoinByPosition(position = "63-68", elementType = Double.class,
                              converter = SimpleDoubleConverter.class)
    private MultiValuedMap<Integer, Double> conductivities;

    // rain
    // varying capitalizations: heavy, trace, light, none, moderate [need to figure out which is which]
    // trace/intermittent, light to moderate, moderate to heavy,
    // very little..., today no rain, (no observations...
    @CsvBindByPosition(position = 69)
    private String rain;

    // benthic macro
    @CsvBindByPosition(position = 70)
    private String benthicMacros;

    // recorder/group
    @CsvBindByPosition(position = 71)
    private String dataRecorder;

    @CsvBindByPosition(position = 72)
    private String notes;

    ///////////////////////////////////////////////////////////////

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Integer getSheet() {
        return sheet;
    }

    public void setSheet(Integer sheet) {
        this.sheet = sheet;
    }

    public MultiValuedMap<Integer, Double> getTurbiditiesFirst() {
        return turbiditiesFirst;
    }

    public void setTurbiditiesFirst(MultiValuedMap<Integer, Double> turbiditiesFirst) {
        this.turbiditiesFirst = turbiditiesFirst;
    }

    public String getTurbidityUnitsFirst() {
        return turbidityUnitsFirst;
    }

    public void setTurbidityUnitsFirst(String turbidityUnitsFirst) {
        this.turbidityUnitsFirst = turbidityUnitsFirst;
    }

    public MultiValuedMap<Integer, Double> getTurbiditiesSecond() {
        return turbiditiesSecond;
    }

    public void setTurbiditiesSecond(MultiValuedMap<Integer, Double> turbiditiesSecond) {
        this.turbiditiesSecond = turbiditiesSecond;
    }

    public String getTurbidityUnitsSecond() {
        return turbidityUnitsSecond;
    }

    public void setTurbidityUnitsSecond(String turbidityUnitsSecond) {
        this.turbidityUnitsSecond = turbidityUnitsSecond;
    }

    public MultiValuedMap<Integer, Double> getAirTemps() {
        return airTemps;
    }

    public void setAirTemps(MultiValuedMap<Integer, Double> airTemps) {
        this.airTemps = airTemps;
    }

    public MultiValuedMap<Integer, Double> getWaterTemps() {
        return waterTemps;
    }

    public void setWaterTemps(MultiValuedMap<Integer, Double> waterTemps) {
        this.waterTemps = waterTemps;
    }

    public MultiValuedMap<Integer, Double> getPHs() {
        return pHs;
    }

    public void setPHs(MultiValuedMap<Integer, Double> pHs) {
        this.pHs = pHs;
    }

    public MultiValuedMap<Integer, Double> getOxygens() {
        return oxygens;
    }

    public void setOxygens(MultiValuedMap<Integer, Double> oxygens) {
        this.oxygens = oxygens;
    }

    public MultiValuedMap<Integer, Double> getFlowRights() {
        return flowRights;
    }

    public void setFlowRights(MultiValuedMap<Integer, Double> flowRights) {
        this.flowRights = flowRights;
    }

    public MultiValuedMap<Integer, Double> getFlowCenters() {
        return flowCenters;
    }

    public void setFlowCenters(MultiValuedMap<Integer, Double> flowCenters) {
        this.flowCenters = flowCenters;
    }

    public MultiValuedMap<Integer, Double> getFlowLefts() {
        return flowLefts;
    }

    public void setFlowLefts(MultiValuedMap<Integer, Double> flowLefts) {
        this.flowLefts = flowLefts;
    }

    public String getFlowUnits() {
        return flowUnits;
    }

    public void setFlowUnits(String flowUnits) {
        this.flowUnits = flowUnits;
    }

    public Double getPhosphate() {
        return phosphate;
    }

    public void setPhosphate(Double phosphate) {
        this.phosphate = phosphate;
    }

    public Double getNitr() {
        return nitr;
    }

    public void setNitr(Double nitr) {
        this.nitr = nitr;
    }

    public MultiValuedMap<Integer, Double> getConductivities() {
        return conductivities;
    }

    public void setConductivities(MultiValuedMap<Integer, Double> conductivities) {
        this.conductivities = conductivities;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getBenthicMacros() {
        return benthicMacros;
    }

    public void setBenthicMacros(String benthicMacros) {
        this.benthicMacros = benthicMacros;
    }

    public String getDataRecorder() {
        return dataRecorder;
    }

    public void setDataRecorder(String dataRecorder) {
        this.dataRecorder = dataRecorder;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /*@Override
    public int compareTo(StreamMonitoringData o) {
        if (this.getDate() == null) {
            return o.getDate() == null ? 0 : -1;

        } else {
            return o.getDate() == null ? 1 :
                    (this.getDate().compareTo(o.getDate()));
        }
    }*/

    @Override
    public String toString() {
        if (this.getDate() == null) {
            return null;
        }
        return this.getDate().getYear() + 1900 + "";
    }
}
