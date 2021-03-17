
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class Main implements JmmParser {

	public static List<Report> reports;

	public JmmParserResult parse(String jmmCode) {
		
		try {
			reports = new ArrayList<>();

		    Parser myParser = new Parser(new StringReader(jmmCode));
    		SimpleNode root = myParser.Program(); // returns reference to root node
            	
    		root.dump(""); // prints the tree on the screen
			String jsonTree = root.toJson();
			File jsonFile = new File("javacc/output.json");
			try
			{
				FileWriter writer = new FileWriter(jsonFile);
				writer.write(jsonTree);
				writer.close();
			}
			catch (IOException e)
			{
				System.out.println(e.toString());
			}

    		return new JmmParserResult(root, reports);
		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}
	}

    public static void main(String[] args) {
		var main = new Main();
		var fileContents = SpecsIo.read(args[0]);
		main.parse(fileContents);

        if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }
    }


}