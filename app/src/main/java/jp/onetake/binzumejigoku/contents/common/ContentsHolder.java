package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;
import jp.onetake.binzumejigoku.contents.element.ClearText;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;

/**
 * SectionのIndex値に応じたセクションのコンテンツをDBから読み取り、それぞれに応じた要素オブジェクトを生成・保持するクラス
 */
public class ContentsHolder {
	private Context mContext;
	private List<SectionElement> mElementList;
	private int mCurrentIndex;

	public ContentsHolder(Context context, int sectionIndex) {
		mContext = context;
		mElementList = new ArrayList<>();
		mCurrentIndex = -1;

		parse(sectionIndex);
	}

	public boolean hasNext() {
		return (mCurrentIndex + 1 < mElementList.size());
	}

	public SectionElement next() {
		return mElementList.get(++mCurrentIndex);
	}

	/**
	 * sectionIndexに指定したセクションインデックスに該当するセクション要素をDBから読み出して適切な要素オブジェクトを生成する
	 * @param sectionIndex	セクションに割り当てられたインデックス値
	 */
	private void parse(int sectionIndex) throws SQLiteException {
		try (SQLiteDatabase db = ContentsInterface.getInstance().getReadableDatabase();
			 Cursor cursor = db.rawQuery(
					 mContext.getString(R.string.db_query_contents_table_sql), new String[] { Integer.toString(sectionIndex) })) {
			cursor.moveToFirst();

			for (int i = 0 ; i < cursor.getCount() ; i++) {
				SectionElement elm;

				ContentsType type = ContentsType.getValue(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.TYPE)));
				int sequence = Integer.parseInt(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.SEQUENCE)));

				switch (type) {
					case Image:
						elm = new Image(mContext, sectionIndex, sequence);
						break;
					case Title:
						elm = new Title(mContext, sectionIndex, sequence);
						break;
					case Text:
						elm = new Text(mContext, sectionIndex, sequence);
						break;
					case ClearText:
						elm = new ClearText(mContext, sectionIndex, sequence);
						break;
					default:
						continue;
				}

				elm.load(cursor);

				mElementList.add(elm);

				cursor.moveToNext();
			}
		}
	}
}
