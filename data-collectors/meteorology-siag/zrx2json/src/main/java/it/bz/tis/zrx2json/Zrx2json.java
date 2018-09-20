package it.bz.tis.zrx2json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Zrx2json {

	public static String parse(String zrx) throws IOException{
		List<Meteostation> stations = new ArrayList<Meteostation>();
		BufferedReader reader = new BufferedReader(new StringReader(zrx));
		String line;
		Meteostation station = null;
		while ((line = reader.readLine()) != null){
			if (line.startsWith("##")){
				if (station == null)
					station = new Meteostation();
				station.addComment(line.substring(2));
			}
			else if (line.startsWith("#")){
				if (station == null || station.getDataPoints() != null)
					station = new Meteostation();
				station.addParams(line.substring(1));
			}
			else{
				if (station == null)
					station = new Meteostation();
				if (station.getDataPoints() == null)
					stations.add(station);
				station.addDataPoint(line);
			}
		}
		return parseJavaObject(stations);
	}
	public static String parse(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line=reader.readLine()) != 	null){
			buffer.append(line);
			buffer.append("\n");
		}
		reader.close();
		
		return parse(buffer.toString());
	}
	private static String parseJavaObject(List<Meteostation> stations) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(stations);
	}
	
}
