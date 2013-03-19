package Datatypes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import snowball.ext.norwegianStemmer;

public class Case {
	String caseText; 
	public Case(String caseText){
		ArrayList<String> stopWords = new ArrayList<String>();
		BufferedReader br = null;
		try {
			String s;
			br = new BufferedReader(new FileReader("Data/norwegianStopwords.txt"));
			while ((s = br.readLine()) != null) {
				stopWords.add(s);
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
		norwegianStemmer stemmer = new norwegianStemmer();

		String input = "";
		for(char ch:caseText.toCharArray()){
			if ((!Character.isLetter(ch) && !Character.isDigit(ch)) || Character.getType(ch) == 9) {
				if (input.length() > 0) {
					boolean stopword = false;
					for(String word:stopWords)
						if(word.equals(input)){
							stopword = true;
							input="";
							break;
						}

					if(!stopword){
						stemmer.setCurrent(input);
						stemmer.stem();
							if(this.caseText == null){
								this.caseText = stemmer.getCurrent();
							}else{
								this.caseText += " . " + stemmer.getCurrent();
							}
						input="";
					}
				}
			} else {
				input += Character.toLowerCase(ch);
			}
		}
		System.out.println(this.caseText);
	}
}
