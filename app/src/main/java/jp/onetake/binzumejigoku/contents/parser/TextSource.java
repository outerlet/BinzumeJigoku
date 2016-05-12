package jp.onetake.binzumejigoku.contents.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.def.ContentsType;
import jp.onetake.binzumejigoku.util.Utility;

public class TextSource extends Source {
	public TextSource(XmlPullParser parser) {
		super(parser);
	}

	@Override
	public void parse() throws IOException, XmlPullParserException {
		super.parse();

		boolean isRuby = false;

		while (true) {
			int eventType = getXmlParser().getEventType();

			if (eventType == XmlPullParser.TEXT) {
				String text = Utility.format(getXmlParser().getText());
				android.util.Log.i(getContentsType().toString().toUpperCase(), "text = " + text + ", Ruby? = " + isRuby);
			} else if (getXmlParser().getName().equalsIgnoreCase("ruby")
					&& (eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG)) {
				isRuby = (eventType == XmlPullParser.START_TAG);
			}

			if (next()) {
				break;
			}
		}
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Text;
	}
}
