package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import datatypes.NLH;

public class NLHParser {
	private ArrayList<NLH> parsedNLH;

	public NLHParser() {
		parsedNLH = new ArrayList<NLH>();

		FileInputStream is;

		try {
			File folder = new File("Data/NLH/T/");
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile()) {
					parse(file.getName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parse(String name) throws IOException {
		FileInputStream fs = new FileInputStream("Data/NLH/T/"+name);
		Document doc = Jsoup.parse(fs, "UTF-8", "");
		
		final ArrayList<NLH> chapters = new ArrayList<NLH>();
		doc.traverse(new NodeVisitor() {
			String text = "";
			NLH chapter = new NLH();
			public void head(Node node, int depth) {
				if(node.getClass() == TextNode.class){
					text+= ((TextNode) node).text();
				}
			}
			public void tail(Node node, int depth) {
				if(node.nodeName().equals("defa")){
					chapter.addText(text);
					text = "";
				}
			}
		});
	}

	public ArrayList<NLH> getParsedNLHs() {
		return parsedNLH;
	}
}
