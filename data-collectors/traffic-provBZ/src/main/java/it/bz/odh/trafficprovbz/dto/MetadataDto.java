package it.bz.odh.trafficprovbz.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class MetadataDto {

	@JsonProperty("Id")
	private String id;

	@JsonProperty("Nome")
	private String name;

	@JsonProperty("SchemiDiClassificazione")
	private int classificationSchema;

	private final Map<String, Object> otherFields = new HashMap<>();

	// save odhId because name/id mismatch and to save the data the odhId is needed
	private HashMap<String, Integer> lanes;

	public Map<String, Integer> getLanes() {
		return lanes;
	}

	public void addLane(int id, String name) {
		if (lanes == null)
			lanes = new HashMap<>();
		lanes.put(name, id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getClassificationSchema() {
		return this.classificationSchema;
	}

	public void setClassificationSchema(int classificationSchema) {
		this.classificationSchema = classificationSchema;
	}

	// Capture ac
	@JsonAnyGetter
	public Map<String, Object> getOtherFields() {
		return otherFields;
	}

	@JsonAnySetter
	public void setOtherField(String name, Object value) {
		otherFields.put(name, value);
	}
}
