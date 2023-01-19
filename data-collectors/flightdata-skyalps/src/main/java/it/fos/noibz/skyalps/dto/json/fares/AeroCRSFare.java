package it.fos.noibz.skyalps.dto.json.fares;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// API docs https://docs.aerocrs.com/reference/getfares
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSFare {

    private int count;
    private String airlineDesignator;
    private String airlineICAOcode;
    private String airlinename;
    private String fromCode;
    private String toCode;
    private String fromDate;
    private String toDate;
    private List<String> classes;
    private Double adultFareRT;
    private Double childFareRT;
    private Double infantFareRT;
    private Double tax1RT;
    private Double tax2RT;
    private Double tax3RT;
    private Double tax4RT;
    private Double adultFareOW;
    private Double childFareOW;
    private Double infantFareOW;

    private Double tax1OW;
    private Double tax2OW;
    private Double tax3OW;
    private Double tax4OW;
    private String chargeTaxOnReturnTrip;
    private String notification;

    public AeroCRSFare() {

    }

    public AeroCRSFare(int count, String airlineDesignator, String airlineICAOcode, String airlinename,
            String fromCode, String toCode, String fromDate, String toDate, List<String> classes, Double adultFareRT,
            Double childFareRT, Double infantFareRT, Double tax1rt, Double tax2rt, Double tax3rt, Double tax4rt,
            Double adultFareOW, Double childFareOW, Double infantFareOW, Double tax1ow, Double tax2ow, Double tax3ow,
            Double tax4ow, String chargeTaxOnReturnTrip, String notification) {
        this.count = count;
        this.airlineDesignator = airlineDesignator;
        this.airlineICAOcode = airlineICAOcode;
        this.airlinename = airlinename;
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.classes = classes;
        this.adultFareRT = adultFareRT;
        this.childFareRT = childFareRT;
        this.infantFareRT = infantFareRT;
        tax1RT = tax1rt;
        tax2RT = tax2rt;
        tax3RT = tax3rt;
        tax4RT = tax4rt;
        this.adultFareOW = adultFareOW;
        this.childFareOW = childFareOW;
        this.infantFareOW = infantFareOW;
        tax1OW = tax1ow;
        tax2OW = tax2ow;
        tax3OW = tax3ow;
        tax4OW = tax4ow;
        this.chargeTaxOnReturnTrip = chargeTaxOnReturnTrip;
        this.notification = notification;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAirlineDesignator() {
        return airlineDesignator;
    }

    public void setAirlineDesignator(String airlineDesignator) {
        this.airlineDesignator = airlineDesignator;
    }

    public String getAirlineICAOcode() {
        return airlineICAOcode;
    }

    public void setAirlineICAOcode(String airlineICAOcode) {
        this.airlineICAOcode = airlineICAOcode;
    }

    public String getAirlinename() {
        return airlinename;
    }

    public void setAirlinename(String airlinename) {
        this.airlinename = airlinename;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public Double getAdultFareRT() {
        return adultFareRT;
    }

    public void setAdultFareRT(Double adultFareRT) {
        this.adultFareRT = adultFareRT;
    }

    public Double getChildFareRT() {
        return childFareRT;
    }

    public void setChildFareRT(Double childFareRT) {
        this.childFareRT = childFareRT;
    }

    public Double getInfantFareRT() {
        return infantFareRT;
    }

    public void setInfantFareRT(Double infantFareRT) {
        this.infantFareRT = infantFareRT;
    }

    public Double getTax1RT() {
        return tax1RT;
    }

    public void setTax1RT(Double tax1rt) {
        tax1RT = tax1rt;
    }

    public Double getTax2RT() {
        return tax2RT;
    }

    public void setTax2RT(Double tax2rt) {
        tax2RT = tax2rt;
    }

    public Double getTax3RT() {
        return tax3RT;
    }

    public void setTax3RT(Double tax3rt) {
        tax3RT = tax3rt;
    }

    public Double getTax4RT() {
        return tax4RT;
    }

    public void setTax4RT(Double tax4rt) {
        tax4RT = tax4rt;
    }

    public Double getAdultFareOW() {
        return adultFareOW;
    }

    public void setAdultFareOW(Double adultFareOW) {
        this.adultFareOW = adultFareOW;
    }

    public Double getChildFareOW() {
        return childFareOW;
    }

    public void setChildFareOW(Double childFareOW) {
        this.childFareOW = childFareOW;
    }

    public Double getInfantFareOW() {
        return infantFareOW;
    }

    public void setInfantFareOW(Double infantFareOW) {
        this.infantFareOW = infantFareOW;
    }

    public Double getTax1OW() {
        return tax1OW;
    }

    public void setTax1OW(Double tax1ow) {
        tax1OW = tax1ow;
    }

    public Double getTax2OW() {
        return tax2OW;
    }

    public void setTax2OW(Double tax2ow) {
        tax2OW = tax2ow;
    }

    public Double getTax3OW() {
        return tax3OW;
    }

    public void setTax3OW(Double tax3ow) {
        tax3OW = tax3ow;
    }

    public Double getTax4OW() {
        return tax4OW;
    }

    public void setTax4OW(Double tax4ow) {
        tax4OW = tax4ow;
    }

    public String getChargeTaxOnReturnTrip() {
        return chargeTaxOnReturnTrip;
    }

    public void setChargeTaxOnReturnTrip(String chargeTaxOnReturnTrip) {
        this.chargeTaxOnReturnTrip = chargeTaxOnReturnTrip;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

}
