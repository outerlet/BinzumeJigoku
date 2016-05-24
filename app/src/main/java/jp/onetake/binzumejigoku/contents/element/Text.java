package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;
import jp.onetake.binzumejigoku.util.Utility;

/**
 * text要素を制御する要素クラス
 */
public class Text extends SectionElement {
	private String mText;

	/**
	 * コンストラクタ
	 * @param context      コンテキスト
	 * @param sectionIndex このオブジェクトが属するセクションのインデックス値
	 * @param sequence     通し番号
	 */
	public Text(Context context, int sectionIndex, int sequence) {
		super(context, sectionIndex, sequence);
	}

	@Override
	public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
		super.parse(parser);

		StringBuilder buffer = new StringBuilder();

		while (true) {
			int eventType = parser.getEventType();

			if (eventType == XmlPullParser.TEXT) {
				buffer.append(Utility.format(parser.getText()));
			} else if (parser.getName().equalsIgnoreCase("ruby")
					&& (eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG)) {
				buffer.append(ContentsInterface.getInstance().getRubyClosure());
			}

			if (!hasNext(parser)) {
				mText = buffer.toString();
				break;
			}
		}
	}

	@Override
	public void save(SQLiteDatabase db, ContentValues values) {
		values.put(ContentsTable.CONTENTS_TEXT.getColumnName(), mText);

		super.save(db, values);
	}

	@Override
	public void load(Cursor cursor) {
		mText = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.CONTENTS_TEXT));
	}

	public String getText() {
		return mText;
	}

	@Override
	protected ContentsType getContentsType() {
		return ContentsType.Text;
	}

	@Override
	public String toString() {
		return super.toString() + " : text = " + mText;
	}
}
