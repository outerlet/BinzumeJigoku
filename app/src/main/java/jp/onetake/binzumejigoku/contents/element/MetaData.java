package jp.onetake.binzumejigoku.contents.element;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsHandler;
import jp.onetake.binzumejigoku.contents.common.ContentsType;

public class MetaData extends Element {
	/**
	 * コンストラクタ
	 * @param context  コンテキスト
	 */
	public MetaData(Context context) {
		super(context);
	}

	@Override
	public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
		super.parse(parser);

		ContentsHandler handler = ContentsHandler.getInstance();
		handler.setRubyDelimiter(getAttribute("ruby_delimiter"));
		handler.setRubyClosure(getAttribute("ruby_closure"));
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.MetaData;
	}
}
