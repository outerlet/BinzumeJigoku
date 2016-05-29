package jp.onetake.binzumejigoku.contents.db;

public enum ContentsTable {
	SECTION,
	SEQUENCE,
	TYPE,
	COMMON0,
	COMMON1,
	COMMON2,
	VALUE0,
	VALUE1,
	VALUE2,
	VALUE3,
	VALUE4,
	VALUE5,
	VALUE6,
	VALUE7,
	VALUE8,
	VALUE9,
	CONTENTS_TEXT,
	UNKNOWN;

	public String getColumnName() {
		return toString().toUpperCase();
	}

	public static int getColumnIndex(ContentsTable value) {
		int index = 0;
		for (ContentsTable c : values()) {
			if (c == value) {
				return index;
			}
			++index;
		}

		return -1;
	}
}
