package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utility {
	public static String readFile(String fileName){
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
}
