package de.julielab.dta.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasCreationUtils;

import de.julielab.jcore.reader.dta.util.DTAUtils;
import de.julielab.jcore.types.extensions.dta.DTAClassification;
import de.julielab.jcore.types.extensions.dta.Header;

public class Converter {

	static void readDocument(final File inputFile, final File outputFile,
			final boolean normalize, FileWriter metas)
			throws Exception {
		final CollectionReader reader = DTAUtils.getReader(
				inputFile.getCanonicalPath(), normalize);
		final CAS cas = CasCreationUtils
				.createCas((AnalysisEngineMetaData) reader.getMetaData())
				.getJCas().getCas();
		reader.getNext(cas);
		final JCas jcas = cas.getJCas();
		
		//happens for non german texts
		if (jcas.getDocumentText() == null)
			return;

		if (metas != null) {
			String DTAClass = "N.A.", DTASubClass = "N.A.", year = "N.A.";

			final FSIterator<Annotation> it = jcas.getAnnotationIndex(
					DTAClassification.type).iterator();
			if (it.hasNext()) {
				final DTAClassification classification = (DTAClassification) it
						.next();
				DTAClass = classification.getClassification();
				DTASubClass = classification.getSubClassification();
			}

			final Header header = (Header) jcas.getAnnotationIndex(Header.type)
					.iterator().next();
			year = header.getYear();

			metas.write(new MetaInformation(CLI.removeEnd(inputFile.getName()),
					DTAClass, DTASubClass, year).toCSV()+"\n");
		}

		try (BufferedWriter outFile = new BufferedWriter(new FileWriter(
				outputFile))) {
			outFile.write(jcas.getDocumentText());
		}
	}
}
