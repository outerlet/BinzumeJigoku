package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;

import jp.onetake.binzumejigoku.contents.common.ContentsType;

public class ClearTextSource extends SectionChildSource {
	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param parser       XMLの解析を担当するパーサー
	 * @param database     コンテンツの各要素を保存するためのDBオブジェクト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public ClearTextSource(Context context, XmlPullParser parser, SQLiteDatabase database, int sectionIndex, int sequence) {
		super(context, parser, database, sectionIndex, sequence);
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.ClearText;
	}
}
