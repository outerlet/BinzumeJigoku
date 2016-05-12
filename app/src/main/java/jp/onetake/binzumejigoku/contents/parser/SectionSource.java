package jp.onetake.binzumejigoku.contents.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.def.ContentsType;

public class SectionSource extends Source {
	public SectionSource(XmlPullParser parser) {
		super(parser);
	}

	@Override
	public void parse() throws IOException, XmlPullParserException {
		super.parse();

		while (true) {
			if (getXmlParser().getEventType() == XmlPullParser.START_TAG) {
				switch (ContentsType.getValue(getXmlParser().getName())) {
					case Title:
						(new TitleSource(getXmlParser())).parse();
						break;
					case Image:
						(new ImageSource(getXmlParser())).parse();
						break;
					case Text:
						(new TextSource(getXmlParser())).parse();
						break;
					case ClearText:
						(new ClearTextSource(getXmlParser())).parse();
						break;
					default:
						break;
				}
			}

			if (next()) {
				break;
			}
		}
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Section;
	}
}
