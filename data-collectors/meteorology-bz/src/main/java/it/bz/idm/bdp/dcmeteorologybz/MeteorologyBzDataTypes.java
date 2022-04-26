package it.bz.idm.bdp.dcmeteorologybz;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource({ "classpath:/META-INF/spring/types.properties" })
public class MeteorologyBzDataTypes {

    private static final Logger LOG = LoggerFactory.getLogger(MeteorologyBzDataTypes.class.getName());

    @Autowired
    private Environment env;

    private Map<String, String> typeNames;

    public MeteorologyBzDataTypes() {
        LOG.debug("Create instance");
        typeNames = new HashMap<String, String>();
    }

    public String getProperty(String key) {
        String value = this.typeNames.get(key);
        if ( value == null ) {
            value = env.getProperty(key);
            if ( value != null ) {
                typeNames.put(key, value);
            }
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = this.typeNames.get(key);
        if ( value == null ) {
            value = env.getProperty(key, defaultValue);
            if ( value != null ) {
                typeNames.put(key, value);
            }
        }
        return value;
    }

}
