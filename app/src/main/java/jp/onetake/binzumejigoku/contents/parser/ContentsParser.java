package jp.onetake.binzumejigoku.contents.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

import jp.onetake.binzumejigoku.contents.data.ContentsDbOpenHelper;
import jp.onetake.binzumejigoku.contents.common.ContentsType;

public class ContentsParser {
	private Context mContext = null;
	private ParserListener mListener = null;

	public ContentsParser(Context context) {
		mContext = context;
	}

	public void setListener(ParserListener listener) {
		mListener = listener;
	}

	public void parse(String fileName) throws IOException, XmlPullParserException {
		InputStreamReader reader = new InputStreamReader(mContext.getAssets().open(fileName));

		// XML解析のためのパーサを取得
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(reader);

		// XMLを解析した結果をDBに書き込むためのヘルパ
		// DB更新処理に失敗した場合に備えてトランザクションを張っておく
		ContentsDbOpenHelper helper = new ContentsDbOpenHelper(mContext);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();

		try {
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG) {
					switch (ContentsType.getValue(parser.getName())) {
						case MetaData:
							(new MetaDataSource(mContext, parser, db)).parse();
							break;
						case Section:
							(new SectionSource(mContext, parser, db)).parse();
							break;
						default:
							break;
					}
				}
			}

			// トランザクション正常終了
			db.setTransactionSuccessful();
		} catch (IOException | XmlPullParserException | SQLiteException ex) {
			ex.printStackTrace();
		} finally {
			db.endTransaction();
		}

		if (mListener != null) {
			mListener.onParseFinished();
		}
	}

	public interface ParserListener {
		void onParseFinished();
	}
}
