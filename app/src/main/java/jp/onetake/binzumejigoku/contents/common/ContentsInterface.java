package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;
import android.content.SharedPreferences;
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
	private int mCurrentDbVersion;
	private int mMaxSectionIndex;
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
	 * コンストラクタ
	 */
	private ContentsInterface() {
		mCurrentDbVersion = 0;
		mMaxSectionIndex = -1;
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

			// セーブデータを読み込む
			// オートセーブデータが保存されてない場合はまだ物語が始まってないので、使用できないものとしてマークする
			if (!saveData.load(context)) {
				saveData.setName(SaveData.getSaveName(context, i));

				if (i == 0) {
					saveData.markAsUnusable();
				}
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
	 * アプリで利用するSharedPreferencesはここから返却する
	 * @return	SharedPreferences
	 */
	private SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	/**
	 * データベースにアクセスするためのヘルパオブジェクトを返却する
	 * @return	データベースにアクセスするためのヘルパオブジェクト
	 */
	public ContentsDbOpenHelper getDatabaseHelper() {
		return mDbHelper;
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
	 * text要素において、表示テキストとそれにふるルビを区別するための区切り文字を返却する
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
	 * text要素において、表示テキストのうちルビをふるべき文字列かそうでないかを区別するための囲み文字を返却する
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
	 * 最新のデータベースバージョンを保存する<br />
	 * getCurrentDbVersionと併せて使い、起動時に不要なINSERTを発生させないようにする
	 * @param dbVersion	現在のデータベースバージョン
	 */
	public void setCurrentDbVersion(int dbVersion) {
		mCurrentDbVersion = dbVersion;

		getPreferences().edit()
				.putInt(mContext.getString(R.string.prefkey_current_db_version), mCurrentDbVersion)
				.apply();
	}

	/**
	 * アプリが最新のデータベースバージョンとして保持している値を返却する
	 * setCurrentDbVersionと併せて使い、起動時に不要なINSERTを発生させないようにする
	 * @return	現在のデータベースバージョン
	 */
	public int getCurrentDbVersion() {
		return getPreferences().getInt(mContext.getString(R.string.prefkey_current_db_version), 0);
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
	 * 章番号の最大値を返却する
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
	 * slotIndexに指定したインデックスに保存されているSaveDataを返却する
	 * @param slotIndex	セーブスロットを示すインデックス値
	 * @return	SaveDataオブジェクト
	 */
	public SaveData getSaveData(int slotIndex) {
		return mSaveDatas[slotIndex];
	}

	/**
	 * スロット番号slotIndexにsaveDataをセットする
	 * @param slotIndex スロット番号
	 * @param saveData  セーブデータ
	 */
	public void setSaveData(int slotIndex, SaveData saveData) {
		mSaveDatas[slotIndex] = saveData;
	}

	/**
	 * チュートリアルが終了したことを記録する
	 */
	public void markAsTutorialFinished() {
		getPreferences().edit()
				.putBoolean(mContext.getString(R.string.prefkey_tutorial_finished), true)
				.apply();
	}

	/**
	 * チュートリアルが終了しているかを返却する
	 * @return  チュートリアルが済んでいればtrue
	 */
	public boolean isTutorialFinished() {
		return getPreferences().getBoolean(mContext.getString(R.string.prefkey_tutorial_finished), false);
	}

	/**
	 * テキストの描画サイズを返却する
	 * @return  テキストの描画サイズ(px)
	 */
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

	/**
	 * ルビの描画サイズを返却する
	 * @return  ルビの描画サイズ(px)
	 */
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

	/**
	 * テキストの描画間隔を返却する
	 * @return  テキストの描画間隔(ms)
	 */
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
