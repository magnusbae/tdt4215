import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import datatypes.Case;
import parser.CaseReader;
import search.IndexFiles;
import search.SearchFiles;


public class Main {
	public static void main(String[] args) {
		PropertyConfigurator.configure("lib/jena-log4j.properties");
		Case[] cases = CaseReader.readCases();
		Directory dirICD10;
		Directory dirAtc;
		Analyzer ana = new NorwegianAnalyzer(Version.LUCENE_CURRENT);
		try {
			dirICD10 = new SimpleFSDirectory(new File("Index/icd10"));
			dirAtc = new SimpleFSDirectory(new File("Index/atc"));
			IndexFiles indexICD10 = new IndexFiles(dirICD10, ana);
//			indexICD10.indexICD10();
			IndexFiles indexAtc = new IndexFiles(dirAtc, ana);
//			indexAtc.indexAtc();
			SearchFiles sf = new SearchFiles();
			Case c = cases[0];
//			for(Case c:cases)
//				for(String s:c.getSentences()){
			String s = c.getCaseText();
			sf.Search(s, dirICD10, ana);
					sf.Search(s, dirAtc, ana);
//				}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}
