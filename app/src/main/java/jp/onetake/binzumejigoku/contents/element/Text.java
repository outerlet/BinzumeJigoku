package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

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
	public enum Align {
		Left,	// 左寄せ(デフォルト)
		Right;	// 右寄せ

		public static Align getValue(String align) {
			for (Align a : values()) {
				if (align.equalsIgnoreCase(a.toString())) {
					return a;
				}
			}
			return Left;
		}
	}

	private String mText;
	private Align mAlign;
	private int mIndent;

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
		StringBuilder u16Buf = new StringBuilder();
		boolean utf16 = false;

		while (true) {
			int eventType = parser.getEventType();

			if (eventType == XmlPullParser.TEXT) {
				if (utf16) {
					u16Buf.append(parser.getText());
				} else {
					buffer.append(Utility.format(parser.getText()));
				}
			} else if (parser.getName().equalsIgnoreCase("ruby")
					&& (eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG)) {
				buffer.append(ContentsInterface.getInstance().getRubyClosure());
			} else if (parser.getName().equalsIgnoreCase("u16")) {
				if (eventType == XmlPullParser.START_TAG) {
					u16Buf = new StringBuilder();
					utf16 = true;
				} else if (eventType == XmlPullParser.END_TAG) {
					char c = (char)Integer.parseInt(u16Buf.toString(), 16);
					buffer.append("" + c);
					utf16 = false;
				}
			}

			if (!hasNext(parser)) {
				mText = buffer.toString();
				break;
			}
		}
	}

	@Override
	public void save(SQLiteDatabase db, ContentValues values) {
		String textAlign = getAttribute("text_align");
		if (!TextUtils.isEmpty(textAlign)) {
			values.put(ContentsTable.VALUE0.getColumnName(), textAlign);
		}

		String indent = getAttribute("indent");
		if (!TextUtils.isEmpty(indent)) {
			values.put(ContentsTable.VALUE1.getColumnName(), indent);
		}

		values.put(ContentsTable.CONTENTS_TEXT.getColumnName(), mText);

		super.save(db, values);
	}

	@Override
	public void load(Cursor cursor) {
		super.load(cursor);

		String align = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE0));
		mAlign = (TextUtils.isEmpty(align)) ? Align.Left : Align.getValue(align);

		String indent = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE1));
		mIndent = (TextUtils.isEmpty(indent)) ? 0 : Integer.parseInt(indent);

		mText = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.CONTENTS_TEXT));
	}

	public Align getAlign() {
		return mAlign;
	}

	public int getIndent() {
		return mIndent;
	}

	public String getText() {
		return mText;
	}

	@Override
	public ContentsType getContentsType() {
		return ContentsType.Text;
	}

	@Override
	public String toString() {
		return super.toString() + " : text = " + mText;
	}
}
