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
 * テキストを表示させるための"text"要素を制御するクラス
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(SQLiteDatabase db, ContentValues values) {
		String textAlign = getAttribute("align");
		values.put(ContentsTable.VALUE0.toColumnName(), TextUtils.isEmpty(textAlign) ? "" : textAlign);

		String indent = getAttribute("indent");
		values.put(ContentsTable.VALUE1.toColumnName(), TextUtils.isEmpty(indent) ? "0" : indent);

		String textSize = getAttribute("text_size");
		values.put(ContentsTable.VALUE2.toColumnName(), TextUtils.isEmpty(textSize) ? "" : textSize);

		String colorString = getAttribute("color");
		values.put(ContentsTable.VALUE3.toColumnName(), TextUtils.isEmpty(colorString) ? "" : colorString);

		values.put(ContentsTable.CONTENTS_TEXT.toColumnName(), mText);

		super.save(db, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(Cursor cursor) {
		super.load(cursor);

		mAlign = Align.getValue(cursor.getString(ContentsTable.VALUE0.toColumnIndex()));
		mIndent = Integer.parseInt(cursor.getString(ContentsTable.VALUE1.toColumnIndex()));

		String colorString = cursor.getString(ContentsTable.VALUE3.toColumnIndex());
		mColor = (TextUtils.isEmpty(colorString)) ? Color.BLACK : Color.parseColor(colorString);

		mText = cursor.getString(ContentsTable.CONTENTS_TEXT.toColumnIndex());
	}

	/**
	 * テキストを寄せる側を示すAlign列挙値を返却する
	 * @return	テキストを寄せる側
	 */
	public Align getAlign() {
		return mAlign;
	}

	/**
	 * インデントを返却する
	 * @return	インデント
	 */
	public int getIndent () {
		return mIndent;
	}

	/**
	 *  テキスト色に対応するint値を返却する
	 * @return	テキスト色に対応するint値
	 */
	public int getColor() {
		return mColor;
	}

	/**
	 * この要素が表示すべきテキストを返却する<br />
	 * ここで返されるのはパースする前、ルビの情報を含んだ(可読性の低い)文字列
	 * @return	テキスト(ルビ情報入り)
	 */
	public String getText() {
		return mText;
	}

	/**
	 * この要素が表示すべきテキストを返却する<br />
	 * ここで返されるのはルビの情報を含まない文字列
	 * @return	テキスト(ルビ情報なし)
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentsType getContentsType() {
		return ContentsType.Text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString() + " : text = " + mText;
	}
}
