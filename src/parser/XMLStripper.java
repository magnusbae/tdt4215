package parser;

public class XMLStripper {
	
	
	/**
	 * Strips a string of all XML/HTML entities
	 * @param XMLString
	 * @return A string containing only text/non-XML markup
	 */
	public String stripAllXML(String XMLString){
		String strippedString = XMLString.replaceAll("(<script>.*?</script>)", "");
		strippedString = strippedString.replaceAll("<[^a/].*?>", "");
		strippedString = strippedString.replaceAll("</[^a].*?>", "");
		return strippedString;
		
	}

}
