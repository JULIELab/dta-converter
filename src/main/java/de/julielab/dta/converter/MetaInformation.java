package de.julielab.dta.converter;

import com.google.common.base.Joiner;

public class MetaInformation {

	private static final Joiner TAB_JOINER = Joiner.on("\t");
	public static final String CSV_HEADER = TAB_JOINER.join(new String[] {
			"fileName", "DTAClass", "DTASubClass", "year", "pubPlaces",
			"publisher" });
	private final String fileName, DTAClass, DTASubClass, year, pubPlaces,
			publisher;

	public MetaInformation(final String fileName, final String DTAClass,
			final String DTASubClass, final String year,
			final String pubPlaces, final String publisher) {
		this.fileName = fileName;
		this.DTAClass = DTAClass;
		this.DTASubClass = DTASubClass;
		this.year = year;
		this.pubPlaces = pubPlaces;
		this.publisher = publisher;
	}

	/**
	 * @return a csv description
	 */
	public String toCSV() {
		final String[] s = new String[] { fileName, DTAClass, DTASubClass,
				year, pubPlaces, publisher };
		for (int i = 0; i < s.length; ++i) {
			if (s[i] == null)
				s[i] = "N.A.";
			else
				s[i] = s[i].replaceAll("\t", "    ");
		}
		return TAB_JOINER.join(s);
	}

}
