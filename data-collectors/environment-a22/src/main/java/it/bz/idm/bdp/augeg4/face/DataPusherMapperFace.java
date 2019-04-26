package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;

import java.util.List;

/**
 * Maps a list of {@link AugeG4ToHubDataDto} to a single {@link DataMapDto}, used by the Pusher to send data to the hub
 */
public interface DataPusherMapperFace {

    /**
     * Maps the list of DTOs to the hierarchical structure needed by the Pusher.
     *
     * Example of generated map:
     * rootMap --
     *          |-- Station: "A"
     *            |-- Parameter: "temperature"
     *              |-- Values: { (time, 23), (time, ...) }
     *            |-- Parameter: "pressure"
     *              |-- Values: { (time, 1.2), (time, ...) }
     *			|-- Station: "B"
     *            |-- Parameter: "temperature"
     *              |-- Values: { (time, 21), (time, ...) }
     *            |-- Parameter: "pressure"
     *              |-- Values: { (time, 1.5), (time, ...) }
     *
     * where values are a list of SimpleRecordDtos
     *
     * @param measurementsByStations List of DTOs to map
     * @return Hierarchical map structure
     */
    DataMapDto<RecordDtoImpl> map (List<AugeG4ToHubDataDto> measurementsByStations);

}
