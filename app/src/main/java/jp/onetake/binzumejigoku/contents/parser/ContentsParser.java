package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

import jp.onetake.binzumejigoku.contents.def.ContentsType;

public class ContentsParser {
	private Context mContext;
	private XmlPullParser mXmlParser;

	public ContentsParser(Context context) {
		mContext = context;
	}

	public void parse(String fileName) throws IOException, XmlPullParserException {
		try (InputStreamReader reader = new InputStreamReader(mContext.getAssets().open(fileName))) {
			mXmlParser = Xml.newPullParser();
			mXmlParser.setInput(reader);

			while (mXmlParser.next() != XmlPullParser.END_DOCUMENT) {
				if (mXmlParser.getEventType() == XmlPullParser.START_TAG) {
					switch (ContentsType.getValue(mXmlParser.getName())) {
						case MetaData:
							(new MetaDataSource(mXmlParser)).parse();
							break;
						case Section:
							(new SectionSource(mXmlParser)).parse();
							break;
						default:
							break;
					}
				}
			}
		} catch (IOException | XmlPullParserException ex) {
			throw ex;
		}
	}
}
