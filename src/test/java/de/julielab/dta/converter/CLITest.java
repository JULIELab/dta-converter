package de.julielab.dta.converter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				outputPath, TEST_NAME + ".txt") });
		final List<File> actual = CLI.getOutputFiles(outputPath,
				Arrays.asList(new File[] { new File(TEST_FILE) }));
		assertEquals(expected, actual);

		exception.expect(IllegalArgumentException.class);
		CLI.getOutputFiles(BAD_TEST_PATH,
				Arrays.asList(new File[] { new File(TEST_FILE) }));
	}

	@Test
	public void writeFile2ClassesTest() throws IOException {
		final File metaFile = new File(Files.createTempDir(), "foo");
		final Map<String, List<String>> file2classes = new HashMap<>();
		file2classes.put("name1", Arrays.asList(new String[] { "a1", "b1" }));
		file2classes.put("name2", Arrays.asList(new String[] { "a2", "b2" }));
		CLI.writeFile2Classes(file2classes, metaFile);

		final Map<String, List<String>> readBackFile2Classes = new HashMap<>();
		for (final String line : FileUtils.readLines(metaFile)) {
			final String[] parts = line.split(";");
			final List<String> value = Arrays.asList(Arrays.copyOfRange(parts,
					1, parts.length));
			readBackFile2Classes.put(parts[0], value);
		}
		assertEquals(file2classes, readBackFile2Classes);

		//can not overwrite existing file
		exception.expect(IllegalArgumentException.class);
		CLI.writeFile2Classes(file2classes, metaFile);
	}

}
