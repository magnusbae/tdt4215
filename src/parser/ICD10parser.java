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

import org.semanticweb.HermiT.Reasoner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import datatypes.ICD10;

public class ICD10parser{

    private ArrayList<ICD10> parsedICDs;

    public ICD10parser(String filename) {
        parsedICDs = new ArrayList<ICD10>();
        
        FileInputStream is;
        
        try {
            is = new FileInputStream(filename);
            
            OntModel onto = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
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
                        if(value.contains("Hekseskudd"))
                        	System.out.println(value);;
                    }
                }
            }
// Iterator it = onto.listAllOntProperties();
// while (it.hasNext()) {
// Property p = (Property) it.next();
// properties.add(p);
// }
// System.out.println(properties);
// Iterator it2 = onto.listNamedClasses();
// while (it2.hasNext()) {
// OntClass ontClass = (OntClass) it2.next();
// String id = ontClass.getLocalName();
// System.out.println(id);
// String label = ontClass.getLabel(null);
// System.out.println(label);
//
// System.out.println(ontClass.getCardinality(null));
// System.out.println(ontClass.getClass());
// System.out.println(ontClass.getComment(null));
// System.out.println(ontClass.getNameSpace());
// System.out.println(ontClass.getSubClass());
// System.out.println(ontClass.getSuperClass());
// System.out.println(ontClass.getRDFType());
// for (int i = 0; i<properties.size(); i++){
// Property p = properties.get(i);
// System.out.println(p.getLocalName());
// RDFNode r = ontClass.getPropertyValue(p);
// if (r != null){
// System.out.println(r.toString());
// }
//
//
// }
// ICD10 icd = new ICD10(id, label, null, null);
// Iterator it3 = ontClass.listDeclaredProperties();
// while (it3.hasNext()) {
// OntProperty p2 = (OntProperty) it3.next();
// if (ontClass.hasProperty(p2)) {
// System.out.println(p2.getLocalName());
// RDFNode r = p2.getPropertyValue(p2);
// System.out.println(r);
// }
//
// }
// }
// System.out.println(onto.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < parsedICDs.size(); i++){
            //System.out.println(parsedICDs.get(i));
        }
    }

    public ArrayList<ICD10> getParsedICDs() {
        return parsedICDs;
    }
}