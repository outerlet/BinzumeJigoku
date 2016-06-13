package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;

/**
 * セーブデータ
 */
public class SaveData implements Serializable {
	private static final long serialVersionUID = -3692542681255401454L;

	private final String FILENAME_FORMAT	= "binzume-jigoku_%1$02d";
	private final int MAX_SEQUENCE_NUMBER	= 10000;

	private int mSlotIndex;
	private int mSectionIndex;
	private int mSequence;
	private String mName;
	private long mTimeMillis;
	private HashMap<String, int[]> mRestoreMap;
	private boolean mHasSaved;

	public static String getSaveName(Context context, int slotIndex) {
		int strId = context.getResources().getIdentifier("name_save_data" + slotIndex, "string", context.getPackageName());
		return context.getString(strId);
	}

	/**
	 * コンストラクタ
	 */
	public SaveData(int slotIndex) {
		mSlotIndex = slotIndex;
		mRestoreMap = new HashMap<>();
	}

	public void setSectionIndex(int sectionIndex) {
		mSectionIndex = sectionIndex;
	}

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

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public void setTimeMillis(long timeMillis) {
		mTimeMillis = timeMillis;
	}

	public String getTimeText() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(mTimeMillis);

		return String.format("%1$d/%2$02d/%3$02d %4$02d:%5$02d:%6$02d",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	public boolean hasSaved() {
		return mHasSaved;
	}

	/**
	 * このセーブデータから復元すべき要素オブジェクトの種類を全て返却する
	 * @return  要素オブジェクトの種類
	 */
	public List<ContentsType> getContentsTypeList() {
		List<ContentsType> keyList = new ArrayList<>();
		for (String key : mRestoreMap.keySet()) {
			keyList.add(ContentsType.getValue(key));
		}

		return keyList;
	}

	/**
	 * 要素オブジェクトの種類に対応するシーケンス番号を配列で取得する<br />
	 * Image要素は複数のレイヤーがあるのでlength>=1の配列、それ以外の要素はlength==1の配列
	 * @param type  要素オブジェクトの種類
	 * @return  シーケンス番号の配列
	 */
	public int[] getContentsSequence(ContentsType type) {
		for (String key : mRestoreMap.keySet()) {
			if (type == ContentsType.getValue(key)) {
				if (type == ContentsType.Image) {
					int[] imageNumbers = mRestoreMap.get(key);

					int[] seqs = new int[imageNumbers.length];
					int idx = 0;
					for (int imageNumber : imageNumbers) {
						seqs[idx++] = imageNumber % MAX_SEQUENCE_NUMBER;
					}

					return seqs;
				} else {
					return mRestoreMap.get(key);
				}
			}
		}

		return null;
	}

	/**
	 * 最新の要素オブジェクトをセットする
	 * @param elm   要素オブジェクト
	 */
	public void setLatestElement(SectionElement elm) {
		if (elm.getContentsType().shouldSave()) {
			// Image要素については1つのレイヤに複数の番号は要らないので別扱い
			if (elm instanceof Image) {
				Image image = (Image)elm;

				// 被らないように4桁目以下はシーケンス番号、5桁目以上をレイヤ番号とする
				int imageNumber = image.getLayer() * MAX_SEQUENCE_NUMBER + image.getSequence();

				if (mRestoreMap.containsKey(ContentsType.Image.getKeyString())) {
					int[] imageNumbers = mRestoreMap.get(ContentsType.Image.getKeyString());

					boolean found = false;
					for (int i = 0 ; i < imageNumbers.length ; i++) {
						if (image.getLayer() == imageNumbers[i] / MAX_SEQUENCE_NUMBER) {
							imageNumbers[i] = imageNumber;
							found = true;
							break;
						}
					}

					if (!found) {
						int[] temp = Arrays.copyOf(imageNumbers, imageNumbers.length + 1);
						temp[temp.length - 1] = imageNumber;

						mRestoreMap.put(ContentsType.Image.getKeyString(), temp);
					}
				} else {
					mRestoreMap.put(
							ContentsType.Image.getKeyString(), new int[] { imageNumber });
				}
			} else {
				mRestoreMap.put(elm.getContentsType().getKeyString(), new int[] { elm.getSequence() });
			}
		}

		mSequence = elm.getSequence();
	}

	/**
	 * slotNumberに指定した番号にセーブデータを保存する
	 * @return  セーブに成功したらtrue
	 */
	public boolean save(Context context) {
		try (ObjectOutputStream oos =
					 new ObjectOutputStream(context.openFileOutput(getFileName(), Context.MODE_PRIVATE))) {
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
		try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(getFileName()))) {
			copyFrom((SaveData) ois.readObject(), true);
			mHasSaved = true;

			return true;
		} catch (FileNotFoundException fne) {
			// ファイルが見つからないだけなら何もしない
			fne.printStackTrace();
		} catch (IOException | ClassNotFoundException ice) {
			ice.printStackTrace();
		}

		return false;
	}

	public void copyFrom(SaveData other, boolean setName) {
		this.mSectionIndex = other.mSectionIndex;
		this.mSequence = other.mSequence;
		this.mTimeMillis = other.mTimeMillis;

		if (setName) {
			this.mName = other.mName;
		}

		for (String key : other.mRestoreMap.keySet()) {
			this.mRestoreMap.put(key, other.mRestoreMap.get(key));
		}
	}

	/**
	 * slotNumberに対応するセーブファイル名を取得する
	 * @return  セーブファイル名
	 */
	private String getFileName() {
		return String.format(Locale.JAPANESE, FILENAME_FORMAT, mSlotIndex);
	}
}
