package jp.onetake.binzumejigoku.contents.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;
import jp.onetake.binzumejigoku.contents.element.ClearText;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;

/**
 * SectionのIndex値に応じたセクションのコンテンツをDBから読み取り、それぞれに応じた要素オブジェクトを生成・保持するクラス
 */
public class ContentsHolder {
	private Context mContext;
	private List<SectionElement> mElementList;
	private int mCurrentSequence;
	private SaveData mSaveData;

	/**
	 * 章を最初から始める場合のコンストラクタ<br />
	 * @param context       コンテキスト
	 * @param sectionIndex  章番号
	 */
	public ContentsHolder(Context context, int sectionIndex) {
		mContext = context;
		mCurrentSequence = -1;

		mSaveData = new SaveData(0);
		mSaveData.setSectionIndex(sectionIndex);
		ContentsInterface.getInstance().setSaveData(0, mSaveData);

		mElementList = parse(sectionIndex);
	}

	/**
	 * セーブデータを使って途中から始めるためのコンストラクタ
	 * @param context   コンテキスト
	 * @param saveData	セーブデータ
	 */
	public ContentsHolder(Context context, SaveData saveData) {
		mContext = context;
		mCurrentSequence = saveData.getSequence();

		mSaveData = ContentsInterface.getInstance().getSaveData(0);
		mSaveData.copyFrom(saveData, false);
		mSaveData.setSectionIndex(saveData.getSectionIndex());

		mElementList = parse(saveData.getSectionIndex());
	}

	/**
	 * saveDataに保存された状態までコンテンツを進行させ、その時点に戻すために
	 * 復元すべき要素オブジェクトを全て返却する
	 * @return  セーブデータが保存された時点で有効な要素オブジェクトのリスト
	 */
	public List<SectionElement> getLatestElementList() {
		List<SectionElement> list = new ArrayList<>();

		Map<ContentsType, Integer> map = mSaveData.getContentsSequence();

		for (ContentsType type : map.keySet()) {
			list.add(mElementList.get(map.get(type)));
		}

		return list;
	}

	/**
	 * コンテンツが進行可能かどうか
	 * @return  進行可能ならtrue
	 */
	public boolean hasNext() {
		return (mCurrentSequence + 1 < mElementList.size());
	}

	/**
	 * コンテンツをひとつ進行させ、次の要素オブジェクトを返却する<br />
	 * 併せて最新の状態をセーブする
	 * @return  次の要素オブジェクト
	 */
	public SectionElement next() {
		SectionElement elm = mElementList.get(++mCurrentSequence);

		// ActivityやFragmentのonPause等のライフサイクルイベントでセーブするとアプリが突然死したときに
		// 復元できないのでコンテンツが進行するごとにセーブする
		mSaveData.setTimeMillis(System.currentTimeMillis());
		mSaveData.setLatestElement(elm);
		mSaveData.save(mContext);

		return elm;
	}

	/**
	 * コンテンツを進行させることなく現在の要素オブジェクトを返却する
	 * @return  現在の要素オブジェクト
	 */
	public SectionElement current() {
		return (mCurrentSequence >= 0) ? mElementList.get(mCurrentSequence) : null;
	}

	/**
	 * sectionIndexに指定したセクションインデックスに該当するセクション要素をDBから読み出して適切な要素オブジェクトを生成する
	 * @param sectionIndex	セクションに割り当てられたインデックス値
	 */
	private List<SectionElement> parse(int sectionIndex) throws SQLiteException {
		List<SectionElement> list = new ArrayList<>();

		try (SQLiteDatabase db = ContentsInterface.getInstance().getReadableDatabase();
			 Cursor cursor = db.rawQuery(
					 mContext.getString(R.string.db_query_contents_table_sql), new String[] { Integer.toString(sectionIndex) })) {
			cursor.moveToFirst();

			for (int i = 0 ; i < cursor.getCount() ; i++) {
				SectionElement elm;

				ContentsType type = ContentsType.getValue(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.TYPE)));
				int sequence = Integer.parseInt(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.SEQUENCE)));

				switch (type) {
					case Image:
						elm = new Image(mContext, sectionIndex, sequence);
						break;
					case Title:
						elm = new Title(mContext, sectionIndex, sequence);
						break;
					case Text:
						elm = new Text(mContext, sectionIndex, sequence);
						break;
					case ClearText:
						elm = new ClearText(mContext, sectionIndex, sequence);
						break;
					default:
						continue;
				}

				elm.load(cursor);

				list.add(elm);

				cursor.moveToNext();
			}
		}

		return list;
	}
}
