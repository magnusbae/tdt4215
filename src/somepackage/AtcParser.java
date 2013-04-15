package somepackage;



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

import org.semanticweb.HermiT.Reasoner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class AtcParser{

	private OntModel onto;
    public OntModel getOnto() {
		return onto;
	}
	private ArrayList<Atc> parsedAtcs;

    public AtcParser(String filename) {
        parsedAtcs = new ArrayList<Atc>();
        FileInputStream is;
        
        try {
            is = new FileInputStream(filename);
            
            onto = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            onto.read(is, "RDF/XML");

            StmtIterator stit = onto.listStatements();
            Atc atc = new Atc();
            while (stit.hasNext()) {

                Statement stmt = stit.nextStatement(); // get next statement
                Resource subject = stmt.getSubject(); // get the subject
                Property predicate = stmt.getPredicate(); // get the predicate
                RDFNode object = stmt.getObject(); // get the object

                // Create new object
                if (atc.getAtcCode() == null) {
                	atc.setAtccode(subject.getLocalName());
                }
                
                // Add object to list when no more statements
                if (!atc.getAtcCode().equals(subject.getLocalName())){
                    parsedAtcs.add(atc);
                    atc = new Atc();
                }
                // Add label and synonyms
                if (predicate.getLocalName().equals("label") || predicate.getLocalName().equals("synonym")||predicate.getLocalName().equals("seeAlso")||predicate.getLocalName().equals("underterm")) {
                	String value = object.toString();

                	if(value.contains("http")){
                    	int i = value.lastIndexOf("http");
                    	value = value.substring(0, i - 2);
                    }
//                	System.out.println("added: " + value);
                   
                    if (predicate.getLocalName().equals("label")){
                        atc.setLabel(value);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Atc> getParsedAtcs() {
        return parsedAtcs;
    }
}
