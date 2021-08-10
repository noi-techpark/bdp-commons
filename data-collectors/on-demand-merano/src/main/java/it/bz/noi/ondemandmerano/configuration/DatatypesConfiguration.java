package it.bz.noi.ondemandmerano.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@PropertySource("classpath:datatypes.properties")
public class DatatypesConfiguration {

    @Value("#{${position}}")
    private Map<String, String> positionMap;

    private DatatypeConfiguration position;

    @PostConstruct
    private void init() {
        ObjectMapper mapper = new ObjectMapper();
        position = mapper.convertValue(positionMap, DatatypeConfiguration.class);
    }

    public DatatypeConfiguration getPosition() {
        return position;
    }

    public List<DatatypeConfiguration> getAllDataTypes() {
        return Arrays.asList(position);
    }
}
