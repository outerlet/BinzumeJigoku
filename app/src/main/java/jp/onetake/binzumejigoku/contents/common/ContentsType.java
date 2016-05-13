package jp.onetake.binzumejigoku.contents.common;

public enum ContentsType {
	Unknown,
	MetaData,
	Section,
	Title,
	Image,
	Text,
	ClearText;

	public boolean equals(String name) {
		return name.equalsIgnoreCase(this.toString());
	}

	public static ContentsType getValue(String tag) {
		if (tag != null) {
			String comparison = tag.replaceAll("-+", "");

			for (ContentsType type : values()) {
				if (type.equals(comparison)) {
					return type;
				}
			}
		}

		return Unknown;
	}
}
