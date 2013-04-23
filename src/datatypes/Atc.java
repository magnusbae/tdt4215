package datatypes;

import java.util.ArrayList;

public class Atc {
	
	String atcCode;
	String label;
	String synoym;
	public Atc(){
		
	};
	public String getAtcCode() {
		return atcCode;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setAtccode(String atcCode) {
		this.atcCode = atcCode;
	}
	@Override
	public String toString() {
		return atcCode + ": " + label;
	}
	public void addSynonym(String value) {
		synoym += value;
	}
	public String getSynonyms() {
		return synoym;
	}
}
