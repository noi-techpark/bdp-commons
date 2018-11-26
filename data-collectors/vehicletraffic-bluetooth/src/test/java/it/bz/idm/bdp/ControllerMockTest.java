package it.bz.idm.bdp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.dto.OddsRecordDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext*.xml" })
@WebAppConfiguration
public class ControllerMockTest {

	@Autowired
	private WebApplicationContext context;
	private MockMvc mock;

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setup() {
		mock = MockMvcBuilders.webAppContextSetup(context).build();
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	@Test
	public void testPostRecords() {
		OddsRecordDto odd = new OddsRecordDto();
		Date gathered_on = new Date();
		odd.setGathered_on(gathered_on);
		odd.setMac("A random String which becomes a MAC-Address");
		odd.setStationcode("the id");
		odd.setUtcInMs(gathered_on.getTime());
		List<OddsRecordDto> records = new ArrayList<>();
		// regexp ^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$
		records.add(odd);
		try {
			String sObject = mapper.writeValueAsString(records);
			mock.perform(post("/json", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(sObject)).andExpect(status().isGatewayTimeout());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetLastRecord() throws Exception {
			mock.perform(get("/json", new Object[0]).param("station-id", "Station1"))
					.andExpect(status().isGatewayTimeout());
	}
}
