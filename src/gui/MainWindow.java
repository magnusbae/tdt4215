package gui;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import parser.CaseReader;
import search.IndexFiles;
import search.SearchFiles;
import datatypes.Case;
import org.eclipse.swt.widgets.ProgressBar;


public class MainWindow {

	private static boolean textPreSet;
	private static StyledText searchBox = null; 
	private static StyledText resultText = null;
	private static ProgressBar progressBar = null;
	private static Display display = null;
	private static Shell shell = null;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(620, 482);
		shell.setText("SWT Application");
		
		Button btnSk = new Button(shell, SWT.NONE);
		btnSk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				search();
			}
		});
		btnSk.setBounds(10, 86, 420, 28);
		btnSk.setText("Søk");
		
	    searchBox = new StyledText(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		searchBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (textPreSet){
					((StyledText)e.getSource()).setText("");
					textPreSet = false;
				}
			}
		});
		searchBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.stateMask == SWT.MOD1 && e.keyCode == SWT.CR){ //CR might not be platform independent?
					search();
				}
			}
		});
		
		searchBox.setAlwaysShowScrollBars(true);
		searchBox.setText("Skriv eller lim inn søketekst her");
		textPreSet = true;
		searchBox.selectAll();
		searchBox.setBounds(10, 10, 584, 72);
		
		resultText = new StyledText(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		resultText.setText("Resultat");
		resultText.setEditable(false);
		resultText.setBounds(10, 120, 584, 304);
		
		Button searchTestCases = new Button(shell, SWT.NONE);
		searchTestCases.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				search(true);
			}
		});
		searchTestCases.setBounds(437, 86, 157, 28);
		searchTestCases.setText("Søk i eksempelcaser");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static void showProgressBar(boolean b) {
		if (!b){
			progressBar.moveBelow(resultText);
			progressBar.setVisible(false);
			progressBar.dispose();
		}else{
			progressBar = new ProgressBar(shell, SWT.NONE);
			progressBar.setBounds(73, 260, 442, 14);
			progressBar.setSelection(0);
			progressBar.moveAbove(resultText);
			progressBar.setVisible(true);
		}
	}

	private static void search(){
		search(false);
	}
	
	private static void search(boolean testCases){
		showProgressBar(true);
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
			progressBar.setSelection(10);
			if (!(f.exists() && f.isDirectory())){
				resultText.setText("Building index, might take a while");
				
				final int maximum = 90;
				new Thread() {
					public void run() {
						for (final int[] i = new int[1]; i[0] <= maximum; i[0]++) {
						try {Thread.sleep (100);} catch (Throwable th) {}
							if (display.isDisposed()) return;
							display.asyncExec(new Runnable() {
								public void run() {
								if (progressBar.isDisposed ()) return;
									progressBar.setSelection(i[0]);
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
			progressBar.setSelection(90);
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
							searchResults += doc[i].get("Chapter") + "\n";
						}
						searchResults += "\n------\n";
					}else{
						searchResults += "No results found\n------\n";
					}
				}
			}else{
				String s = new Case(searchBox.getText()).getCaseText();
				Document[] doc = sf.Search(s, dirNLH, ana);
				if (doc != null){
					for (int i = 0; i < (doc.length >= 4 ? 4 : doc.length); i++){
						searchResults += doc[i].get("Chapter") + "\n";
					}
					searchResults += "\n------\n";
				}else{
					searchResults += "No results found\n------\n";
				}
			}
			progressBar.setSelection(99);
			showProgressBar(false);
			resultText.setText(searchResults);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
