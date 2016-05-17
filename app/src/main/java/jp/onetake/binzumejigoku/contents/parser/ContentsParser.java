package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;

/**
 * コンテンツのパースに関する処理を担当する各クラスの基底クラス
 */
public class ContentsParser {
	private Context mContext = null;
	private ParserListener mListener = null;

	/**
	 * コンストラクタ
	 * @param context
	 */
	public ContentsParser(Context context) {
		mContext = context;
	}

	/**
	 * パースが終了したイベントをハンドリングするためのリスナをセットする
	 * @param listener	パースに関するイベントをハンドルするリスナ
	 */
	public void setListener(ParserListener listener) {
		mListener = listener;
	}

	/**
	 * パースが終了したイベントをハンドリングするためのリスナを取得
	 * @return	パースに関するイベントをハンドルするリスナ
	 */
	protected ParserListener getListener() {
		return mListener;
	}

	/**
	 * コンテキストオブジェクトの取得
	 * @return	コンテキストオブジェクト
	 */
	protected Context getContext() {
		return mContext;
	}

	/**
	 * stringリソースIDで定義済みの文字列を取得する
	 * (Context.getStringのショートカット)
	 * @param resId	stringリソースID
	 * @return	string.xml等に定義済みの文字列
	 */
	protected String getString(int resId) {
		return mContext.getString(resId);
	}

	/**
	 * XMLのパースに関するイベントをハンドルするリスナクラス
	 */
	public interface ParserListener {
		/**
		 * パースが完了した時に呼び出される
		 */
		void onParseFinished();
	}
}
