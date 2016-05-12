package jp.onetake.binzumejigoku.contents.parser;

import org.xmlpull.v1.XmlPullParser;

import jp.onetake.binzumejigoku.contents.def.ContentsType;

public class ClearTextSource extends Source {
	public ClearTextSource(XmlPullParser parser) {
		super(parser);
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.ClearText;
	}
}
