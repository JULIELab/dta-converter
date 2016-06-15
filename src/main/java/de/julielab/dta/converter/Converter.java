package de.julielab.dta.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasCreationUtils;

import de.julielab.jcore.reader.dta.util.DTAUtils;
import de.julielab.jcore.types.extensions.dta.DocumentClassification;

public class Converter {

	static void readDocument(final File inputFile, final File outputFile,
			final boolean normalize,
			final Map<String, List<String>> file2classes) throws Exception {
		final CollectionReader reader = DTAUtils.getReader(
				inputFile.getCanonicalPath(), normalize);
		final CAS cas = CasCreationUtils
				.createCas((AnalysisEngineMetaData) reader.getMetaData())
				.getJCas().getCas();
		reader.getNext(cas);
		final JCas jcas = cas.getJCas();
		if (jcas.getDocumentText() == null)
			throw new Exception("File " + inputFile + " has not document text!");

		if (file2classes != null) {
			final List<String> classes = new ArrayList<>();
			final FSIterator<Annotation> it = jcas.getAnnotationIndex(
					DocumentClassification.type).iterator();
			while (it.hasNext()) {
				final DocumentClassification classification = (DocumentClassification) it
						.next();
				classes.add(classification.getClassification());
			}
			if (file2classes.containsKey(inputFile.getName()))
				throw new Exception("Can not process file "
						+ inputFile.getCanonicalPath() + " twice!");
			file2classes.put(inputFile.getName(), classes);
		}

		try (BufferedWriter outFile = new BufferedWriter(new FileWriter(
				outputFile))) {
			outFile.write(jcas.getDocumentText());
		}
	}

}
