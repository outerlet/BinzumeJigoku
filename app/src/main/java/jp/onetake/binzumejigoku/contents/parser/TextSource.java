package jp.onetake.binzumejigoku.contents.parser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsHandler;
import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.util.Utility;

public class TextSource extends SectionChildSource {
	private StringBuilder mTextBuffer;

	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param parser       XMLの解析を担当するパーサー
	 * @param database     コンテンツの各要素を保存するためのDBオブジェクト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public TextSource(Context context, XmlPullParser parser, SQLiteDatabase database, int sectionIndex, int sequence) {
		super(context, parser, database, sectionIndex, sequence);

		mTextBuffer = new StringBuilder();
	}

	@Override
	public void parse() throws IOException, XmlPullParserException {
		super.parse();

		while (true) {
			int eventType = getXmlParser().getEventType();

			if (eventType == XmlPullParser.TEXT) {
				mTextBuffer.append(Utility.format(getXmlParser().getText()));
			} else if (getXmlParser().getName().equalsIgnoreCase("ruby")
					&& (eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG)) {
				mTextBuffer.append(ContentsHandler.getInstance().getRubyClosure());
			}

			if (!hasNext()) {
				break;
			}
		}
	}

	@Override
	public void save(ContentValues values) {
		values.put("CONTENTS_TEXT", mTextBuffer.toString());

		super.save(values);
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Text;
	}
}
