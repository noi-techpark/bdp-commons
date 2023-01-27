package it.fos.noibz.skyalps.dto.json.realtime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeDto {

    List<RealtimeArrivalDto> dep;
    List<RealtimeDeparureDto> arr;

    public RealtimeDto() {
        
    }

    public List<RealtimeArrivalDto> getDep() {
        return dep;
    }

    public void setDep(List<RealtimeArrivalDto> dep) {
        this.dep = dep;
    }

    public List<RealtimeDeparureDto> getArr() {
        return arr;
    }

    public void setArr(List<RealtimeDeparureDto> arr) {
        this.arr = arr;
    }

}
