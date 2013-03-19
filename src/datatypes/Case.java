package datatypes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import snowball.ext.norwegianStemmer;

public class Case {
	String caseText = "";
	ArrayList<String> sentences = new ArrayList<String>();
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
			String sentence = "";
			if ((!Character.isLetter(ch) && !Character.isDigit(ch))) {
				boolean sentenceStop = false;
				if(!Character.isWhitespace(ch)){
					sentenceStop = true;
				}
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
						String current = stemmer.getCurrent();
							if(!sentenceStop){
								sentence += current + " . ";
								this.caseText += current + " . ";
								
							}else {
								sentence += current;
								sentences.add(sentence);
								sentence = "";
								this.caseText += current + " : ";
							}
						input="";
					}
				}
			} else {
				input += Character.toLowerCase(ch);
			}
		}
	}
}
