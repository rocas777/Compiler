
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.jasmin.JasminUtils;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;

public class Main implements JmmParser {

	public static List<Report> reports;
	public static List<Report> semanticReports;
	public static boolean dumpTree = false;

	public JmmParserResult parse(String jmmCode) {
		
		try {
			reports = new ArrayList<>();
			semanticReports = new ArrayList<>();

		    Parser myParser = new Parser(new StringReader(jmmCode));
    		SimpleNode root = myParser.Program(); // returns reference to root node
            	
    		if (dumpTree) root.dump(""); // prints the tree on the screen
			

    		return new JmmParserResult(root, reports);
		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}
	}

    public static void main(String[] args) {
		boolean shouldOptimizeWithOptionO = false;
		String pathArg = "";

		for (String arg : args) {
			if (arg.equals("-o")) shouldOptimizeWithOptionO = true;
			else pathArg = arg;
		}

		var main = new Main();
		var fileContents = SpecsIo.read(pathArg);
		var result = main.parse(fileContents);
		
		var analysis = new AnalysisStage();
		var result2 = analysis.semanticAnalysis(result);
		var optimizer = new OptimizationStage();
		if (shouldOptimizeWithOptionO) optimizer.optimize(result2);
		var result3 = optimizer.toOllir(result2);
		var backend = new BackendStage();
		var result4 = backend.toJasmin(result3);

		result4.writeJasminFileToProjRoot();
		//result4.run();

		var table = result2.getSymbolTable();
		String className = table.getClassName();

		String jsonTree = result.toJson();
		File jsonFile = new File(className + ".json");
		SpecsIo.write(jsonFile, jsonTree);

		String tableData = table.print();
		File tableFile = new File(className + ".table.txt");
		SpecsIo.write(tableFile, tableData);

		String ollirCode = result3.getOllirCode();
		File ollirFile = new File(className + ".ollir");
		SpecsIo.write(ollirFile, ollirCode);

		File jasminFile = new File(className + ".j");
		JasminUtils.assemble(jasminFile, new File("").getAbsoluteFile());

        if (args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }
    }


}