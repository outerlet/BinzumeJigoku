package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsHandler;
import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;
import jp.onetake.binzumejigoku.contents.element.ClearText;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;

public class ContentsDbParser extends ContentsParser {
	/**
	 * コンストラクタ
	 * @param context
	 */
	public ContentsDbParser(Context context) {
		super(context);
	}

	/**
	 * sectionIndexに指定したセクションインデックスに該当するセクション要素をDBから読み出して適切な要素オブジェクトを生成する
	 * @param sectionIndex	セクションに割り当てられたインデックス値
	 */
	public List<SectionElement> parse(int sectionIndex) {
		List<SectionElement> list = new ArrayList<>();

		try (SQLiteDatabase db = ContentsHandler.getInstance().getReadableDatabase();
			Cursor cursor = db.rawQuery(getString(R.string.db_query_contents_table_sql), new String[] { Integer.toString(sectionIndex) })) {
			cursor.moveToFirst();

			for (int i = 0 ; i < cursor.getCount() ; i++) {
				SectionElement elm;

				ContentsType type = ContentsType.getValue(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.TYPE)));
				int sequence = Integer.parseInt(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.SEQUENCE)));

				switch (type) {
					case Image:
						elm = new Image(getContext(), sectionIndex, sequence);
						break;
					case Title:
						elm = new Title(getContext(), sectionIndex, sequence);
						break;
					case Text:
						elm = new Text(getContext(), sectionIndex, sequence);
						break;
					case ClearText:
						elm = new ClearText(getContext(), sectionIndex, sequence);
						break;
					default:
						continue;
				}

				elm.load(cursor);

				list.add(elm);

				cursor.moveToNext();
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			return null;
		}

		return list;
	}
}
