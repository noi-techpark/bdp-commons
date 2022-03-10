package it.bz.noi.a22.vms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MappingTest {

    private StreetSignalsImporter importer = new StreetSignalsImporter();

    @Test
    public void testMapStreetCodes() throws IOException {
        List<Object> streetCodes = importer.getStreetCodes();
        assertNotNull(streetCodes);
        assertFalse(streetCodes.size() == 0);
        assertTrue(streetCodes.get(0) instanceof Map<?, ?>);

		@SuppressWarnings("unchecked")
        Map<String,Object> lastEntry = (Map<String, Object>) streetCodes.get(streetCodes.size()-1);
        assertNotNull(lastEntry.get("id"));
    }
}
