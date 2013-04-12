package datatypes;

public class NLH {
	String chapter;
	String synonyms = " "; 
	String text = " ";
	public NLH(String c, String t, String s) {
		text = t;
		chapter = c;
		synonyms = s;
	}
	public NLH() {
	}
	public String getChapter() {
		return chapter;
	}
	public void setChapter(String localName) {
		if(localName.contains(""+((char)160))){
			chapter = localName.substring(0, localName.indexOf(((char)160)));
			synonyms += " " + localName.substring(localName.indexOf(((char)160)));
		}else if(localName.contains(" ")){
			chapter = localName.substring(0, localName.indexOf(" "));
			synonyms += " " + localName.substring(localName.indexOf(" "));
		} 
		else {
			chapter = localName;
		}
	}
	public void setText(String value) {
		text = value;
	}
	public void addText(String ownText) {
		text+=ownText + " ";

	}
	public String getText() {
		return text;
	}
	public String getSynonyms(){
		return synonyms;
	}
}
