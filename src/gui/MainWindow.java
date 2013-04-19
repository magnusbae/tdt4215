package gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class MainWindow {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		shell.setSize(620, 482);
		shell.setText("SWT Application");
		
		Button btnSk = new Button(shell, SWT.NONE);
		btnSk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				//TODO search!
			}
		});
		btnSk.setBounds(10, 86, 420, 28);
		btnSk.setText("Søk");
		
		StyledText searchBox = new StyledText(shell, SWT.BORDER);
		searchBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//TODO search on control + enter
			}
		});
		searchBox.addSelectionListener(new SelectionAdapter() {
			
			/*
			 * Remove text on click.
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
//				TODO Remove text
			}
		});
		
		
		
		searchBox.setText("Skriv eller lim inn søketekst her");
		searchBox.setBounds(10, 10, 584, 72);
		
		StyledText styledText_1 = new StyledText(shell, SWT.BORDER);
		styledText_1.setText("Resultat");
		styledText_1.setEditable(false);
		styledText_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		styledText_1.setBounds(10, 120, 584, 304);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.setBounds(437, 86, 157, 28);
		btnNewButton.setText("Søk i eksempelcaser");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
