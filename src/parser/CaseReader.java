package parser;

public class CaseReader {
	public static void main(String[] args) {
		String[] cases = readCases();
		cases = parseCases(cases);
		for(String s:cases)
			System.out.println(s);
	}
	
	public static String[] readCases(){
		String[] returns = new String[8];
		for(int i = 1; i<9;i++){
			String file = Utility.readFile("Data/Case"+i);
			returns[i-1] = file;
		}
		return returns;
	}
	public static String[] parseCases(String[] cases){
		return cases;
		
	}
}
