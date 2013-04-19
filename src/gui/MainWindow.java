package gui;

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


public class MainWindow {

	private static boolean textPreSet;
	private static StyledText searchBox = null; 
	private static StyledText resultText = null;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
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
		
	    searchBox = new StyledText(shell, SWT.BORDER);
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
		
		resultText = new StyledText(shell, SWT.BORDER);
		resultText.setAlwaysShowScrollBars(true);
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

	private static void search(){
		search(false);
	}
	
	private static void search(boolean cases) {
		String text;
		if(!cases){
			text = searchBox.getText();
		}else{
			//todo read cases;
		}
		//TODO search!
	}
	
	

}
