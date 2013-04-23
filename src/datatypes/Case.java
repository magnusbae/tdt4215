package datatypes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

		String input = "";
		String sentence = "";
		for(char ch:caseText.toCharArray()){
//			System.out.println(ch);
//			System.out.println((int) ch);
			if ((int)ch ==46 || ((!Character.isLetter(ch) && !Character.isDigit(ch) && ch != '/' && ch != '(' && ch != ')' && ch != '%' && ch != ',' ))) {
				boolean sentenceStop = false;
				if(!Character.isWhitespace(ch)){
					sentenceStop = true;
				}else{
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
						String current = input;
						sentence += current + " . ";
						this.caseText += current + " . ";
						input="";
					}
					if(sentenceStop){
						sentences.add(sentence);
						sentence = "";
//						System.out.println("--------------------");
					}
				}

			}else if (!Character.isLetter(ch) && !Character.isDigit(ch))
				input += " . ";
			else {

				input += Character.toLowerCase(ch);
			}
		}
	}
	public String getCaseText() {
		return caseText;
	}
	public void setCaseText(String caseText) {
		this.caseText = caseText;
	}
	public ArrayList<String> getSentences() {
		return sentences;
	}
	public void setSentences(ArrayList<String> sentences) {
		this.sentences = sentences;
	}
}
