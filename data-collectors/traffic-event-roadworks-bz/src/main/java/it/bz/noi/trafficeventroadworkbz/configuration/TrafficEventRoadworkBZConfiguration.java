package it.bz.noi.trafficeventroadworkbz.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.UUID;

@Configuration
@PropertySource("classpath:META-INF/spring/application.properties")
public class TrafficEventRoadworkBZConfiguration {

    @Value( "${app.origin}" )
    private String origin;

	@Value( "${app.provenance.name}" )
    private String provenanceName;

	@Value( "${app.provenance.version}" )
    private String provenanceVersion;

    @Value("${integreenTypology}")
    private String integreenTypology;

    @Value("${uuidNamespace}")
    private UUID uuidNamespace;

    public String getOrigin() {
        return origin;
    }

    public String getIntegreenTypology() {
        return integreenTypology;
    }

    public UUID getUuidNamespace() {
        return uuidNamespace;
    }

	public String getProvenanceName() {
		return provenanceName;
	}

	public String getProvenanceVersion() {
		return provenanceVersion;
	}
}
