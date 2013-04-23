package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import datatypes.ICD10;

public class ICD10parser{

    private ArrayList<ICD10> parsedICDs;
    private OntModel onto;
    public ICD10parser(String filename) {
        parsedICDs = new ArrayList<ICD10>();
        
        FileInputStream is;
        
        try {
            is = new FileInputStream(filename);
            
            onto = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            onto.read(is, "RDF/XML");

            StmtIterator stit = onto.listStatements();
            ICD10 icd10 = new ICD10();
            while (stit.hasNext()) {

                Statement stmt = stit.nextStatement(); // get next statement
                Resource subject = stmt.getSubject(); // get the subject
                Property predicate = stmt.getPredicate(); // get the predicate
                RDFNode object = stmt.getObject(); // get the object

                // Create new object
                if (icd10.getICDCode() == null) {
                	icd10.setICDCode(subject.getLocalName());
                }
                
                // Add object to list when no more statements
                if (!icd10.getICDCode().equals(subject.getLocalName())){
                    parsedICDs.add(icd10);
                    icd10 = new ICD10();
                }
                // Add label and synonyms
                if (predicate.getLocalName().equals("label") || predicate.getLocalName().equals("synonym")||predicate.getLocalName().equals("seeAlso")||predicate.getLocalName().equals("underterm")) {
                    
                	String value = object.toString();

                	if(value.contains("http")){
                    	int i = value.indexOf("http");
                    	value = value.substring(0, i - 2);
                    }
                   
                    if (predicate.getLocalName().equals("label")){
                        icd10.setLabel(value);
                    }
                    else if (predicate.getLocalName().equals("synonym")||predicate.getLocalName().equals("seeAlso")||predicate.getLocalName().equals("underterm")){
                        icd10.addSynonym(value);
                    }
                    if(predicate.getLocalName().equals("subClassOf")){
                    	icd10.addParents(value);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ICD10> getParsedICDs() {
        return parsedICDs;
    }

	public OntModel getOnto() {
		// TODO Auto-generated method stub
		return onto;
	}
}