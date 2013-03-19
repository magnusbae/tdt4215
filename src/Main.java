import datatypes.Case;
import parser.CaseReader;
import search.IndexFiles;
import search.SearchFiles;


public class Main {
	public static void main(String[] args) {
		Case[] cases = CaseReader.readCases();
		IndexFiles index = new IndexFiles();
		index.index();
		SearchFiles sf = new SearchFiles();
		for(Case c:cases)
			for(String s:c.getSentences())
				sf.Search(s, index.getDir(), index.getAnalyzer());
		
		
	}
}
