package de.julielab.dta.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasCreationUtils;

import com.google.common.base.Joiner;

import de.julielab.jcore.reader.dta.util.DTAUtils;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import de.julielab.jcore.types.extensions.dta.DTAClassification;
import de.julielab.jcore.types.extensions.dta.Header;

public class Converter {
	private static final Joiner SC_JOINER = Joiner.on(";");

	static final String asSortedString(final StringArray sa) {
		final String[] s = new String[sa.size()];
		sa.copyToArray(0, s, 0, sa.size());
		Arrays.sort(s);
		for (int i = 0; i < s.length; ++i)
			s[i] = s[i].replaceAll(";", ",");
		return SC_JOINER.join(s);
	}

	static void readDocument(final File inputFile, final File outputFile,
			final Mode mode, final FileWriter metas, final boolean format2017) throws Exception {
		final CollectionReader reader = DTAUtils.getReader(
				inputFile.getCanonicalPath(), mode.equals(Mode.NORMALIZE), format2017);
		final CAS cas = CasCreationUtils
				.createCas((AnalysisEngineMetaData) reader.getMetaData())
				.getJCas().getCas();
		reader.getNext(cas);
		final JCas jcas = cas.getJCas();

		//happens for non german texts
		if (jcas.getDocumentText() == null)
			return;

		if (metas != null) {
			String dtaClass = null, dtaSubClass = null;
			final FSIterator<Annotation> it = jcas.getAnnotationIndex(
					DTAClassification.type).iterator();
			if (it.hasNext()) {
				final DTAClassification classification = (DTAClassification) it
						.next();
				dtaClass = classification.getClassification();
				dtaSubClass = classification.getSubClassification();
			}
			final Header header = (Header) jcas.getAnnotationIndex(Header.type)
					.iterator().next();
			String year = header.getYear();
			String pubPlaces = asSortedString(header.getPublicationPlaces());
			String publisher = asSortedString(header.getPublishers());

			metas.write(new MetaInformation(CLI.removeEnd(inputFile.getName()),
					dtaClass, dtaSubClass, year, pubPlaces, publisher).toCSV()
					+ "\n");
		}

		String text = null;
		switch (mode) {
		case NORMALIZE:
			text = jcas.getDocumentText();
			break;
		case LEMMATIZE:
			final StringBuilder sb = new StringBuilder(jcas.getDocumentText()
					.length());
			final Iterator<Sentence> it = JCasUtil.iterator(jcas,
					Sentence.class);
			while (it.hasNext()) {
				final Sentence s = it.next();
				for (final Token t : JCasUtil.selectCovered(jcas, Token.class,
						s))
					sb.append(t.getLemma().getValue() + " ");
				if (it.hasNext())
					sb.replace(sb.length() - 1, sb.length(), "\n");
				else
					sb.replace(sb.length() - 1, sb.length(), "");
			}
			text = sb.toString();
			break;
		default:
			throw new IllegalArgumentException("Mode " + mode
					+ " not supported");
		}

		try (BufferedWriter outFile = new BufferedWriter(new FileWriter(
				outputFile))) {
			if (text == null)
				throw new IllegalArgumentException("Mode " + mode
						+ " not supported");
			outFile.write(text);
		}
	}
}