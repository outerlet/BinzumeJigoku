package jp.onetake.binzumejigoku.exception;

/**
 * このアプリ独自の例外クラス
 */
public class ContentsException extends Exception {
	/**
	 * コンストラクタ
	 * @param message	例外メッセージ
	 */
	public ContentsException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ
	 * @param throwable	この例外クラスが内部に持つ例外オブジェクト
	 */
	public ContentsException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * コンストラクタ
	 * @param message	例外メッセージ
	 * @param cause		この例外クラスが内部に持つ例外オブジェクト
	 */
	public ContentsException(String message, Throwable cause) {
		super(message, cause);
	}
}
