package somepackage;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Case {
/**
    * <pre>
    *           0..*     0..1
    * Case ------------------------- CaseReader
    *           case        &lt;       caseReader
    * </pre>
    */
   private CaseReader caseReader;
   
   public void setCaseReader(CaseReader value) {
      this.caseReader = value;
   }
   
   public CaseReader getCaseReader() {
      return this.caseReader;
   }
   
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
						String current = input;
							if(!sentenceStop){
								sentence += current + " . ";
								this.caseText += current + " . ";
								
							}else {
								sentence += current;
								sentences.add(sentence);
								sentence = "";
								this.caseText += current + "  ";
							}
						input="";
					}
				}
			} else {
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
