package jp.onetake.binzumejigoku.contents.element;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.ContentsType;

/**
 * コンテンツ全体に関わるメタ情報を制御する要素クラス
 */
public class MetaData extends Element {
	/**
	 * コンストラクタ
	 * @param context  コンテキスト
	 */
	public MetaData(Context context) {
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
		super.parse(parser);

		ContentsInterface cif = ContentsInterface.getInstance();
		cif.setRubyDelimiter(getAttribute("ruby_delimiter"));
		cif.setRubyClosure(getAttribute("ruby_closure"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentsType getContentsType() {
		return ContentsType.MetaData;
	}
}
