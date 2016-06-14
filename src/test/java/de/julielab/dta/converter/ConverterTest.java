package de.julielab.dta.converter;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ConverterTest {

	private static final String TEST_FILE_NAME = "short-arnim_wunderhorn01_1806.tcf.xml";
	private static final String TEST_FILE = "src/test/resources/testfiles/"
			+ TEST_FILE_NAME;

	@Test
	public void readDocumentTest() throws Exception {
		File outputFile = File.createTempFile("fancy", "file");
		Map<String, List<String>> file2classes = new HashMap<>();
		try {
			Converter.readDocument(new File(TEST_FILE), outputFile, true,
					file2classes);
			assertEquals(
					Arrays.asList(new String[] {
							"Des Knaben Wunderhorn.",
							"Alte deutsche Lieder gesammelt von L. A. v. Arnim und Clemens Brentano.",
							"Des Knaben Wunderhorn Alte deutsche Lieder L. Achim v. Arnim.",
							"Clemens Brentano.",
							"Heidelberg, bei Mohr u. Zimmer." }),
					FileUtils.readLines(outputFile));
			assertTrue(file2classes.containsKey(TEST_FILE_NAME));
			assertEquals(
					Arrays.asList(new String[] { "Belletristik",
							"Belletristik", "Wissenschaft" }),
					file2classes.get(TEST_FILE_NAME));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Had exception!");
		}
		
		//Now has to fail, due to entry in file2classes
		boolean ok = false;
		try {
			Converter.readDocument(new File(TEST_FILE), outputFile, true,
					file2classes);
		} catch (Exception e) {
			ok = true;
		}
		assertTrue("Read same file twice!", ok);
	}

}
