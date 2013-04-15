package somepackage;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.hamcrest.core.IsInstanceOf;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;


public class NLHParser {
	private ArrayList<NLH> parsedNLH;

	public NLHParser() {
		parsedNLH = new ArrayList<NLH>();
		try {
			File folder = new File("Data/NLH/T/");
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile()) {
					parse(file.getName(), "Data/NLH/T/");
				}
			}
			folder = new File("Data/NLH/L/");
			listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile()) {
					parse(file.getName(), "Data/NLH/L/");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean recordText = false;
	boolean added = true;
	boolean getName = false;
	private void parse(String name, String dir) throws IOException {
		FileInputStream fs = new FileInputStream(dir+name);
		Document doc = Jsoup.parse(fs, "UTF-8", "");
		doc.traverse(new NodeVisitor() {
			String text = "";
			String name = "";
			NLH chapter = new NLH();
			NLH mainChapter = new NLH();
			public void head(Node node, int depth) {
				if(node instanceof Element)
					if((((Element)node).tagName().equalsIgnoreCase("H3"))){
						chapter = new NLH();
						parsedNLH.add(chapter);
						name = "";
						getName = true;
					}else if (((Element)node).tagName().equalsIgnoreCase("H2")){
						mainChapter = new NLH();
						parsedNLH.add(mainChapter);
						name = "";
						getName = true;
					}
				if(node.hasAttr("class")){
					if(node.attr("class").equals("defa")){
						text = "";
						recordText = true;
						added = false;
					}else{
						if(!added){
							chapter.addText(text);
							mainChapter.addText(text);
							added = true;
						}
						recordText = false;
					}
				}
				if(node.getClass() == TextNode.class){
					if(recordText)
						text+= ((TextNode) node).text();
					if(getName)
						name+=((TextNode)node).text();
				}
			}
			public void tail(Node node, int depth) {
				if(node instanceof Element){
					if(((Element)node).tagName().equalsIgnoreCase("H3")){
						chapter.setChapter(name);
						getName = false;
					}
					if(((Element)node).tagName().equalsIgnoreCase("H2")){
						mainChapter.setChapter(name);
						getName = false;
					}
				}
			}
		});
	}

	public ArrayList<NLH> getParsedNLHs() {
		return parsedNLH;
	}
}
