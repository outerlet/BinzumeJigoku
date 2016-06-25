package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.db.ContentsDbOpenHelper;

/**
 * コンテンツにアクセスするために横断的に必要となるデータの管理や処理を担当する
 */
public class ContentsInterface {
	// Singletonなインスタンス
	private static ContentsInterface mInstance;

	private Context mContext;
	private ContentsDbOpenHelper mDbHelper;
	private String mRubyDelimiter;
	private String mRubyClosure;
	private int mMaxSectionIndex = -1;
	private SaveData[] mSaveDatas;

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
	 * このオブジェクトを初期化する
	 * @param context	コンテキスト
	 */
	public void initialize(Context context) {
		mContext = context;
		mDbHelper = new ContentsDbOpenHelper(context);

		int number = context.getResources().getInteger(R.integer.number_save_slot) + 1;
		mSaveDatas = new SaveData[number];
		for (int i = 0 ; i < number ; i++) {
			SaveData saveData = new SaveData(i);

			if (!saveData.load(context)) {
				saveData.setName(SaveData.getSaveName(context, i));
			}

			mSaveDatas[i] = saveData;
		}
	}

	/**
	 * このオブジェクトが初期化されているかどうか確認する
	 * @return	初期化されていればtrue
	 */
	public boolean isInitialized() {
		return (mContext != null);
	}

	/**
	 * アプリで利用するSharedPreferencesはここから取得する
	 * @return	SharedPreferences
	 */
	private SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
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
				.putString(mContext.getString(R.string.prefkey_ruby_delimiter), delimiter)
				.apply();
	}

	/**
	 * text要素において、表示テキストとそれにふるルビを区別するための区切り文字を取得する
	 * @return	表示テキストとそれにふるルビを区別するための区切り文字
	 */
	public String getRubyDelimiter() {
		if (mRubyDelimiter == null) {
			mRubyDelimiter = getPreferences().getString(
					mContext.getString(R.string.prefkey_ruby_delimiter), null);
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
				.putString(mContext.getString(R.string.prefkey_ruby_closure), closure)
				.apply();
	}

	/**
	 * text要素において、表示テキストのうちルビをふるべき文字列かそうでないかを区別するための囲み文字を取得する
	 * @return	text要素において、表示テキストのうちルビをふるべき文字列かそうでないかを区別するための囲み文字
	 */
	public String getRubyClosure() {
		if (mRubyClosure == null) {
			mRubyClosure = getPreferences().getString(
					mContext.getString(R.string.prefkey_ruby_closure), null);
		}

		return mRubyClosure;
	}

	/**
	 * XMLのパース処理が完了した場合、処理が終わったことを記録する
	 */
	public void markAsXmlParsed() {
		getPreferences().edit()
				.putBoolean(mContext.getString(R.string.prefkey_is_xml_parsed), true)
				.apply();
	}

	/**
	 * XMLのパース処理が完了しているかどうか<br />
	 * これまでにmarkAsXmlParsedが呼び出されたことがあるかどうかに等しい
	 * @return	XMLのパース処理が完了している場合true、未完了ならfalse
	 */
	public boolean isXmlParsed() {
		return getPreferences().getBoolean(
				mContext.getString(R.string.prefkey_is_xml_parsed), false);
	}

	/**
	 * 章番号の最大値をセットしてSharedPreferencesに保存する
	 * @param sectionIndex	章番号の最大値
	 */
	public void setMaxSectionIndex(int sectionIndex) {
		mMaxSectionIndex = sectionIndex;

		getPreferences().edit()
				.putInt(mContext.getString(R.string.prefkey_max_section_index), mMaxSectionIndex)
				.apply();
	}

	/**
	 * 章番号の最大値を取得する
	 * @return	章番号の最大値
	 */
	public int getMaxSectionIndex() {
		if (mMaxSectionIndex == -1) {
			mMaxSectionIndex = getPreferences().getInt(
					mContext.getString(R.string.prefkey_max_section_index), 0);
		}

		return mMaxSectionIndex;
	}

	/**
	 * slotIndexに指定したインデックスに保存されているSaveDataを取得する
	 * @param slotIndex	セーブスロットを示すインデックス値
	 * @return	SaveDataオブジェクト
	 */
	public SaveData getSaveData(int slotIndex) {
		return mSaveDatas[slotIndex];
	}

	public void setSaveData(int slotIndex, SaveData saveData) {
		mSaveDatas[slotIndex] = saveData;
	}

	public float getTextSize() {
		int size = Integer.parseInt(getPreferences().getString(mContext.getString(R.string.prefkey_text_size), "0"));
		int dimenId = R.dimen.default_text_size;
		switch (size) {
			case 1:
				dimenId = R.dimen.large_text_size;
				break;
			case -1:
				dimenId = R.dimen.small_text_size;
				break;
		}

		return mContext.getResources().getDimensionPixelSize(dimenId);
	}

	public float getRubySize() {
		int size = Integer.parseInt(getPreferences().getString(mContext.getString(R.string.prefkey_text_size), "0"));
		int dimenId = R.dimen.default_ruby_size;
		switch (size) {
			case 1:
				dimenId = R.dimen.large_ruby_size;
				break;
			case -1:
				dimenId = R.dimen.small_ruby_size;
				break;
		}

		return mContext.getResources().getDimensionPixelSize(dimenId);
	}

	public int getTextPeriod() {
		int speed = Integer.parseInt(getPreferences().getString(mContext.getString(R.string.prefkey_text_speed), "0"));
		int intId = R.integer.text_period_millis_default;
		switch (speed) {
			case 1:
				intId = R.integer.text_period_millis_fast;
				break;
			case -1:
				intId = R.integer.text_period_millis_slow;
				break;
		}

		return mContext.getResources().getInteger(intId);
	}
}
