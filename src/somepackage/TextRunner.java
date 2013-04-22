package somepackage;
import java.util.HashSet;
import java.util.Set;

public class TextRunner implements Runnable {
/**
    * <pre>
    *           0..*     0..*
    * TextRunner ------------------------- MainWindow
    *           textRunner        &lt;       mainWindow
    * </pre>
    */
   private Set<MainWindow> mainWindow;
   
   public Set<MainWindow> getMainWindow() {
      if (this.mainWindow == null) {
         this.mainWindow = new HashSet<MainWindow>();
      }
      return this.mainWindow;
   }
   
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
