package de.julielab.dta.converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Joiner;

public class CLI {

	private static final Joiner SC_JOINER = Joiner.on(";");

	@Parameter(names = { "--inputpath", "-i" }, required = true, description = "Path with input")
	String input;
	@Parameter(names = { "--outputpath", "-o" }, required = true, description = "Path to store output")
	String output;
	@Parameter(names = { "--metafile", "-m" }, required = true, description = "File to store meta information")
	String meta;
	@Parameter(names = { "--normalize", "-n" }, required = false, arity = 1, description = "Normalize input")
	boolean normalize = true;

	public static void main(String... args) throws Exception {
		CLI cli = new CLI();
		JCommander jc = new JCommander(cli);
		try {
			jc.parse(args);
		} catch (ParameterException e) {
			jc.usage();
		}
		cli.run();
	}

	private void run() throws Exception {
		List<File> inputFiles = getInputFiles(input);
		List<File> outputFiles = getOutputFiles(output, inputFiles);
		Map<String, List<String>> file2classes = new HashMap<>();
		for(int i=0;i<inputFiles.size();++i)
			Converter.readDocument(inputFiles.get(i), outputFiles.get(i), normalize, file2classes);
		writeFile2Classes(file2classes, new File(meta));
	}

	static List<File> getInputFiles(String input) {
		List<File> inputFiles = new ArrayList<>();
		File inputPath = new File(input);
		if (!inputPath.exists())
			throw new IllegalArgumentException(input + " does not exist!");
		if (inputPath.isFile())
			inputFiles.add(inputPath);
		else {
			for (File f : inputPath.listFiles()) {
				if (f.isDirectory() || !f.getName().endsWith(".tcf.xml"))
					throw new IllegalArgumentException(
							f
									+ " can not be parsed as tcf.xml, yet is contained in "
									+ input);
				inputFiles.add(f);
			}
		}
		return inputFiles;
	}

	static List<File> getOutputFiles(String output, List<File> inputFiles)
			throws IOException {
		File outputPath = new File(output);
		if (!outputPath.exists())
			outputPath.mkdirs();
		if(outputPath.isDirectory() && outputPath.listFiles().length != 0)
			throw new IllegalArgumentException(output + " is not empty!");
		List<File> outputFiles = new ArrayList<>();
		for (File f : inputFiles) {
			String name = f.getName();
			//fix suffix
			name = name.substring(0, name.lastIndexOf(".tcf.xml")) + ".txt";
			outputFiles.add(new File(output, name));
		}
		return outputFiles;
	}

	static void writeFile2Classes(Map<String, List<String>> file2classes,
			File metaFile) throws IOException {
		if(metaFile.exists())
			throw new IllegalArgumentException(metaFile + " already exists!");
		metaFile.getParentFile().mkdirs();
		try (FileWriter fw = new FileWriter(metaFile)) {
			for (Entry<String, List<String>> e : file2classes.entrySet())
				fw.write(e.getKey() + ";" + SC_JOINER.join(e.getValue())+"\n");
		}
	}

}
