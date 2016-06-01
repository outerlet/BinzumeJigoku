package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.db.ContentsDbOpenHelper;

/**
 * コンテンツにアクセスするために横断的に必要となるデータの管理や処理を担当する
 */
public class ContentsInterface {
	private final String KEY_SHARED_PREFS			= "ContentsInterface.KEY_SHARED_PREFS";
	private final String PREFKEY_RUBY_DELIMITER		= "ContentsInterface.PREFKEY_RUBY_DELIMITER";
	private final String PREFKEY_RUBY_CLOSURE		= "ContentsInterface.PREFKEY_RUBY_CLOSURE";
	private final String PREFKEY_IS_XML_PARSED		= "ContentsInterface.PREFKEY_IS_XML_PARSED";
	private final String PREFKEY_MAX_SECTION_INDEX	= "ContentsInterface.PREFKEY_MAX_SECTION_INDEX";

	// Singletonなインスタンス
	private static ContentsInterface mInstance;

	private Context mContext;
	private ContentsDbOpenHelper mDbHelper;
	private String mRubyDelimiter;
	private String mRubyClosure;
	private int mMaxSectionIndex = -1;

	/**
	 * このクラスの唯一のインスタンスを返却する
	 * @return	このクラスのインスタンス
	 */
	public static ContentsInterface getInstance() {
		if (mInstance == null) {
			mInstance = new ContentsInterface();
		}
		return mInstance;
	}

	/**
	 * このクラスを初期化する
	 * @param context	コンテキスト
	 */
	public void initialize(Context context) {
		mContext = context;
		mDbHelper = new ContentsDbOpenHelper(context);
	}

	/**
	 * アプリで利用するSharedPreferencesはここから取得する
	 * @return	SharedPreferences
	 */
	public SharedPreferences getPreferences() {
		return mContext.getSharedPreferences(KEY_SHARED_PREFS, Context.MODE_PRIVATE);
	}

	/**
	 * 読み込み可能なデータベースを取得する
	 * @return	読み込みモードのデータベース
	 */
	public SQLiteDatabase getReadableDatabase() {
		return mDbHelper.getReadableDatabase();
	}

	/**
	 * 書き込み可能なデータベースを取得する
	 * @return	書き込みモードのデータベース
	 */
	public SQLiteDatabase getWritableDatabase() {
		return mDbHelper.getWritableDatabase();
	}

	/**
	 * text要素において、表示テキストとそれにふるルビを区別するための区切り文字をセットする<br />
	 * この区切り文字はXMLのmeta-data要素で与えられる
	 * @param delimiter	テキストとルビを区別するための区切り文字
	 */
	public void setRubyDelimiter(String delimiter) {
		mRubyDelimiter = delimiter;

		getPreferences().edit()
				.putString(PREFKEY_RUBY_DELIMITER, delimiter)
				.apply();
	}

	/**
	 * text要素において、表示テキストとそれにふるルビを区別するための区切り文字を取得する
	 * @return	表示テキストとそれにふるルビを区別するための区切り文字
	 */
	public String getRubyDelimiter() {
		if (mRubyDelimiter == null) {
			mRubyDelimiter = getPreferences().getString(PREFKEY_RUBY_DELIMITER, null);
		}

		return mRubyDelimiter;
	}

	/**
	 * text要素において、表示テキストのうちルビをふるべき文字列かそうでないかを区別するための囲み文字をセットする<br />
	 * この囲み文字はXMLのmeta-data要素で与えられる
	 * @param closure	ルビつきの文字かどうかを区別するための囲み文字
	 */
	public void setRubyClosure(String closure) {
		mRubyClosure = closure;

		getPreferences().edit()
				.putString(PREFKEY_RUBY_CLOSURE, closure)
				.apply();
	}

	/**
	 * text要素において、表示テキストのうちルビをふるべき文字列かそうでないかを区別するための囲み文字を取得する
	 * @return	text要素において、表示テキストのうちルビをふるべき文字列かそうでないかを区別するための囲み文字
	 */
	public String getRubyClosure() {
		if (mRubyClosure == null) {
			mRubyClosure = getPreferences().getString(PREFKEY_RUBY_CLOSURE, null);
		}

		return mRubyClosure;
	}

	/**
	 * XMLのパース処理が完了した場合、処理が終わったことを記録する
	 */
	public void markAsXmlParsed() {
		getPreferences().edit()
				.putBoolean(PREFKEY_IS_XML_PARSED, true)
				.apply();
	}

	/**
	 * XMLのパース処理が完了しているかどうか<br />
	 * これまでにmarkAsXmlParsedが呼び出されたことがあるかどうかに等しい
	 * @return	XMLのパース処理が完了している場合true、未完了ならfalse
	 */
	public boolean isXmlParsed() {
		return getPreferences().getBoolean(PREFKEY_IS_XML_PARSED, false);
	}

	public void setMaxSectionIndex(int sectionIndex) {
		mMaxSectionIndex = sectionIndex;

		getPreferences().edit()
				.putInt(PREFKEY_MAX_SECTION_INDEX, mMaxSectionIndex)
				.apply();
	}

	public int getMaxSectionIndex() {
		if (mMaxSectionIndex == -1) {
			mMaxSectionIndex = getPreferences().getInt(PREFKEY_MAX_SECTION_INDEX, 0);
		}

		return mMaxSectionIndex;
	}
}
