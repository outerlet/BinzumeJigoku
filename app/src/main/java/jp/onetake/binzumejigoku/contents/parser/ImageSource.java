package jp.onetake.binzumejigoku.contents.parser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;

import jp.onetake.binzumejigoku.contents.common.ContentsType;

public class ImageSource extends SectionChildSource {
	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param parser       XMLの解析を担当するパーサー
	 * @param database     コンテンツの各要素を保存するためのDBオブジェクト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public ImageSource(Context context, XmlPullParser parser, SQLiteDatabase database, int sectionIndex, int sequence) {
		super(context, parser, database, sectionIndex, sequence);
	}

	@Override
	public void save(ContentValues values) {
		values.put("VALUE0", getAttribute("src"));
		values.put("VALUE1", getAttribute("duration"));
		values.put("VALUE2", getAttribute("effect"));
		values.put("VALUE3", getAttribute("chain"));

		super.save(values);
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Image;
	}
}
