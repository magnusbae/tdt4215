package gui;

public class TextRunner implements Runnable {
	private String found;
	
	public String getText(){
		return found;
	}
	
	@Override
	public void run() {
		MainWindow m = new MainWindow();
		found = m.searchBox.getText();
	}
}
