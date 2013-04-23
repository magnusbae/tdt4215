package gui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.swt.widgets.Display;

import parser.CaseReader;
import datatypes.Case;

import search.IndexFiles;
import search.SearchFiles;


public class Indexer extends Thread {

	private Display display;
	private boolean testCases = false;

	public Indexer(Display display, boolean testCases){
		this.display = display;
		this.testCases = testCases;
	}


	public void run(){
		MainWindow m = new MainWindow();
		PropertyConfigurator.configure("lib/jena-log4j.properties");
		Case[] cases = CaseReader.readCases();
		Directory dirICD10;
		Directory dirAtc;
		Directory dirNLH;
		Analyzer ana = new NorwegianAnalyzer(Version.LUCENE_CURRENT);
		try {
			dirICD10 = new SimpleFSDirectory(new File("Index/icd10"));
			dirAtc = new SimpleFSDirectory(new File("Index/atc"));
			dirNLH = new SimpleFSDirectory(new File("Index/NLH"));
			final IndexFiles index = new IndexFiles(dirICD10,dirAtc,dirNLH, ana);
			File f = new File("Index");
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					MainWindow m = new MainWindow();
					m.progressBar.setSelection(10);
				}
			});
			if (!(f.exists() && f.isDirectory())){
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						MainWindow m = new MainWindow();
						m.resultText.setText("Building index, might take a while");
					}
				});


				final int maximum = 94;
				new Thread() {
					public void run() {
						for (final int[] i = new int[1]; i[0] <= maximum; i[0]++) {
							try {Thread.sleep (150);} catch (Throwable th) {}
							if (display.isDisposed()) return;
							display.asyncExec(new Runnable() {
								MainWindow m = new MainWindow();
								public void run() {
									if (m.progressBar.isDisposed ()) return;
									m.progressBar.setSelection(i[0]);
								}
							});
						}
					}
				}.start();

				Thread t = new Thread(){
					public void run(){
						index.index();
					}
				};
				t.start();
				while(t.isAlive()){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}		
			}
			display.asyncExec(new Runnable() {
				MainWindow m = new MainWindow();
				public void run() {
					if (m.progressBar.isDisposed ()) return;
					m.progressBar.setSelection(95);
				}
			});

			SearchFiles sf = new SearchFiles();
			int caseNum = 0;

			String searchResults = "";

			if(testCases){
				for(Case c:cases){
					//			Case c = cases[0];
					//		for(String s:c.getSentences()){
					caseNum++;
					String s = c.getCaseText();
					searchResults += "Case nr. " + caseNum + "\n"; 
					searchResults += search(s, sf, m.showStats, ana, dirNLH, m, caseNum);
				}
			}else{
				searchResults += search(m.getSearchText(), sf, false, ana, dirNLH, m, -1);
			}
			final String sr = searchResults;
			display.asyncExec(new Runnable(){
				MainWindow m = new MainWindow();
				@Override
				public void run(){
					m.progressBar.setSelection(99);
					m.resultText.setText(sr);
					m.showProgressBar(false);
				}
			});
			dirAtc.close();
			dirICD10.close();
			dirNLH.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private static String search(String s, SearchFiles sf, boolean showPres, Analyzer ana, Directory dirNLH, MainWindow m, int caseNum) {
		String[] case1 = {"T3.1", "L3.1.1"};
		String[] case2 = {"T10.2", "T10.2.2"};
		String[] case3 = {"T1.10"};
		String[] case4 = {"T8.3", "T8.3.2", "T8.3.1"};
		String[] case5 = {"T2.2.1.1", "T4.1", "T4.1.1"};
		String[] case6 = {"T11.3.2", "T11.3.2", "T11.3.2.2", "T1.3"};
		String[] case7 = {"T21", "T21.1.1", "T21.1.1.1", "L20.1.2.3"};
		String[] case8 = {"T11.3.2", "T11.3.2.1", "T11.3.2.2", "T1.3"};
		String[][] goldStandard = {case1, case2, case3, case4, case5, case6, case7, case8};

		String searchResults = "";
		Document[] doc = sf.Search(s, dirNLH, ana, m.numberOfSearchResults);
		//				sf.Search(s, dirICD10, ana);
		//				sf.Search(s, dirAtc, ana);
		int right = 0;
		if(caseNum != -1)
			right = checkAnswer(doc, goldStandard[caseNum-1]);
		if (doc != null){
			for (int i = 0; i < doc.length; i++){
				searchResults += doc[i].get("Chapter") + " -" + doc[i].get("Name") + "\n";
			}
			searchResults += "\n------\n";
			if(showPres && caseNum != -1){
				searchResults+="Precision: ";
				searchResults += precision(right, doc.length);
				searchResults += "\nRecall: ";
				searchResults += recall(right, goldStandard[caseNum-1].length);
				searchResults += "\n------\n";
			}
		}else{
			searchResults += "No results found\n------\n";
		}
		return searchResults;
	}



	public static int checkAnswer(Document[] answers, String[] goldStandard){
		int result = 0;
		for(Document d:answers)
			for(String s:goldStandard)
				if(d.get("Chapter").equals(s))
					result++;
		return result;

	}
	public static float precision(int right, int total){
		return ((float)right)/((float)total);
	}
	public static float recall(int right, int total){
		return ((float)right)/((float)total);
	}
	public static void main(String[] args) {
		PropertyConfigurator.configure("lib/jena-log4j.properties");

		Case[] cases = CaseReader.readCases();
		Directory dirICD10;
		Directory dirAtc;
		Directory dirNLH;
		Analyzer ana = new NorwegianAnalyzer(Version.LUCENE_CURRENT);
		boolean showPr = true;
		float best1=-1, bestRe = 0;
		float bestj = 0, bestk=0;
		try {
			dirICD10 = new SimpleFSDirectory(new File("Index/icd10"));
			dirAtc = new SimpleFSDirectory(new File("Index/atc"));
			dirNLH = new SimpleFSDirectory(new File("Index/NLH"));
			final IndexFiles index = new IndexFiles(dirICD10,dirAtc,dirNLH, ana);
			index.index();

			
			for(float i = 0; i<3;i+= 0.5)
				for(float j = 0; j<3;j+= 0.5)
					for(float k = 0; k<3;k+= 0.5){
						SearchFiles sf = new SearchFiles(i,j,k);
						int caseNum = 0;
						float pres = 0;
						for(Case c:cases){
							//			Case c = cases[0];
							//		for(String s:c.getSentences()){
							caseNum++;
							String s = c.getCaseText();
							float[] f = searchPres(s, sf, showPr, ana, dirNLH, null, caseNum);
							pres += f[0]/8;
						}
						if(pres> bestRe){
							bestRe = pres;
							best1 = i;
							bestk = k;
							bestj = j;
						}
					}
			dirAtc.close();
			dirICD10.close();
			dirNLH.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Best recall: " + bestRe);
	}


	private static float[] searchPres(String s, SearchFiles sf, boolean showPr,
			Analyzer ana, Directory dirNLH, Object object, int caseNum) {
		float[] results = new float[2];
		String[] case1 = {"T3.1", "L3.1.1"};
		String[] case2 = {"T10.2", "T10.2.2"};
		String[] case3 = {"T1.10"};
		String[] case4 = {"T8.3", "T8.3.2", "T8.3.1"};
		String[] case5 = {"T2.2.1.1", "T4.1", "T4.1.1"};
		String[] case6 = {"T11.3.2", "T11.3.2", "T11.3.2.2", "T1.3"};
		String[] case7 = {"T21", "T21.1.1", "T21.1.1.1", "L20.1.2.3"};
		String[] case8 = {"T11.3.2", "T11.3.2.1", "T11.3.2.2", "T1.3"};
		String[][] goldStandard = {case1, case2, case3, case4, case5, case6, case7, case8};

		String searchResults = "";
		Document[] doc = sf.WeightedSearch(s, dirNLH, ana, 4);
		//				sf.Search(s, dirICD10, ana);
		//				sf.Search(s, dirAtc, ana);
		int right = checkAnswer(doc, goldStandard[caseNum-1]);
		results[0] = recall(right, goldStandard[caseNum-1].length);
		results[1] = precision(right, doc.length);
		return results;
	}
}

