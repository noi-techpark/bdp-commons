package it.bz.noi.a22.vms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreetSignalsImporter {

    public List<Object> getStreetCodes() throws IOException{
        List<Object> inputList = new ArrayList<>();
        InputStream stream = getClass().getResourceAsStream("streetcodes.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        inputList = br.lines().map(mapperFunction).collect(Collectors.toList());
        br.close();
        return inputList;
    }
    private Function<String,Object> mapperFunction = csvLine -> {
        Map<String, Object> codes = new HashMap<>();
        String [] fields = csvLine.split(";");
        if (fields.length > 0)
            codes.put("id",fields[0]);
        if (fields.length >1)
            codes.put("description", Collections.singletonMap("it", fields[1]));
        if (fields.length > 2)
            codes.put("signalAsBase64",fields[2]);
        return codes;
    };
}
