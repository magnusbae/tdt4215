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
import org.eclipse.swt.widgets.Decorations;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;


public class MainWindow {

	 static boolean textPreSet;
	 static StyledText searchBox = null; 
	 static StyledText resultText = null;
	 static ProgressBar progressBar = null;
	 static Display display = null;
	 static Shell shell = null;
	 static int numberOfSearchResults = 4;
	 static boolean showStats = false;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(620, 482);
		shell.setText("NLH Searcher");
		
		Button btnSk = new Button(shell, SWT.NONE);
		btnSk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				search();
			}
		});
		btnSk.setBounds(10, 86, 228, 28);
		btnSk.setText("Search");
		
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
		searchBox.setText("Write or paste search text here");
		textPreSet = true;
		searchBox.selectAll();
		searchBox.setBounds(10, 10, 584, 72);
		
		resultText = new StyledText(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		resultText.setText("Result");
		resultText.setEditable(false);
		resultText.setBounds(10, 143, 584, 281);
		
		Button searchTestCases = new Button(shell, SWT.NONE);
		searchTestCases.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				search(true);
			}
		});
		searchTestCases.setBounds(244, 86, 170, 28);
		searchTestCases.setText("Search example cases");
		
		Spinner spinner = new Spinner(shell, SWT.BORDER);
	
		spinner.setMaximum(10);
		spinner.setMinimum(1);
		spinner.setSelection(4);
		spinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Spinner s = (Spinner)e.getSource();
				int a = s.getSelection();
				if (a >= s.getMinimum() && a <= s.getMaximum()){
					numberOfSearchResults = a;
				}
				
			}
		});
		spinner.setBounds(543, 88, 51, 22);
		
		Label lblNumberOfResults = new Label(shell, SWT.NONE);
		lblNumberOfResults.setAlignment(SWT.RIGHT);
		lblNumberOfResults.setBounds(420, 93, 117, 14);
		lblNumberOfResults.setText("Number of results:");
		
		Button btnShowRecallAnd = new Button(shell, SWT.CHECK);
		btnShowRecallAnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showStats = ((Button)e.getSource()).getSelection();
			}
		});
		btnShowRecallAnd.setBounds(244, 113, 350, 18);
		btnShowRecallAnd.setText("Show recall and precision for example cases");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	static void showProgressBar(boolean b) {
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
	
	static String getSearchText(){
		TextRunner sr = new TextRunner();
		display.syncExec(sr);
		return sr.getText();
	}

	private static void search(){
		search(false);
	}
	
	private static void search(boolean testCases){
		showProgressBar(true);
		Indexer i = new Indexer(display, testCases);
		i.start();
	}
}
