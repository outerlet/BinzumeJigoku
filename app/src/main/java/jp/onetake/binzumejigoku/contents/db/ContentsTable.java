package jp.onetake.binzumejigoku.contents.db;

/**
 * XMLをパースして得られたコンテンツデータは一旦DBに保存されるが、そのテーブルの列を示す列挙値
 */
public enum ContentsTable {
	/** セクション番号 */
	SECTION(0),
	/** セクション内の通し番号 */
	SEQUENCE(1),
	/** コンテンツの種類 */
	TYPE(2),
	/** 共通0 */
	COMMON0(3),
	/** 共通1 */
	COMMON1(4),
	/** 共通2 */
	COMMON2(5),
	/** コンテンツ固有の値0 */
	VALUE0(6),
	/** コンテンツ固有の値1 */
	VALUE1(7),
	/** コンテンツ固有の値2 */
	VALUE2(8),
	/** コンテンツ固有の値3 */
	VALUE3(9),
	/** コンテンツ固有の値4 */
	VALUE4(10),
	/** コンテンツ固有の値5 */
	VALUE5(11),
	/** コンテンツ固有の値6 */
	VALUE6(12),
	/** コンテンツ固有の値7 */
	VALUE7(13),
	/** コンテンツ固有の値8 */
	VALUE8(14),
	/** コンテンツ固有の値9 */
	VALUE9(15),
	/** コンテンツがテキストの場合はその文字列 */
	CONTENTS_TEXT(16),
	/** 不明(便宜上の値) */
	UNKNOWN(-1);

	private final int mColumnIndex;

	ContentsTable(int columnIndex) {
		mColumnIndex = columnIndex;
	}

	/**
	 * テーブルの列名を返却する
	 * @return	列名
	 */
	public String toColumnName() {
		return toString().toUpperCase();
	}

	/**
	 * ContentsTable列挙値に対応する列のインデックスを返却する
	 * @return	列のインデックス
	 */
	public int toColumnIndex() {
		return mColumnIndex;
	}
}
