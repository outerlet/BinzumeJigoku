package jp.onetake.binzumejigoku.contents.parser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.util.Utility;

public class TitleSource extends SectionChildSource {
	private String mTitle = null;

	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param parser       XMLの解析を担当するパーサー
	 * @param database     コンテンツの各要素を保存するためのDBオブジェクト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public TitleSource(Context context, XmlPullParser parser, SQLiteDatabase database, int sectionIndex, int sequence) {
		super(context, parser, database, sectionIndex, sequence);
	}

	@Override
	public void parse() throws IOException, XmlPullParserException {
		super.parse();

		while (true) {
			if (getXmlParser().getEventType() == XmlPullParser.TEXT) {
				mTitle = Utility.format(getXmlParser().getText());
			}

			if (!hasNext()) {
				break;
			}
		}
	}

	@Override
	public void save(ContentValues values) {
		values.put("CONTENTS_TEXT", mTitle);

		super.save(values);
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Title;
	}
}
