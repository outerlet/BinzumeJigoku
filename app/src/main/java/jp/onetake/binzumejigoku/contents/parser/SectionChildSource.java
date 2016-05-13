package jp.onetake.binzumejigoku.contents.parser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;

import jp.onetake.binzumejigoku.R;

public abstract class SectionChildSource extends Source {
	private int mSectionIndex;
	private int mSequence;

	/**
	 * コンストラクタ
	 * @param context		コンテキスト
	 * @param parser		XMLの解析を担当するパーサー
	 * @param database		コンテンツの各要素を保存するためのDBオブジェクト
	 * @param sectionIndex	このオブジェクトが属するセクションのインデックス値
	 * @param sequence		通し番号
	 */
	public SectionChildSource(Context context, XmlPullParser parser, SQLiteDatabase database, int sectionIndex, int sequence) {
		super(context, parser, database);

		mSectionIndex = sectionIndex;
		mSequence = sequence;
	}

	protected int getSectionIndex() {
		return mSectionIndex;
	}

	protected int getSequence() {
		return mSequence;
	}

	public void save(ContentValues values) {
		values.put("SECTION", getSectionIndex());
		values.put("SEQUENCE", getSequence());
		values.put("TYPE", getContentsType().toString());

		getDatabase().insert(getContext().getString(R.string.db_contents_table_name), null, values);
	}
}
