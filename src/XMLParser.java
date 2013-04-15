import java.io.BufferedReader;import java.io.File;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.FileReader;import java.io.IOException;import java.io.PrintWriter;import java.util.ArrayList;import java.util.StringTokenizer;import org.jdom.Document;import org.jdom.Element;import org.jdom.JDOMException;import org.jdom.input.SAXBuilder;/** * Klassen leser konfigurasjonsfila og lager xml til konfigurasjonsfila */public class XMLParser {	private boolean isFileMade = false;	private File oldFile = new File("tokenized.xml");	HtmlStripper hs;	private boolean fromUrl;	/**	 * konstruktoren til klassen Den tar inn konfirurasjonsfila	 * @param file File configfilen	 * @param boolean fromUrl Sjekker om inputfilene skal komme fra fil eller fra nett	 */	public XMLParser(File file, boolean fromUrl) {		this.fromUrl = fromUrl;		readXML(file);	}	/**	 * Metoden leser innholdet i konfigurasjonsfilen(xml filen)	 * @param file String	 */	public void readXML(File file) {		SAXBuilder myBuilder = new SAXBuilder();		Document myDocument = null;		try {			myDocument = myBuilder.build(file);		}		catch (JDOMException jde) {			jde.printStackTrace();		} catch (IOException e) {			e.printStackTrace();		}		Element root = myDocument.getRootElement();		Element collection = root.getChild("collection");		Element uri = collection.getChild("uri");		Element directory = collection.getChild("directory");		Element tokenizer = root.getChild("tokenizer");		Element sentenceboundary = tokenizer.getChild("sentenceboundary");		Element paragraphboundary = tokenizer.getChild("paragraphboundary");		Element allsmall = tokenizer.getChild("allsmall");		String urls = uri.getText();		String directoryText = directory.getText().trim();		String sBoundary = sentenceboundary.getText();		String pBoundary = paragraphboundary.getText();		String aSmall = allsmall.getText();		if (fromUrl) {			ArrayList uriList = parseUri(urls);			for (int i = 0; i < uriList.size(); i++) {				hs = new HtmlStripper((String) uriList.get(i), sBoundary,						pBoundary, aSmall, this);			}		}		else {			hs = new HtmlStripper(directoryText, this, sBoundary, pBoundary); // Denne kj�res dersom det skal hentes fra fil		}	}	/**	 * Metoden finner alle urlene som ble tatt inn i konfigurasjonsfilen	 * @param urls String	 * @return ArrayList	 */	public ArrayList parseUri(String urls) {		StringTokenizer st = new StringTokenizer(urls);		ArrayList uriList = new ArrayList();		while (st.hasMoreTokens()) {			uriList.add(st.nextToken());		}		return uriList;	}	/**	 * Metoden lager tokenize.xml fila som skal sendes videre til med xml-fila	 * @param title String	 * @param url String	 * @param body String	 * @throws java.io.FileNotFoundException	 * @throws java.lang.SecurityException	 */	public void makeXmlTokenizer(String title, String url, String body)			throws FileNotFoundException, SecurityException {		String fileContent = "";		try {			fileContent = readFile(oldFile);		} catch (IOException e) {			e.printStackTrace();		}		String encoding = "ISO-8859-1";		fileContent = fileContent				.replaceAll("<\\?xml version=\"1.0\" encoding=\"" + encoding						+ "\"\\?>", "");		fileContent = fileContent.replaceAll("<tokenized>", "");		fileContent = fileContent.replaceAll("</tokenized>", "");		PrintWriter out = new PrintWriter(new FileOutputStream("tokenized.xml"));		out.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");		out.println("<tokenized>");		out.println(fileContent);		out.println("<doc id = \"" + url + "\">");		out.println("<title>");		out.println(title);		out.println("</title>");		out.println("<body>");		out.println(body);		out.println("</body>");		out.println("</doc>");		out.println("</tokenized>");		out.flush();		out.close();	}	/**	 * Metoden leser innholdet i fila og lagrer det i en string	 * @param filename File	 * @return String	 * @throws java.io.IOException	 */	public String readFile(File filename) throws IOException {		BufferedReader in = new BufferedReader(new FileReader(filename));		String file = "";		String temp;		while ((temp = in.readLine()) != null) {			file += temp;			file += " ";		}		in.close();		return file;	}}