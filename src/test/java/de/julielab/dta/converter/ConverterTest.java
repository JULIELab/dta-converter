package de.julielab.dta.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ConverterTest {

	private static final String TEST_FILE_NAME = "short-arnim_wunderhorn01_1806.tcf.xml";
	private static final String TEST_FILE = "src/test/resources/testfiles/"
			+ TEST_FILE_NAME;

	@Test
	public void readDocumentLemmatizeTest() throws Exception {
		final File outputFile = File.createTempFile("fancy", "file");
		try (FileWriter metas = new FileWriter(File.createTempFile("meta",
				"file"))) {
			Converter.readDocument(new File(TEST_FILE), outputFile,
					Mode.LEMMATIZE, metas);
			assertEquals(
					Arrays.asList(new String[] {
							"d Knabe Wunderhorn .",
							"alt deutsch Lied sammeln von L. A. v. Arnim und Clemens Brentano .",
							"d Knabe Wunderhorn alt deutsch Lied L. Achim v. Arnim .",
							"clemens brentano .",
							"Heidelberg , bei Mohr u. Zimmer ." }),
							FileUtils.readLines(outputFile));
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Had exception!");
		}
	}

	@Test
	public void readDocumentNormalizeTest() throws Exception {
		final File outputFile = File.createTempFile("fancy", "file");
		try (FileWriter metas = new FileWriter(File.createTempFile("meta",
				"file"))) {
			Converter.readDocument(new File(TEST_FILE), outputFile,
					Mode.NORMALIZE, metas);
			assertEquals(
					Arrays.asList(new String[] {
							"Des Knaben Wunderhorn.",
							"Alte deutsche Lieder gesammelt von L. A. v. Arnim und Clemens Brentano.",
							"Des Knaben Wunderhorn Alte deutsche Lieder L. Achim v. Arnim.",
							"Clemens Brentano.",
					"Heidelberg, bei Mohr u. Zimmer." }),
					FileUtils.readLines(outputFile));
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Had exception!");
		}
	}
}