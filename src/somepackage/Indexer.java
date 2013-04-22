package somepackage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.swt.widgets.Display;


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
					System.out.println("");
					System.out.println("Case : " + caseNum);
					String s = c.getCaseText();
					Document[] doc = sf.Search(s, dirNLH, ana);
					//				sf.Search(s, dirICD10, ana);
					//				sf.Search(s, dirAtc, ana);
					searchResults += "Case nr. " + caseNum + "\n"; 
					if (doc != null){
						
						for (int i = 0; i < (doc.length >= 4 ? 4 : doc.length); i++){
							searchResults += doc[i].get("Chapter") + " -" + doc[i].get("Name") + "\n";
						}
						searchResults += "\n------\n";
					}else{
						searchResults += "No results found\n------\n";
					}
				}
			}else{
				String s = m.getSearchText();
				Document[] doc = sf.Search(s, dirNLH, ana);
				if (doc != null && doc.length > 0){
					for (int i = 0; i < (doc.length >= 4 ? 4 : doc.length); i++){
						searchResults += doc[i].get("Chapter") + " -" + doc[i].get("Name") + "\n";
					}
					searchResults += "\n------\n";
				}else{
					searchResults += "No results found\n------\n";
				}
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
}

