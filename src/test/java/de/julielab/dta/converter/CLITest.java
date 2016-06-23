package de.julielab.dta.converter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.io.Files;

public class CLITest {

	private static final String TEST_NAME = "short-arnim_wunderhorn01_1806";

	private static final String TEST_PATH = "src/test/resources/testfiles";

	private static final String TEST_SUFFIX = ".tcf.xml";
	private static final String TEST_FILE = TEST_PATH + "/" + TEST_NAME
			+ TEST_SUFFIX;
	private static final String BAD_TEST_PATH = "src/test/resources/badfiles";

	static final String EXPECTED_META = "short-arnim_wunderhorn01_1806\tBelletristik\tLyrik\t1806\tFrankfurt;Heidelberg\tMohr u: Zimmer";

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void getInputFilesTest() {
		assertEquals(Arrays.asList(new File[] { new File(TEST_FILE) }),
				CLI.getInputFiles(TEST_PATH));

		assertEquals(Arrays.asList(new File[] { new File(TEST_FILE) }),
				CLI.getInputFiles(TEST_FILE));

		exception.expect(IllegalArgumentException.class);
		CLI.getInputFiles(BAD_TEST_PATH);
	}

	@Test
	public void getOutputFilesTest() throws IOException {
		final String outputPath = Files.createTempDir().getCanonicalPath();
		final List<File> expected = Arrays.asList(new File[] { new File(
				outputPath, TEST_NAME) });
		final List<File> actual = CLI.getOutputFiles(outputPath,
				Arrays.asList(new File[] { new File(TEST_FILE) }));
		assertEquals(expected, actual);

		exception.expect(IllegalArgumentException.class);
		CLI.getOutputFiles(BAD_TEST_PATH,
				Arrays.asList(new File[] { new File(TEST_FILE) }));
	}

	@Test
	public void mainTest() throws Exception {
		final String input = TEST_FILE;
		final String output = Files.createTempDir().getCanonicalPath();
		final String meta = new File(Files.createTempDir(), "foo")
		.getCanonicalPath();
		CLI.main(new String[] { "-i", input, "-o", output, "-m", meta });

		//Meta
		final List<String> lines = FileUtils.readLines(new File(meta));
		assertEquals(MetaInformation.CSV_HEADER, lines.get(0));
		assertEquals(EXPECTED_META, lines.get(1));

		//Text
		assertEquals(
				Arrays.asList(new String[] {
						"Des Knaben Wunderhorn.",
						"Alte deutsche Lieder gesammelt von L. A. v. Arnim und Clemens Brentano.",
						"Des Knaben Wunderhorn Alte deutsche Lieder L. Achim v. Arnim.",
						"Clemens Brentano.", "Heidelberg, bei Mohr u. Zimmer." }),
				FileUtils.readLines(new File(output, TEST_NAME)));
	}

	@Test
	public void mainTestLemmatize() throws Exception {
		final String input = TEST_FILE;
		final String output = Files.createTempDir().getCanonicalPath();
		final String meta = new File(Files.createTempDir(), "foo")
		.getCanonicalPath();
		CLI.main(new String[] { "-i", input, "-o", output, "-m", meta, "-l" });

		//Meta
		final List<String> lines = FileUtils.readLines(new File(meta));
		assertEquals(MetaInformation.CSV_HEADER, lines.get(0));
		assertEquals(EXPECTED_META, lines.get(1));

		//Text
		assertEquals(
				Arrays.asList(new String[] {
						"d Knabe Wunderhorn .",
						"alt deutsch Lied sammeln von L. A. v. Arnim und Clemens Brentano .",
						"d Knabe Wunderhorn alt deutsch Lied L. Achim v. Arnim .",
						"clemens brentano .",
						"Heidelberg , bei Mohr u. Zimmer ." }),
				FileUtils.readLines(new File(output, TEST_NAME)));
	}

	@Test
	public void writeMetaInformationTest() throws IOException {
		final File metaFile = new File(Files.createTempDir(), "foo");

		FileWriter fw = CLI.writeMetaInformation(metaFile);
		fw.close();
		List<String> lines = FileUtils.readLines(metaFile);
		assertEquals(MetaInformation.CSV_HEADER, lines.get(0));

		//delete and recreate
		metaFile.delete();
		metaFile.getParentFile().delete();
		fw = CLI.writeMetaInformation(metaFile);
		fw.close();
		lines = FileUtils.readLines(metaFile);
		assertEquals(MetaInformation.CSV_HEADER, lines.get(0));

		//can not overwrite existing file
		exception.expect(IllegalArgumentException.class);
		fw = CLI.writeMetaInformation(metaFile);
		fw.close();
	}
}
