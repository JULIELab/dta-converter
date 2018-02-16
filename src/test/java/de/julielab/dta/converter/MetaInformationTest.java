package de.julielab.dta.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MetaInformationTest {
	@Test
	public void testToCSV() {
		assertEquals(CLITest.EXPECTED_META, new MetaInformation(
				"short-arnim_wunderhorn01_1806", "Belletristik", "Lyrik",
				"1806", "Frankfurt;Heidelberg", "Mohr u: Zimmer").toCSV());
		assertEquals(CLITest.EXPECTED_META+"    foo",
				new MetaInformation("short-arnim_wunderhorn01_1806", "Belletristik", "Lyrik",
						"1806", "Frankfurt;Heidelberg", "Mohr u: Zimmer\tfoo").toCSV());
	}
}
