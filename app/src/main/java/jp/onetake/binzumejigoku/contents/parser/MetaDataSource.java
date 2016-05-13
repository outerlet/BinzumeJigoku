package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsHandler;
import jp.onetake.binzumejigoku.contents.common.ContentsType;

public class MetaDataSource extends Source {
	/**
	 * コンストラクタ
	 * @param context  コンテキスト
	 * @param parser   XMLの解析を担当するパーサー
	 * @param database コンテンツの各要素を保存するためのDBオブジェクト
	 */
	public MetaDataSource(Context context, XmlPullParser parser, SQLiteDatabase database) {
		super(context, parser, database);
	}

	@Override
	public void parse() throws IOException, XmlPullParserException {
		super.parse();

		ContentsHandler handler = ContentsHandler.getInstance();
		handler.setRubyDelimiter(getAttribute("ruby_delimiter"));
		handler.setRubyClosure(getAttribute("ruby_closure"));
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.MetaData;
	}
}
