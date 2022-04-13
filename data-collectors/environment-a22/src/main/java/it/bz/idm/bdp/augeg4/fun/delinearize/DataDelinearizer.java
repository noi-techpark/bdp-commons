package it.bz.idm.bdp.augeg4.fun.delinearize;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.ElaboratedResVal;
import it.bz.idm.bdp.augeg4.face.DataDelinearizerFace;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataDelinearizer implements DataDelinearizerFace {

    private static final Logger LOG = LoggerFactory.getLogger(DataDelinearizer.class.getName());

    private final MeasurementDelinearizer measurementDelinearizer = new MeasurementDelinearizer();

    @Override
    public List<AugeG4RawData> delinearize(List<AugeG4ElaboratedDataDto> data) {
        return data
                .stream()
                .map(this::delinearize)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Optional<AugeG4RawData> delinearize(AugeG4ElaboratedDataDto fromAlgorab) {
        List<RawMeasurement> measurements = delinearizeResVal(fromAlgorab.getResVal());
        if(measurements.isEmpty()) {
            LOG.warn("delinearize() measurements.isEmpty(): all the measurements have an unknown linear function id");
            return Optional.empty();
        }
        return Optional.of(new AugeG4RawData(
                fromAlgorab.getControlUnitId(),
                fromAlgorab.getDateTimeAcquisition(),
                measurements
        ));
    }

    private List<RawMeasurement> delinearizeResVal(List<ElaboratedResVal> rawValues) {
        return rawValues
                .stream()
                .map(measurementDelinearizer::delinearize)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
