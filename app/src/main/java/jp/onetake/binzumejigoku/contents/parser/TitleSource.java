package jp.onetake.binzumejigoku.contents.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.def.ContentsType;
import jp.onetake.binzumejigoku.util.Utility;

public class TitleSource extends Source {
	public TitleSource(XmlPullParser parser) {
		super(parser);
	}

	@Override
	public void parse() throws IOException, XmlPullParserException {
		super.parse();

		while (true) {
			if (getXmlParser().getEventType() == XmlPullParser.TEXT) {
				String title = Utility.format(getXmlParser().getText());
				android.util.Log.i(getContentsType().toString().toUpperCase(), title);
			}

			if (next()) {
				break;
			}
		}
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Title;
	}
}
