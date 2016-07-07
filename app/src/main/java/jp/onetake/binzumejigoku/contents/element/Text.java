package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.ContentsType;
import jp.onetake.binzumejigoku.contents.db.ContentsTable;
import jp.onetake.binzumejigoku.util.Utility;

/**
 * テキストの表示を制御する要素クラス
 */
public class Text extends SectionElement {
	/**
	 * テキストの寄せ方を指定する"align"属性値に対応する列挙値
	 */
	public enum Align {
		Left,	// 左寄せ(デフォルト)
		Right;	// 右寄せ

		public static Align getValue(String align) {
			if (!TextUtils.isEmpty(align)) {
				for (Align a : values()) {
					if (align.equalsIgnoreCase(a.toString())) {
						return a;
					}
				}
			}

			return Left;
		}
	}

	private String mText;
	private Align mAlign;
	private int mIndent;
	private int mColor;

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
					buffer.append("" + (char)Integer.parseInt(u16Buf.toString(), 16));
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
		String textAlign = getAttribute("align");
		values.put(ContentsTable.VALUE0.getColumnName(), TextUtils.isEmpty(textAlign) ? "" : textAlign);

		String indent = getAttribute("indent");
		values.put(ContentsTable.VALUE1.getColumnName(), TextUtils.isEmpty(indent) ? "0" : indent);

		String textSize = getAttribute("text_size");
		values.put(ContentsTable.VALUE2.getColumnName(), TextUtils.isEmpty(textSize) ? "" : textSize);

		String colorString = getAttribute("color");
		values.put(ContentsTable.VALUE3.getColumnName(), TextUtils.isEmpty(colorString) ? "" : colorString);

		values.put(ContentsTable.CONTENTS_TEXT.getColumnName(), mText);

		super.save(db, values);
	}

	@Override
	public void load(Cursor cursor) {
		super.load(cursor);

		mAlign = Align.getValue(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE0)));
		mIndent = Integer.parseInt(cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE1)));

		String colorString = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.VALUE3));
		mColor = (TextUtils.isEmpty(colorString)) ? Color.BLACK : Color.parseColor(colorString);

		mText = cursor.getString(ContentsTable.getColumnIndex(ContentsTable.CONTENTS_TEXT));
	}

	public Align getAlign() {
		return mAlign;
	}

	public int getIndent() {
		return mIndent;
	}

	public int getColor() {
		return mColor;
	}

	public String getText() {
		return mText;
	}

	public String getPlainText() {
		ContentsInterface cif = ContentsInterface.getInstance();
		StringBuilder buffer = new StringBuilder();

		for (String text : mText.split(cif.getRubyClosure())) {
			int idx = text.indexOf(cif.getRubyDelimiter());

			if (idx != -1) {
				buffer.append(text.substring(0, idx));
			} else {
				buffer.append(text);
			}
		}

		return buffer.toString();
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
