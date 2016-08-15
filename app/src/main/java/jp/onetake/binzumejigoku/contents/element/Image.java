package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Locale;

import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;

/**
 * 画像を表示する"image"要素を制御するクラス
 */
public class Image extends SectionElement {
	/**
	 * 画像を表示する際のエフェクトに対応する列挙値
	 */
	public enum EffectType {
		Unknown,	// 不明(便宜上の値)
		Fade,		// フェードイン・アウト
		Cut;		// カットイン・アウト

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

	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public Image(Context context, int sectionIndex, int sequence) {
		super(context, sectionIndex, sequence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(SQLiteDatabase db, ContentValues values) {
		values.put(ContentsTable.VALUE0.toColumnName(), getAttribute("src"));
		values.put(ContentsTable.VALUE1.toColumnName(), getAttribute("duration"));
		values.put(ContentsTable.VALUE2.toColumnName(), getAttribute("effect"));
		values.put(ContentsTable.VALUE3.toColumnName(), getAttribute("layer"));

		super.save(db, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(Cursor cursor) {
		super.load(cursor);

		mSrc = cursor.getString(ContentsTable.VALUE0.toColumnIndex());
		mDuration = (long)(Float.parseFloat(cursor.getString(ContentsTable.VALUE1.toColumnIndex())) * 1000);
		mEffectType = EffectType.getValue(cursor.getString(ContentsTable.VALUE2.toColumnIndex()));
	}

	/**
	 * 画像の表示にかける時間を返却する
	 * @return	画像の表示にかける時間(ms)
	 */
	public long getDuration() {
		return mDuration;
	}

	/**
	 * 画像の表示にかけるエフェクトを示すEffectType列挙値を返却する
	 * @return	エフェクトを示すEffectType列挙値
	 */
	public EffectType getEffectType() {
		return mEffectType;
	}

	/**
	 * 表示させる画像のBitmapオブジェクトを返却する
	 * @return	画像のBitmapオブジェクト
	 */
	public Bitmap getBitmap() {
		if (mSrc != null) {
			Resources res = getContext().getResources();

			return BitmapFactory.decodeResource(
					res, res.getIdentifier(mSrc, "drawable", getContext().getPackageName()));
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentsType getContentsType() {
		return ContentsType.Image;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format(
				Locale.US,
				"%1$s : src = %2$s, duration = %3$d, effect = %4$s",
				super.toString(), mSrc, mDuration, mEffectType.toString());
	}
}
