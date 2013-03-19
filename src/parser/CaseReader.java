package parser;

import datatypes.Case;

public class CaseReader {
	public static void main(String[] args) {
		Case[] cases = readCases();
		cases = parseCases(cases);
			
	}
	
	public static Case[] readCases(){
		Case[] returns = new Case[8];
		for(int i = 1; i<9;i++){
			String file = Utility.readFile("Data/Cases/Case"+i);
			returns[i-1] = new Case(file);
		}
		return returns;
	}
	public static Case[] parseCases(Case[] cases){
		return cases;
		
	}
}
