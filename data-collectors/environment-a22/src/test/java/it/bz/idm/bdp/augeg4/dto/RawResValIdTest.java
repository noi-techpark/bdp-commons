package it.bz.idm.bdp.augeg4.dto;

import it.bz.idm.bdp.augeg4.dto.fromauge.RawResValId;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RawResValIdTest {

    @Test
    public void test_correct_mapping_from_int_values() {
        // given
        int resValId=2;

        // when
        Optional<RawResValId> id = RawResValId.getId(resValId);

        // then
        assertEquals(RawResValId.TEMPERATURA,id.get());
    }


    @Test
    public void test_wrong_mapping_from_int_values() {
        // given
        int resValId=4;

        // when
        Optional<RawResValId> id = RawResValId.getId(resValId);

        // then
        assertFalse(id.isPresent());
    }

}
