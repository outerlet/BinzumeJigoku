package jp.onetake.binzumejigoku.contents.xml;

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
public class ContentsXmlParser {
	private Context mContext;
	private ParserListener mListener = null;

	/**
	 * コンストラクタ
	 * @param context
	 */
	public ContentsXmlParser(Context context) {
		mContext = context;
	}

	/**
	 * assetsディレクトリにあるfileNameという名前のXMLファイルを解析してDBに登録する
	 * @param fileName	解析するXMLファイル名
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void parse(String fileName) throws IOException, XmlPullParserException {
		// XML解析のためのパーサを取得
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new InputStreamReader(mContext.getAssets().open(fileName)));

		int sectionIndex = 0;

		// XMLを解析した結果をDBに書き込むためのヘルパ
		// DB更新処理に失敗した場合に備えてトランザクションを張っておく
		SQLiteDatabase db = ContentsInterface.getInstance().getWritableDatabase();
		db.beginTransaction();

		try {
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG) {
					switch (ContentsType.getValue(parser.getName())) {
						case MetaData:
							(new MetaData(mContext)).parse(parser);
							break;
						case Section:
							Section section = new Section(mContext);
							section.parse(parser);
							section.save(db);

							int index = section.getIndex();
							if (index > sectionIndex) {
								sectionIndex = index;
							}

							break;
						default:
							break;
					}
				}
			}

			// パースが正常に完了した
			ContentsInterface cif = ContentsInterface.getInstance();
			cif.markAsXmlParsed();
			cif.setMaxSectionIndex(sectionIndex);

			db.setTransactionSuccessful();
		} catch (IOException | XmlPullParserException | SQLiteException ex) {
			ex.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}

		if (mListener != null) {
			mListener.onParseFinished();
		}
	}

	/**
	 * パースが終了したイベントをハンドリングするためのリスナをセットする
	 * @param listener	パースに関するイベントをハンドルするリスナ
	 */
	public void setListener(ParserListener listener) {
		mListener = listener;
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
