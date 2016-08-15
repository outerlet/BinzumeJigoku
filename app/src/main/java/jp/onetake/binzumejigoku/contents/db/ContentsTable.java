package jp.onetake.binzumejigoku.contents.db;

/**
 * XMLをパースして得られたコンテンツデータは一旦DBに保存されるが、そのテーブルの列を示す列挙値
 */
public enum ContentsTable {
	SECTION(0),			// セクション番号
	SEQUENCE(1),		// セクション内の通し番号
	TYPE(2),			// コンテンツの種類
	COMMON0(3),			// 共通0
	COMMON1(4),			// 共通1
	COMMON2(5),			// 共通2
	VALUE0(6),			// コンテンツ固有の値0
	VALUE1(7),			// コンテンツ固有の値1
	VALUE2(8),			// コンテンツ固有の値2
	VALUE3(9),			// コンテンツ固有の値3
	VALUE4(10),			// コンテンツ固有の値4
	VALUE5(11),			// コンテンツ固有の値5
	VALUE6(12),			// コンテンツ固有の値6
	VALUE7(13),			// コンテンツ固有の値7
	VALUE8(14),			// コンテンツ固有の値8
	VALUE9(15),			// コンテンツ固有の値9
	CONTENTS_TEXT(16),	// コンテンツがテキストの場合はその文字列
	UNKNOWN(-1);		// 不明(便宜上の値)

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
