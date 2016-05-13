package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.onetake.binzumejigoku.contents.common.ContentsType;

/**
 * ストーリーが記録されているXMLをパースする際、各要素に対応したオブジェクトを生成するがそれらに共通するロジックを定義した基底クラス
 */
public abstract class Source {
	private Map<String, String> mAttributeMap;
	private Context mContext;
	private XmlPullParser mXmlParser;
	private SQLiteDatabase mDatabase;

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param parser	XMLの解析を担当するパーサー
	 * @param database	コンテンツの各要素を保存するためのDBオブジェクト
	 */
	public Source(Context context, XmlPullParser parser, SQLiteDatabase database) {
		mContext = context;
		mXmlParser = parser;
		mDatabase = database;
	}

	/**
	 * 各要素のパースを実行する
	 * 要素ごとに固有のパース処理はこのメソッドをオーバーライドして定義
	 * 処理しようとする要素に対応したクラスでないか、mXmlParserの位置がタグの先頭にない場合はXmlPullParserExceptionがスローされる
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parse() throws IOException, XmlPullParserException {
		if (mXmlParser.getEventType() != XmlPullParser.START_TAG || ContentsType.getValue(mXmlParser.getName()) != getContentsType()) {
			throw new XmlPullParserException("Cannot parse contents XML : NOT START_TAG or Invalid contents type");
		}

		// 属性値の取得
		if (mXmlParser.getAttributeCount() > 0) {
			mAttributeMap = new HashMap<>();

			for (int i = 0 ; i < mXmlParser.getAttributeCount() ; i++) {
				mAttributeMap.put(mXmlParser.getAttributeName(i), mXmlParser.getAttributeValue(i));
			}
		}
	}

	/**
	 * nameに対応する属性値を取得する
	 * @param name	属性名
	 * @return	属性値
	 */
	public String getAttribute(String name) {
		return mAttributeMap.get(name);
	}

	protected Context getContext() {
		return mContext;
	}

	/**
	 * XMLの解析に使っているパーサを返却する
	 * @return	XMLパーサ
	 */
	protected XmlPullParser getXmlParser() {
		return mXmlParser;
	}

	/**
	 * コンテンツを保存するためのデータベースを取得する
	 * @return	データベース
	 */
	protected SQLiteDatabase getDatabase() {
		return mDatabase;
	}

	/**
	 * XmlPullParserのnextメソッドを呼び出して読み込み位置を先に進める
	 * クラスに対応した要素の閉じタグに到達した場合はfalseを返す
	 * @return	次の要素に進めるならtrue、要素の閉じタグに到達した場合はfalse
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	protected boolean hasNext() throws IOException, XmlPullParserException {
		return (mXmlParser.next() != XmlPullParser.END_TAG || ContentsType.getValue(mXmlParser.getName()) != getContentsType());
	}

	/**
	 * このクラスが処理すべき要素に対応するContentsTypeを返却する
	 * @return	このクラスが処理すべき要素に対応するContentsType
	 */
	protected abstract ContentsType getContentsType();
}
