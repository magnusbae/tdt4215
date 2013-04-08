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
		Directory dir;
		try {
			dir = new SimpleFSDirectory(new File("Index/icd10"));
			Analyzer ana = new NorwegianAnalyzer(Version.LUCENE_CURRENT);
			IndexFiles index = new IndexFiles(dir, ana);
			index.index();
			SearchFiles sf = new SearchFiles();
			for(Case c:cases)
				for(String s:c.getSentences())
					sf.Search(s, dir, ana);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
