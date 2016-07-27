package jp.onetake.binzumejigoku.contents.xml;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsDbOpenHelper;
import jp.onetake.binzumejigoku.contents.element.MetaData;
import jp.onetake.binzumejigoku.contents.element.Section;

/**
 * コンテンツ(=ストーリー)が定義されているXMLを解析しDBに登録するパーサクラス<br />
 * コンテンツの大きさによっては解析に時間がかかるかもしれないのでAsyncTaskで実装
 */
public class ContentsXmlParserTask extends AsyncTask<String, Void, Object> {
	/**
	 * このオブジェクトがXMLをパースしたときに起こりうるイベントを捕捉するためのリスナインターフェイス
	 */
	public interface XmlParseListener {
		/**
		 * パースが終了したイベントを捕捉する<br />
		 * DBのバージョンがアプリ更新前と同じであれば何も行われず、executedにfalseが与えられる
		 * @param executed	パース処理が行われたならtrue
		 */
		void onParseFinished(boolean executed);

		/**
		 * パース処理において例外が発生したイベントを捕捉する
		 * @param e	例外オブジェクト
		 */
		void onExceptionOccurred(Exception e);
	}

	// 処理が終了した際にonPostExecuteに与えられるオブジェクト
	private class ResultHolder {
		boolean isSucceeded;
		Exception exception;

		ResultHolder(boolean isSucceeded, Exception exception) {
			this.isSucceeded = isSucceeded;
			this.exception = exception;
		}
	}

	private Context mContext;
	private XmlParseListener mListener;

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 */
	public ContentsXmlParserTask(Context context) {
		mContext = context;
	}

	/**
	 * パースした結果を受け取るリスナをセットする
	 * @param	listener	リスナオブジェクト
	 */
	public void setListener(XmlParseListener listener) {
		mListener = listener;
	}

	/**
	 * <p>
	 * assetsディレクトリにあるXMLファイルを解析してDBに登録する<br />
	 * 処理するXMLファイルのファイル名はsetFileNameでセットしておくこと
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	protected Object doInBackground(String... fileNames) {
		ContentsInterface cif = ContentsInterface.getInstance();
		ContentsDbOpenHelper helper = cif.getDatabaseHelper();

		// データベースにデータが入っていれば何もしない
		// (以下、Android4.4＋Nexus5で確認した不具合への対処)
		// アプリを初めて起動したあとバックキー2回押しで終了させて、プロセスが死なないうちにフォアグラウンドに回すと
		// DbOpenHelperのonCreateが実行されるのにテーブルにはデータが入ったままというおかしな状況になる
		// その状況で後続のパース処理＆DBへのINSERTを行うとSQLiteConstraintException(恐らくPrimary Keyの問題)が
		// 発生するので、データが入ってれば後続のパース処理は行わないようにする
		if (helper.isContentsExists()) {
			return new ResultHolder(false, null);
		}

		// XMLを解析した結果を保存するためのデータベースオブジェクト
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			// XML解析のためのパーサを取得
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new InputStreamReader(mContext.getAssets().open(fileNames[0])));

			int sectionIndex = 0;

			// DB更新処理に失敗した場合に備えてトランザクションを張っておく
			db.beginTransaction();

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
			cif.setMaxSectionIndex(sectionIndex);

			db.setTransactionSuccessful();
		} catch (IOException | XmlPullParserException | SQLiteException e) {
			return new ResultHolder(false, e);
		} finally {
			db.endTransaction();
			db.close();
		}

		return new ResultHolder(true, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostExecute(Object obj) {
		if (mListener != null) {
			ResultHolder result = (ResultHolder)obj;

			if (result.exception == null) {
				mListener.onParseFinished(result.isSucceeded);
			} else {
				mListener.onExceptionOccurred(result.exception);
			}
		}
	}
}
