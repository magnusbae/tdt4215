package somepackage;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;




public class Main {
	static Directory dirICD10;
	static Directory dirAtc;
	static Directory dirNLH;
	static Analyzer ana = new NorwegianAnalyzer(Version.LUCENE_CURRENT);
	static Case[] cases = null;
	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("lib/jena-log4j.properties");
		cases = CaseReader.readCases();
		
		
		try {
			dirICD10 = new SimpleFSDirectory(new File("Index/icd10"));
			dirAtc = new SimpleFSDirectory(new File("Index/atc"));
			dirNLH = new SimpleFSDirectory(new File("Index/NLH"));

			IndexFiles index = new IndexFiles(dirICD10,dirAtc,dirNLH, ana);
			index.indexICD10();
			index.indexAtc();
			index.indexNLH();

		
			SearchFiles sf = new SearchFiles();
			for(Case c:cases){
				//							for(String s:c.getSentences()){
				String s = c.getCaseText();
				sf.Search(s, dirNLH, ana);

				sf.Search(s, dirICD10, ana);
				sf.Search(s, dirAtc, ana);
								}


		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
