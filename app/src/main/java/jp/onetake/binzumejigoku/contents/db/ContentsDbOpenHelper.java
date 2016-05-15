package jp.onetake.binzumejigoku.contents.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jp.onetake.binzumejigoku.R;

/**
 * コンテンツの内容をデータベースに保存したりデータベースから読み出したりするためのヘルパクラス
 */
public class ContentsDbOpenHelper extends SQLiteOpenHelper {
	private Context mContext;

	public ContentsDbOpenHelper(Context context) {
		super(context, context.getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));

		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(mContext.getString(R.string.db_create_contents_table_sql));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(mContext.getString(R.string.db_drop_contents_table_sql));
		onCreate(db);
	}

	public void debugPrint() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(mContext.getString(R.string.db_query_contents_table_sql_debug), null);
		c.moveToFirst();

		for (int i = 0 ; i < c.getCount() ; i++) {
			StringBuilder buf = new StringBuilder();
			for (int j = 0 ; j < c.getColumnCount() ; j++) {
				buf.append(((j == 0) ? "" : ", ") + c.getString(j));
			}
			android.util.Log.i("DATABASE", buf.toString());

			c.moveToNext();
		}

		c.close();
		db.close();
	}
}
