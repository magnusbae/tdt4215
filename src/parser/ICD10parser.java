package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.digester3.Digester;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
		  OWLOntologyManager m=OWLManager.createOWLOntologyManager();
		  OWLOntology o;
		try {
			o = m.loadOntologyFromOntologyDocument(new File("Data/icd10no.owl"));
			Reasoner hermit=new Reasoner(o);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}