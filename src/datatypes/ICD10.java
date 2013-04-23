package datatypes;

import java.util.ArrayList;

public class ICD10 {
	
	ArrayList<String> synonyms = new ArrayList<>();
	String ICDCode;
	String label;
	String parents;
	public ICD10(){
		
	};
	public String getSynonyms() {
		String returns = "";
		for(String syn:synonyms)
			returns+=" " +syn;
		return returns;
	}
	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}
	public String getICDCode() {
		return ICDCode;
	}
	public String getLabel() {
		return label;
	}
	public void addSynonym(String e) {
		synonyms.add(e);
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setICDCode(String iCDCode) {
		ICDCode = iCDCode;
	}
	@Override
	public String toString() {
		return ICDCode + ": " + label + ": " + synonyms;
	}
	public String getParents() {
		return parents;
	}
	public void addParents(String value) {
		parents+= value;
	}
}
