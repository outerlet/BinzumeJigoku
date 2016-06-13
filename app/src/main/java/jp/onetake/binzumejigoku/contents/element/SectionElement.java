package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;

/**
 * 各セクションを構成する要素を表現するオブジェクト
 */
public abstract class SectionElement extends Element {
	public enum ChainType {
		None,
		Wait,
		Immediate;

		public static ChainType getValue(String chainText) {
			for (ChainType c : ChainType.values()) {
				if (chainText.equalsIgnoreCase(c.toString())) {
					return c;
				}
			}

			return None;
		}
	}

	private int mSectionIndex;
	private int mSequence;
	private ChainType mChainType;

	/**
	 * コンストラクタ
	 * @param context		コンテキスト
	 * @param sectionIndex	このオブジェクトが属するセクションのインデックス値
	 * @param sequence		通し番号
	 */
	public SectionElement(Context context, int sectionIndex, int sequence) {
		super(context);

		mSectionIndex = sectionIndex;
		mSequence = sequence;
	}

	/**
	 * セクションに割り当てられたインデックスを取得する<br />
	 * このインデックスと通し番号で一意の要素を特定できる
	 * @return	セクションに割り当てられたインデックス
	 */
	public int getSectionIndex() {
		return mSectionIndex;
	}

	/**
	 * セクション内での通し番号を取得する<br />
	 * この通し番号とインデックスで一意の要素を特定できる
	 * @return	セクション内での通し番号
	 */
	public int getSequence() {
		return mSequence;
	}

	/**
	 * 次の要素を連続して実行する"chain"要素の値を取得する
	 * @return	"chain"要素の値に応じたChainType列挙値
	 */
	public ChainType getChainType() {
		return mChainType;
	}

	/**
	 * オブジェクトが保持している情報のうち必要なものをデータベースに保存する<br />
	 * セクションのインデックスと通し番号、コンテンツの種類はここで保存し、それ以外のものはサブクラスごとに保存する
	 * @param db		登録に利用するデータベースオブジェクト。Writableで開かれている必要がある
	 * @param values	insertに利用する値オブジェクト
	 */
	public void save(SQLiteDatabase db, ContentValues values) {
		values.put(ContentsTable.SECTION.getColumnName(), getSectionIndex());
		values.put(ContentsTable.SEQUENCE.getColumnName(), getSequence());
		values.put(ContentsTable.TYPE.getColumnName(), getContentsType().toString());

		String chain = getAttribute("chain");
		values.put(ContentsTable.COMMON0.getColumnName(), (chain != null) ? chain : ChainType.None.toString());

		db.insert(getContext().getString(R.string.db_contents_table_name), null, values);
	}

	/**
	 * cursorからデータを読み出してオブジェクト内に保持する
	 * @param cursor	データベースからコンテンツを読みだすカーソルオブジェクト
	 */
	public void load(Cursor cursor) {
		mChainType = ChainType.getValue(
				cursor.getString(ContentsTable.getColumnIndex(ContentsTable.COMMON0)));
	}

	/**
	 * このオブジェクトの文字列表現
	 * @return	文字列表現
	 */
	public String toString() {
		return getContentsType().toString();
	}
}
