package jp.onetake.binzumejigoku.util;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 便利メソッドの寄せ集めクラス
 */
public class Utility {
	/** 全角空白 */
	private static final char WHITE_SPACE_JP = '　';

	/** 全角空白を前後に含んだ文字列からそれら（全角空白）を除くための正規表現 */
	private static final String REGEX_TRIM =
			"^" + WHITE_SPACE_JP + "*?([^" + WHITE_SPACE_JP + "].+[^" + WHITE_SPACE_JP + "])" + WHITE_SPACE_JP + "*?\\z";

	/**
	 * 文字列sourceから改行ごとに含まれるタブや文字列の前後に含まれる全角空白などを取り去って整形された文字列を返却する
	 * @param source	整形したい文字列
	 * @return	整形された新しい文字列
	 */
	public static String format(String source) {
		// 複数の改行を区切り文字にして文字列を分割
		String[] lines = source.split("\\t+");

		// 分割した各文字列の全角空白をトリミング
		// substringとかでものすごーくベタにやってもいいんですが、どうせなので正規表現を使ってみました
		StringBuilder formatted = new StringBuilder();
		for (int i = 0 ; i < lines.length ; i++) {
			if (i > 0 && formatted.length() > 0) {
				formatted.append("\n");
			}

			String str = lines[i].trim();
			if (!TextUtils.isEmpty(str)) {
				Matcher matcher = Pattern.compile(REGEX_TRIM).matcher(str);
				formatted.append(matcher.matches() ? matcher.group(1) : str);
			}
		}

		return formatted.toString();
	}

	/**
	 * ログにメッセージを出力する<br />
	 * ログを出す箇所に全てandroid.util.Logをインポートするのも何なのでここに集約
	 * @param tag		タグ文字列
	 * @param message	ログに出力するメッセージ文字列
	 * @param logLevel	出力するログのレベル。Logクラスの定数を与える。省略可能、デフォルトはLog.INFO
	 */
	public static void printLog(String tag, String message, int... logLevel) {
		int level = (logLevel.length > 0) ? logLevel[0] : Log.INFO;

		switch (level) {
			case Log.DEBUG:
				Log.d(tag, message);
				break;
			case Log.ERROR:
				Log.e(tag, message);
				break;
			case Log.VERBOSE:
				Log.v(tag, message);
				break;
			case Log.WARN:
				Log.w(tag, message);
				break;
			default:
				Log.i(tag, message);
		}
	}
}
