package jp.onetake.binzumejigoku.contents.common;

/**
 * コンテンツを構成する要素オブジェクトを示す列挙値
 */
public enum ContentsType {
	/** 不明(便宜的な値) */
	Unknown,
	/** メタデータ */
	MetaData,
	/** セクション */
	Section,
	/** 章タイトル */
	Title,
	/** 背景画像 */
	Image,
	/** テキスト */
	Text,
	/** 指定された時間だけユーザーの操作を待機する */
	Wait,
	/** 表示されているテキストを全てクリアする */
	ClearText;

	/**
	 * textと等価のContentsType列挙値を取得する
	 * @param text	列挙値を取得するための文字列
	 * @return	ContentsType列挙値
	 */
	public static ContentsType getValue(String text) {
		if (text != null) {
			for (ContentsType type : values()) {
				if (type.equals(text.replaceAll("-+", ""))) {
					return type;
				}
			}
		}

		return Unknown;
	}

	/**
	 * セーブ時にキー文字列として使用する文字列を取得する
	 * @return	セーブ時のキー文字列
	 */
	public String getKeyString() {
		return this.toString().toUpperCase();
	}

	/**
	 * この要素の保持する状態をセーブデータに保存すべきかどうか
	 * @return	保存すべきであればtrue
	 */
	public boolean shouldSave() {
		// 一応TextやTitleも保存できるがテキストを復元するとプレイ時に違和感があるのでImageだけに変更
		return (this == Image);
	}

	/**
	 * nameに与えた文字列が列挙値名と等しいかどうか検証する
	 * @param name	検証するための文字列
	 * @return	nameと列挙値名が等しければtrue
	 */
	public boolean equals(String name) {
		return name.equalsIgnoreCase(this.toString());
	}
}
