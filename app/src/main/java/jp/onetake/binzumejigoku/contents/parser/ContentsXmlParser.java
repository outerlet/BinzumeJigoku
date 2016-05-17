package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.element.MetaData;
import jp.onetake.binzumejigoku.contents.element.Section;

/**
 * コンテンツ(=ストーリー)が定義されているXMLを解析しDBに登録するパーサクラス
 */
public class ContentsXmlParser extends ContentsParser {
	/**
	 * コンストラクタ
	 * @param context
	 */
	public ContentsXmlParser(Context context) {
		super(context);
	}

	/**
	 * assetsディレクトリにあるfileNameという名前のXMLファイルを解析してDBに登録する
	 * @param fileName	解析するXMLファイル名
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parse(String fileName) throws IOException, XmlPullParserException {
		InputStreamReader reader = new InputStreamReader(getContext().getAssets().open(fileName));

		// XML解析のためのパーサを取得
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(reader);

		// XMLを解析した結果をDBに書き込むためのヘルパ
		// DB更新処理に失敗した場合に備えてトランザクションを張っておく
		SQLiteDatabase db = ContentsInterface.getInstance().getWritableDatabase();
		db.beginTransaction();

		try {
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG) {
					switch (ContentsType.getValue(parser.getName())) {
						case MetaData:
							(new MetaData(getContext())).parse(parser);
							break;
						case Section:
							Section section = new Section(getContext());
							section.parse(parser);
							section.save(db);
							break;
						default:
							break;
					}
				}
			}

			// パースが正常に完了した
			ContentsInterface.getInstance().markAsXmlParsed();

			db.setTransactionSuccessful();
		} catch (IOException | XmlPullParserException | SQLiteException ex) {
			ex.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}

		if (getListener() != null) {
			getListener().onParseFinished();
		}
	}
}
