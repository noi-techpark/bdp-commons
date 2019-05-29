package it.bz.idm.bdp.augeg4.dto;

public class ProcessedMeasurement {
    private final MeasurementId id;
    private final double rawValue;
    private final double processedValue;

    public ProcessedMeasurement(MeasurementId id, double rawValue, double processedValue) {
        this.id = id;
        this.rawValue = rawValue;
        this.processedValue = processedValue;
    }

    public MeasurementId getId() {
        return id;
    }

    public double getRawValue() {
        return rawValue;
    }

    public double getProcessedValue() {
        return processedValue;
    }

    @Override
    public String toString() {
        return "ProcessedMeasurementToHub{" +
                "id=" + id +
                ", rawValue=" + rawValue +
                ", processedValue=" + processedValue +
                '}';
    }
}
