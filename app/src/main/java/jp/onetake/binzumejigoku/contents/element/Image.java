package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;

/**
 * image要素を制御する要素クラス
 */
public class Image extends SectionElement {
	public enum EffectType {
		Unknown,
		Fade,
		Cut;

		public static EffectType getValue(String effectText) {
			for (EffectType e : EffectType.values()) {
				if (effectText.equalsIgnoreCase(e.toString())) {
					return e;
				}
			}

			return Unknown;
		}
	}

	private String mSrc;
	private long mDuration;
	private EffectType mEffectType;
	private int mLayer;

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
		values.put(ContentsTable.VALUE3.getColumnName(), getAttribute("layer"));

		super.save(db, values);
	}

	@Override
	public void load(Cursor cursor) {
		super.load(cursor);

		mSrc = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE0));
		mDuration = (long)(Float.parseFloat(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE1))) * 1000);
		mEffectType = EffectType.getValue(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE2)));
		mLayer = Integer.parseInt(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE3)));
	}

	public int getLayer() {
		return mLayer;
	}

	public long getDuration() {
		return mDuration;
	}

	public EffectType getEffectType() {
		return mEffectType;
	}

	public Bitmap getBitmap() {
		if (mSrc != null) {
			Resources res = getContext().getResources();

			return BitmapFactory.decodeResource(
					res, res.getIdentifier(mSrc, "drawable", getContext().getPackageName()));
		}

		return null;
	}

	@Override
	public ContentsType getContentsType() {
		return ContentsType.Image;
	}

	@Override
	public String toString() {
		return String.format("%1$s : layer = %2$d, src = %3$s, duration = %4$d, effect = %5$s",
				super.toString(), mLayer, mSrc, mDuration, mEffectType.toString());
	}
}
