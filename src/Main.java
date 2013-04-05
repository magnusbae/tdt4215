import org.apache.log4j.PropertyConfigurator;

import datatypes.Case;
import parser.CaseReader;
import search.IndexFiles;
import search.SearchFiles;


public class Main {
	public static void main(String[] args) {
		PropertyConfigurator.configure("lib/jena-log4j.properties");
		Case[] cases = CaseReader.readCases();
		IndexFiles index = new IndexFiles();
		index.index();
		SearchFiles sf = new SearchFiles();
		for(Case c:cases)
			for(String s:c.getSentences())
				sf.Search(s, index.getDir(), index.getAnalyzer());


	}
}
