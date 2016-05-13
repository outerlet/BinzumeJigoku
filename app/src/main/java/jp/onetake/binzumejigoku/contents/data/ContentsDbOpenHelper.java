package jp.onetake.binzumejigoku.contents.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jp.onetake.binzumejigoku.R;

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
}
