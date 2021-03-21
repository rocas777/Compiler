
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
	public static boolean dumpTree = true;

	public JmmParserResult parse(String jmmCode) {
		
		try {
			reports = new ArrayList<>();

		    Parser myParser = new Parser(new StringReader(jmmCode));
    		SimpleNode root = myParser.Program(); // returns reference to root node
            	
    		if (dumpTree) root.dump(""); // prints the tree on the screen
			

    		return new JmmParserResult(root, reports);
		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}
	}

    public static void main(String[] args) {
		var main = new Main();
		var fileContents = SpecsIo.read(args[0]);
		var result = main.parse(fileContents);

		if (dumpTree)
		{
			String jsonTree = result.toJson();
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
		}

        if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }
    }


}