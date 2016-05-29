package jp.onetake.binzumejigoku.contents.element;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.onetake.binzumejigoku.contents.common.ContentsType;

/**
 * ストーリーが記録されているXMLの各要素に対応する基底クラス
 */
public abstract class Element {
	private Context mContext;
	private Map<String, String> mAttributeMap;

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 */
	public Element(Context context) {
		mContext = context;
		mAttributeMap = new HashMap<>();
	}

	/**
	 * 各要素のパースを実行する
	 * 要素ごとに固有のパース処理はこのメソッドをオーバーライドして定義
	 * 処理しようとする要素に対応したクラスでないか、mXmlParserの位置がタグの先頭にない場合はXmlPullParserExceptionがスローされる
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
		if (parser.getEventType() != XmlPullParser.START_TAG || ContentsType.getValue(parser.getName()) != getContentsType()) {
			throw new XmlPullParserException("Cannot parse contents XML : NOT START_TAG or Invalid contents type");
		}

		// 属性値があれば取得
		if (parser.getAttributeCount() > 0) {
			for (int i = 0 ; i < parser.getAttributeCount() ; i++) {
				mAttributeMap.put(parser.getAttributeName(i), parser.getAttributeValue(i));
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

	/**
	 * XmlPullParserのnextメソッドを呼び出して読み込み位置を先に進める
	 * クラスに対応した要素の閉じタグに到達した場合はfalseを返す
	 * @return	次の要素に進めるならtrue、要素の閉じタグに到達した場合はfalse
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	protected boolean hasNext(XmlPullParser parser) throws IOException, XmlPullParserException {
		return (parser.next() != XmlPullParser.END_TAG || ContentsType.getValue(parser.getName()) != getContentsType());
	}

	/**
	 * コンテキストオブジェクトを取得する
	 * @return	コンテキストオブジェクト
	 */
	protected Context getContext() {
		return mContext;
	}

	/**
	 * このクラスが処理すべき要素に対応するContentsTypeを返却する
	 * @return	このクラスが処理すべき要素に対応するContentsType
	 */
	public abstract ContentsType getContentsType();
}
