package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

import jp.onetake.binzumejigoku.contents.data.ContentsDbOpenHelper;
import jp.onetake.binzumejigoku.contents.common.ContentsType;

public class ContentsParser {
	private Context mContext;

	public ContentsParser(Context context) {
		mContext = context;
	}

	public void parse(String fileName) throws IOException, XmlPullParserException {
		InputStreamReader reader = new InputStreamReader(mContext.getAssets().open(fileName));
		ContentsDbOpenHelper helper = new ContentsDbOpenHelper(mContext);
		SQLiteDatabase database = helper.getWritableDatabase();

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(reader);

		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				switch (ContentsType.getValue(parser.getName())) {
					case MetaData:
						(new MetaDataSource(mContext, parser, database)).parse();
						break;
					case Section:
						(new SectionSource(mContext, parser, database)).parse();
						break;
					default:
						break;
				}
			}
		}
	}
}
