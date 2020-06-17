package it.bz.idm.bdp.augeg4.dto;

public class ProcessedMeasurement {
    private final MeasurementId id;
    private final double rawValue;
    private final Double processedValue;

    public ProcessedMeasurement(MeasurementId id, double rawValue, Double processedValue) {
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

    public Double getProcessedValue() {
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
