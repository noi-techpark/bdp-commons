package it.bz.idm.bdp.augeg4.dto.fromauge;

import javax.swing.text.html.Option;
import java.util.Optional;

public enum RawResValId {
    CO2(1),
    TEMPERATURA(2),
    UMIDITA_RELATIVA(3),
    TEMPERATURA_INTERNA(7),
    O3(8),
    NO2(9),
    CO(10),
    VOC(11),
    PM10(12),
    PM2_5(13),
    NO2_ALPHASENSE(14),
    NO_ALPHASENSE(15),
    NO2_ORION(16);

    private int idVal;

    RawResValId(int idVal) {
        this.idVal = idVal;
    }

    public int getIdVal() {
        return idVal;
    }

    public static Optional<RawResValId> getId(int idVal) {
        for (RawResValId v: RawResValId.values()) {
            if (v.getIdVal() == idVal) return Optional.of(v);
        }
        return Optional.empty();
    }

}
