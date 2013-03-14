package parser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;

public class TestXMLStripperTest {
	
	
	@Test
	public void testStripAllXML() throws Exception{
	
		File f = new File("test/parser/T1.6.htm");
		BufferedReader r = new BufferedReader(new FileReader(f));
		String parse = "";
		
		
		while(!r.ready()){}
		String read = r.readLine();
		while(read != null){
			parse += read;
			read = r.readLine();
		}
		XMLStripper stripper = new XMLStripper();
		String parsed = stripper.stripAllXML(parse);
		System.out.println(parsed);
	}
}
