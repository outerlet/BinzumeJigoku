package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import jp.onetake.binzumejigoku.contents.db.ContentsDbOpenHelper;

public class ContentsHandler {
	private static ContentsHandler mInstance;

	private ContentsDbOpenHelper mDbHelper;
	private String mRubyDelimiter;
	private String mRubyClosure;

	public static ContentsHandler getInstance() {
		if (mInstance == null) {
			mInstance = new ContentsHandler();
		}
		return mInstance;
	}

	public void initialize(Context context) {
		mDbHelper = new ContentsDbOpenHelper(context);
	}

	public SQLiteDatabase getReadableDatabase() {
		return mDbHelper.getReadableDatabase();
	}

	public SQLiteDatabase getWritableDatabase() {
		return mDbHelper.getWritableDatabase();
	}

	public void setRubyDelimiter(String delimiter) {
		mRubyDelimiter = delimiter;
	}

	public void setRubyClosure(String closure) {
		mRubyClosure = closure;
	}

	public String getRubyDelimiter() {
		return mRubyDelimiter;
	}

	public String getRubyClosure() {
		return mRubyClosure;
	}
}
