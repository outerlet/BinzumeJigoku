package jp.onetake.binzumejigoku.contents.parser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsType;

public class SectionSource extends Source {
	/**
	 * コンストラクタ
	 * @param context  コンテキスト
	 * @param parser   XMLの解析を担当するパーサー
	 * @param database コンテンツの各要素を保存するためのDBオブジェクト
	 */
	public SectionSource(Context context, XmlPullParser parser, SQLiteDatabase database) {
		super(context, parser, database);
	}

	@Override
	public void parse() throws IOException, XmlPullParserException {
		super.parse();

		int index = Integer.parseInt(getAttribute("index"));
		int sequence = 0;
		while (true) {
			if (getXmlParser().getEventType() == XmlPullParser.START_TAG) {
				SectionChildSource source = null;

				switch (ContentsType.getValue(getXmlParser().getName())) {
					case Title:
						source = new TitleSource(getContext(), getXmlParser(), getDatabase(), index, sequence++);
						break;
					case Image:
						source = new ImageSource(getContext(), getXmlParser(), getDatabase(), index, sequence++);
						break;
					case Text:
						source = new TextSource(getContext(), getXmlParser(), getDatabase(), index, sequence++);
						break;
					case ClearText:
						source = new ClearTextSource(getContext(), getXmlParser(), getDatabase(), index, sequence++);
						break;
					default:
						break;
				}

				if (source != null) {
					source.parse();
					source.save(new ContentValues());
				}
			}

			if (!hasNext()) {
				break;
			}
		}
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Section;
	}
}
