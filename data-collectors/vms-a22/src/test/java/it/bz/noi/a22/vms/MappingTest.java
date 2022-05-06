package it.bz.noi.a22.vms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import it.bz.idm.bdp.dto.SimpleRecordDto;

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

	@Test
	public void testConcatenateValues() {
		List<String> dtoValues = new ArrayList<>();
		dtoValues.add(" ");
		dtoValues.add("1");
		dtoValues.add("Hello");

		List<String> values = new ArrayList<>();
		values.add(" ");
		values.add("  3 ");
		values.add("1    ");
		values.add("  Hasta la     pasta   ");

		List<String> expected = new ArrayList<>();
		// dto.value = " "
		expected.add(" ");
		expected.add("3");
		expected.add("1");
		expected.add("Hasta la pasta");

		// dto.value = "1"
		expected.add("1");
		expected.add("1|3");
		expected.add("1");
		expected.add("1|Hasta la pasta");

		// dto.value = "Hello"
		expected.add("Hello");
		expected.add("Hello|3");
		expected.add("Hello|1");
		expected.add("Hello|Hasta la pasta");

		int i = 0;
		for (String dtoValue : dtoValues) {
			for (String value : values) {
				SimpleRecordDto dto = new SimpleRecordDto(0L, dtoValue, 0);
				MainA22Sign.concatenateValues(dto, value);
				assertEquals(expected.get(i), dto.getValue().toString());
				i++;
			}
		}

		// Testing 3 values: skipping one (with tabs and spaces)
		SimpleRecordDto dto = new SimpleRecordDto(0L, "x", 0);
		MainA22Sign.concatenateValues(dto, " ");
		MainA22Sign.concatenateValues(dto, "	y  ");
		assertEquals("x|y", dto.getValue().toString());

		// Testing 3 values: with tabs and spaces
		dto = new SimpleRecordDto(0L, "x", 0);
		MainA22Sign.concatenateValues(dto, "y yy    1");
		MainA22Sign.concatenateValues(dto, " 	zZ   Z  ");
		assertEquals("x|y yy 1|zZ Z", dto.getValue().toString());
	}
}
