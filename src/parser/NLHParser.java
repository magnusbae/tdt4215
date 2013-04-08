package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datatypes.NLH;

public class NLHParser {
	private ArrayList<NLH> parsedNLH;

	public NLHParser(String filename) {
		parsedNLH = new ArrayList<NLH>();

		FileInputStream is;

		try {
			File folder = new File("Data/NLH/T/");
			File[] listOfFiles = folder.listFiles();
			
			for (File file : listOfFiles) {
			    if (file.isFile()) {
			        NLHParser.parse(file.getName());
			    }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void parse(String name) throws IOException {
		FileInputStream fs = new FileInputStream("Data/NLH/T/"+name);
		Document doc = Jsoup.parse(fs, "UTF-8", "");

		Elements chapters = doc.select("H2");
		for(Element c:chapters){
			NLH chapter = new NLH();
			for(Element e:c.getAllElements()){
				chapter.setChapter(e.ownText());
				for(Element i:e.getElementsByAttribute("defa")){
					chapter.addText(i.ownText());
				}
			}
		}		
	}

	public ArrayList<NLH> getParsedNLHs() {
		return parsedNLH;
	}
}
