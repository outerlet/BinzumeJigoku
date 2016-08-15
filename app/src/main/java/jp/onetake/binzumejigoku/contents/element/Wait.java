package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;

/**
 * 指定した時間だけコンテンツの進行を待機させる"wait"要素を制御するクラス
 *
 */
public class Wait extends SectionElement {
	private long mDuration;

	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public Wait(Context context, int sectionIndex, int sequence) {
		super(context, sectionIndex, sequence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(SQLiteDatabase db, ContentValues values) {
		values.put(ContentsTable.VALUE0.toColumnName(), getAttribute("duration"));

		super.save(db, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(Cursor cursor) {
		super.load(cursor);

		mDuration = (long)(Float.parseFloat(cursor.getString(ContentsTable.VALUE0.toColumnIndex())) * 1000);
	}

	/**
	 * 待機時間を取得する
	 * @return	待機時間(ms)
	 */
	public long getDuration() {
		return mDuration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentsType getContentsType() {
		return ContentsType.Wait;
	}
}
