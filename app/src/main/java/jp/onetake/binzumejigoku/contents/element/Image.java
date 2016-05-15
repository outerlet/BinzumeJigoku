package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;

/**
 * image要素を制御する要素クラス
 */
public class Image extends SectionElement {
	private String mSrc;
	private float mDuration;
	private String mEffect;
	private String mChain;

	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public Image(Context context, int sectionIndex, int sequence) {
		super(context, sectionIndex, sequence);
	}

	@Override
	public void save(SQLiteDatabase db, ContentValues values) {
		values.put(ContentsTable.VALUE0.getColumnName(), getAttribute("src"));
		values.put(ContentsTable.VALUE1.getColumnName(), getAttribute("duration"));
		values.put(ContentsTable.VALUE2.getColumnName(), getAttribute("effect"));
		values.put(ContentsTable.VALUE3.getColumnName(), getAttribute("chain"));

		super.save(db, values);
	}

	@Override
	public void load(Cursor cursor) {
		mSrc = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE0));
		mDuration = Float.parseFloat(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE1)));
		mEffect = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE2));
		mChain = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE3));
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Image;
	}

	@Override
	public String toString() {
		return super.toString() + " : src = " + mSrc + ", duration = " + mDuration + ", effect = " + mEffect + ", chain = " + mChain;
	}
}
