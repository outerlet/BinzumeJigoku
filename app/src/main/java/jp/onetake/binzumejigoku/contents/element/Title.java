package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;
import jp.onetake.binzumejigoku.util.Utility;

/**
 * タイトルを表示させるための"title"要素を制御するクラス
 */
public class Title extends SectionElement {
	private String mTitle;

	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public Title(Context context, int sectionIndex, int sequence) {
		super(context, sectionIndex, sequence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
		super.parse(parser);

		while (true) {
			if (parser.getEventType() == XmlPullParser.TEXT) {
				mTitle = Utility.format(parser.getText());
			}

			if (!hasNext(parser)) {
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(SQLiteDatabase db, ContentValues values) {
		values.put(ContentsTable.CONTENTS_TEXT.toColumnName(), mTitle);

		super.save(db, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(Cursor cursor) {
		super.load(cursor);

		mTitle = cursor.getString(ContentsTable.CONTENTS_TEXT.toColumnIndex());
	}

	/**
	 * タイトルとして表示する文字列を返却する
	 * @return	タイトル文字列
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentsType getContentsType() {
		return ContentsType.Title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString() + " : title = " + mTitle;
	}
}
