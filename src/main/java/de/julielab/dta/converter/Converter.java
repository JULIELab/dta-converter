package de.julielab.dta.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import com.google.common.base.Joiner;

import de.julielab.jcore.reader.dta.util.DTAUtils;
import de.julielab.jcore.types.extensions.dta.DTABelletristik;
import de.julielab.jcore.types.extensions.dta.DWDS1Belletristik;
import de.julielab.jcore.types.extensions.dta.DWDS2Belletristik;
import de.julielab.jcore.types.extensions.dta.DocumentClassification;
import de.julielab.jcore.utility.JCoReTools;

public class Converter {
	private static final Joiner WHITE_SPACE_JOINER = Joiner.on(" ");
	private static final String DESCRIPTOR = null;
	
	private static void transformFile(String[] args) throws Exception {
		boolean belle = Boolean.valueOf(args[0]);
		String outputFile = "/home/hellrich/Schreibtisch/BelletristikWindow5-" + args[0];
		CollectionReader reader = JCoReTools.getCollectionReader("src/main/resources/jcore-dta-reader.xml");
		CAS cas = CasCreationUtils.createCas((AnalysisEngineMetaData) reader.getMetaData()).getJCas().getCas();
		try (BufferedWriter outFile = new BufferedWriter(new FileWriter(outputFile))) {
			while (reader.hasNext()) {
				cas.reset();
				reader.getNext(cas);
				JCas jcas = cas.getJCas();
				if (jcas.getDocumentText() == null)
					continue;
				if ((belle && DTAUtils.hasAnyClassification(jcas, DTABelletristik.class, DWDS1Belletristik.class,
						DWDS2Belletristik.class))
						|| (!belle && !DTAUtils.hasAnyClassification(jcas, DTABelletristik.class,
								DWDS1Belletristik.class, DWDS2Belletristik.class)))
					for (List<String> sentence : DTAUtils.slidingSymetricWindow(jcas, 5))
						outFile.write(WHITE_SPACE_JOINER.join(sentence) + "\n");
			}
		}
	}
	
	
	
	static void readDocument(File inputFile, File outputFile, boolean normalize, Map<String,List<String>> file2classes) throws Exception{
		CollectionReader reader = DTAUtils.getReader(inputFile.getCanonicalPath(), normalize);
		CAS cas = CasCreationUtils.createCas((AnalysisEngineMetaData) reader.getMetaData()).getJCas().getCas();
		reader.getNext(cas);
		JCas jcas = cas.getJCas();
		if (jcas.getDocumentText() == null)
			throw new Exception("File "+inputFile+" has not document text!");

		if(file2classes != null){
			List<String> classes = new ArrayList<>();
			final FSIterator<Annotation> it = jcas.getAnnotationIndex(DocumentClassification.type).iterator();
			while (it.hasNext()) {
				final DocumentClassification classification = (DocumentClassification) it.next();
				classes.add(classification.getClassification());
			}
			if(file2classes.containsKey(inputFile.getName()))
				throw new Exception("Can not process file "+inputFile.getCanonicalPath()+" twice!");
			file2classes.put(inputFile.getName(), classes);
		}
		
		try (BufferedWriter outFile = new BufferedWriter(new FileWriter(outputFile))) {
			outFile.write(jcas.getDocumentText());
		}
	}
	
	
}
