package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;

/**
 * 1つのセーブデータを表現するクラス
 */
public class SaveData implements Serializable {
	private static final long serialVersionUID = -3692542681255401454L;

	private int mSlotIndex;
	private int mSectionIndex;
	private int mSequence;
	private String mName;
	private long mTimeMillis;
	private Map<String, Integer> mRestoreMap;
	private List<String> mBacklogList;

	private transient boolean mHasSaved;

	/**
	 * stringリソースに定義されている、slotIndexに対応するセーブデータのタイトルを取得する
	 * @param context	コンテキスト
	 * @param slotIndex	セーブスロットの番号(0-)
	 * @return	セーブデータのタイトル
	 */
	public static String getSaveName(Context context, int slotIndex) {
		int strId = context.getResources().getIdentifier("name_save_data" + slotIndex, "string", context.getPackageName());
		return context.getString(strId);
	}

	/**
	 * コンストラクタ
	 * @param slotIndex セーブスロットの番号(0-)
	 */
	public SaveData(int slotIndex) {
		mSlotIndex = slotIndex;
		mRestoreMap = new HashMap<>();
		mBacklogList = new ArrayList<>();
	}

	/**
	 * セクション番号(0-)をセットする
	 * @param sectionIndex	セクション番号(0-)
	 */
	public void setSectionIndex(int sectionIndex) {
		mSectionIndex = sectionIndex;
	}

	/**
	 * セーブスロットの番号を取得する
	 * @return	セーブスロットの番号
	 */
	public int getSlotIndex() {
		return mSlotIndex;
	}

	/**
	 * セーブデータのセクション番号を返却する
	 * @return  このオブジェクトが保持するセクション番号
	 */
	public int getSectionIndex() {
		return mSectionIndex;
	}

	/**
	 * セーブデータのシーケンス番号を返却する<br />
	 * シーケンス番号は、セクション中のどこまで物語が進行したかを示す通し番号。セクション番号＋シーケンス番号で一意
	 * @return  このオブジェクトが保持するシーケンス番号
	 */
	public int getSequence() {
		return mSequence;
	}

	/**
	 * セーブデータの名前をセットする
	 * @param name	セーブデータの名前
	 */
	public void setName(String name) {
		mName = name;
	}

	/**
	 * セーブデータの名前を返却する
	 * @return	セーブデータの名前
	 */
	public String getName() {
		return mName;
	}

	/**
	 * セーブした時間をセットする
	 * @param timeMillis	セーブした時間。エポックからのミリ秒
	 */
	public void setTimeMillis(long timeMillis) {
		mTimeMillis = timeMillis;
	}

	/**
	 * セーブした時間を示す文字列を「yyyy/MM/dd hh:mm:ss」形式で返却する
	 * @return	セーブした時間を示す文字列
	 */
	public String getTimeText() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(mTimeMillis);

		return String.format(
				Locale.US,
				"%1$d/%2$02d/%3$02d %4$02d:%5$02d:%6$02d",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	/**
	 * このセーブデータでセーブが行われたことがあるかを返却する
	 * @return	このセーブデータでセーブが行われたことがあればtrue
	 */
	public boolean hasSaved() {
		return mHasSaved;
	}

	/**
	 * 復元すべき要素の種類とシーケンス番号のペアを複数格納したMapを返却する
	 * @return  復元すべき要素の種類とシーケンス番号のペアを複数格納したMap
	 */
	public Map<ContentsType, Integer> getContentsSequence() {
		Map<ContentsType, Integer> map = new HashMap<>();

		for (String key : mRestoreMap.keySet()) {
			map.put(ContentsType.getValue(key), mRestoreMap.get(key));
		}

		return map;
	}

	/**
	 * セーブされた時点までのバックログを文字列のリストで返却する
	 * @return	バックログのリスト
	 */
	public List<String> getBacklogList() {
		return mBacklogList;
	}

	/**
	 * 最新の要素オブジェクトをセットする
	 * @param elm   要素オブジェクト
	 */
	public void setLatestElement(SectionElement elm) {
		// 要素の保存
		if (elm.getContentsType().shouldSave()) {
			mRestoreMap.put(elm.getContentsType().getKeyString(), elm.getSequence());
		}

		// バックログテキストの保存
		if (elm.getContentsType() == ContentsType.Text) {
			mBacklogList.add(((Text)elm).getPlainText());
		}

		mSequence = elm.getSequence();
	}

	/**
	 * slotNumberに指定した番号にセーブデータを保存する
	 * @return  セーブに成功したらtrue
	 */
	public boolean save(Context context) {
		try (ObjectOutputStream oos =
					 new ObjectOutputStream(context.openFileOutput(getFileName(context), Context.MODE_PRIVATE))) {
			oos.writeObject(this);

			mHasSaved = true;
		} catch (IOException ie) {
			ie.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * slotNumberに指定した番号のセーブデータを読み出す
	 * @return  ロードに成功したらtrue
	 */
	public boolean load(Context context) {
		try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(getFileName(context)))) {
			copyFrom((SaveData) ois.readObject(), true);
			mHasSaved = true;

			return true;
		} catch (FileNotFoundException fne) {
			// ファイルが見つからないだけなら何もしない
		} catch (IOException | ClassNotFoundException ice) {
			ice.printStackTrace();
		}

		return false;
	}

	/**
	 * otherからこのオブジェクトにセーブデータの中身をコピーする
	 * @param other			コピー元となるセーブデータ
	 * @param containsName	セーブデータの名前もコピーするかどうか。trueなら含める
	 */
	public void copyFrom(SaveData other, boolean containsName) {
		this.mSectionIndex = other.mSectionIndex;
		this.mSequence = other.mSequence;
		this.mTimeMillis = other.mTimeMillis;

		if (containsName) {
			this.mName = other.mName;
		}

		for (String key : other.mRestoreMap.keySet()) {
			this.mRestoreMap.put(key, other.mRestoreMap.get(key));
		}

		this.mBacklogList.clear();
		for (String log : other.mBacklogList) {
			this.mBacklogList.add(log);
		}
	}

	/**
	 * slotNumberに対応するセーブファイル名を取得する
	 * @return  セーブファイル名
	 */
	private String getFileName(Context context) {
		return String.format(Locale.JAPANESE, context.getString(R.string.filename_save_data_format), mSlotIndex);
	}
}
