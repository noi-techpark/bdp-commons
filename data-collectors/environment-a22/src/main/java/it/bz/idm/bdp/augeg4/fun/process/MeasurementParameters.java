package it.bz.idm.bdp.augeg4.fun.process;

/**
 * Parameters used by the MeasurementProcessor for a specific Measurement
 */
public class MeasurementParameters {

    private MeasurementParametersId id;

    private double a;
    private double b;
    private double c;
    private double d;
    private double e;
    private double f;

    public MeasurementParameters(MeasurementParametersId id, double a, double b, double c, double d, double e, double f) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public double getE() {
        return e;
    }

    public double getF() {
        return f;
    }
}
