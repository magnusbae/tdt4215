package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;


import datatypes.ICD10;

public class ICD10parser{

	private ArrayList<ICD10> parsedICDs = new ArrayList<>();
	public ICD10parser() {
	}
	public void addCode(ICD10 code) throws FileNotFoundException
	{	
		parsedICDs.add(code);
	}
	
	public ArrayList<ICD10> getParsedICDs(){
		return parsedICDs;
	}

	public ICD10parser(String icdPath) throws IOException, SAXException {

		parsedICDs = new ArrayList<ICD10>();
		Digester digester = new Digester();
		digester.setValidating(true);

		digester.addObjectCreate("rdf:RDF", ICD10parser.class );
		digester.addObjectCreate("rdf:RDF/owl:Class", ICD10.class );

		// set different properties of owl:Class instance using specified methods
		digester.addCallMethod("rdf:RDF/owl:Class/rdfs:label", "setLabel", 0);
		digester.addCallMethod("rdf:RDF/owl:Class/code_formatted", "setICDCode", 0);
		digester.addCallMethod("rdf:RDF/owl:Class/synonym", "addSynonym", 0);

		// call 'addCode' method when the next 'rdf:RDF/owl:Class' pattern is seen
		digester.addSetNext("rdf:RDF/owl:Class/", "addCode" );

		// now that rules and actions are configured, start the parsing process
		File input = new File(icdPath);
		digester.clear();
		ICD10parser parse = (ICD10parser) digester.parse(input);

	}
}