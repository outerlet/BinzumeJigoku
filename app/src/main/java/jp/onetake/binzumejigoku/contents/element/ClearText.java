package jp.onetake.binzumejigoku.contents.element;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;

import jp.onetake.binzumejigoku.contents.common.ContentsType;

/**
 * clear-text要素を制御する要素クラス
 */
public class ClearText extends SectionElement {
	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public ClearText(Context context, int sectionIndex, int sequence) {
		super(context, sectionIndex, sequence);
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.ClearText;
	}
}
