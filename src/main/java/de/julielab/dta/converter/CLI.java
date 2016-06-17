package de.julielab.dta.converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class CLI {
	static List<File> getInputFiles(final String input) {
		final List<File> inputFiles = new ArrayList<>();
		final File inputPath = new File(input);
		if (!inputPath.exists())
			throw new IllegalArgumentException(input + " does not exist!");
		if (inputPath.isFile())
			inputFiles.add(inputPath);
		else
			for (final File f : inputPath.listFiles()) {
				if (f.isDirectory() || !f.getName().endsWith(".tcf.xml"))
					throw new IllegalArgumentException(
							f
									+ " can not be parsed as tcf.xml, yet is contained in "
									+ input);
				inputFiles.add(f);
			}
		return inputFiles;
	}

	static List<File> getOutputFiles(final String output,
			final List<File> inputFiles) throws IOException {
		final File outputPath = new File(output);
		if (!outputPath.exists())
			outputPath.mkdirs();
		if (outputPath.isDirectory() && (outputPath.listFiles().length != 0))
			throw new IllegalArgumentException(output + " is not empty!");
		final List<File> outputFiles = new ArrayList<>();
		for (final File f : inputFiles) {
			String name = removeEnd(f.getName());
			outputFiles.add(new File(output, name));
		}
		return outputFiles;
	}

	public static void main(final String... args) throws Exception {
		final CLI cli = new CLI();
		final JCommander jc = new JCommander(cli);
		try {
			jc.parse(args);
		} catch (final ParameterException e) {
			jc.usage();
		}
		cli.run();
	}

	static FileWriter writeMetaInformation(final File metaFile)
			throws IOException {
		if (metaFile.exists())
			throw new IllegalArgumentException(metaFile + " already exists!");
		metaFile.getAbsoluteFile().getParentFile().mkdirs();
		FileWriter fw = new FileWriter(metaFile);
		fw.write(MetaInformation.CSV_HEADER + "\n");
		return fw;
	}

	@Parameter(names = { "--inputpath", "-i" }, required = true, description = "Path with input")
	String input;

	@Parameter(names = { "--outputpath", "-o" }, required = true, description = "Path to store output")
	String output;

	@Parameter(names = { "--metafile", "-m" }, required = true, description = "File to store meta information")
	String meta;

	@Parameter(names = { "--normalize", "-n" }, required = false, arity = 1, description = "Normalize input")
	boolean normalize = true;

	private void run() throws Exception {
		final List<File> inputFiles = getInputFiles(input);
		final List<File> outputFiles = getOutputFiles(output, inputFiles);
		try (FileWriter metaInformation = writeMetaInformation(new File(meta))) {
			for (int i = 0; i < inputFiles.size(); ++i)
				Converter.readDocument(inputFiles.get(i), outputFiles.get(i),
						normalize, metaInformation);
		}
	}
	
	static String removeEnd(String name){
		return name.substring(0, name.lastIndexOf(".tcf.xml"));
	}
}
