package somepackage;
import java.util.HashSet;
import java.util.Set;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.util.Version;


public class CaseReader {

   
	
	public static Case[] readCases(){
		Case[] returns = new Case[8];
		for(int i = 1; i<9;i++){
			String file = readFile("Data/Cases/Case"+i);
			returns[i-1] = new Case(file);
		}
		return returns;
	}
	private static String readFile(String fileName) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(fileName));
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine+" ");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static Case[] parseCases(Case[] cases){
		return cases;
		
	}
}
