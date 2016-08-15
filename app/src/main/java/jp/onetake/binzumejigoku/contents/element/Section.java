package jp.onetake.binzumejigoku.contents.element;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.contents.common.ContentsType;

/**
 * セクション(=章)をまとめる"section"要素を制御するクラス<br />
 * "image"、"text"、"title"などの親要素
 */
public class Section extends Element {
	private List<SectionElement> mElementList;
	private int mIndex;

	/**
	 * コンストラクタ
	 * @param context  コンテキスト
	 */
	public Section(Context context) {
		super(context);
	}

	/**
	 * 特定のセクションを示すインデックス値を返却する
	 * @return	セクションを示すインデックス値
	 */
	public int getIndex() {
		return mIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
		super.parse(parser);

		mIndex = Integer.parseInt(getAttribute("index"));
		int sequence = 0;
		mElementList = new ArrayList<>();

		while (true) {
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				SectionElement source = null;

				switch (ContentsType.getValue(parser.getName())) {
					case Title:
						source = new Title(getContext(), mIndex, sequence++);
						break;
					case Image:
						source = new Image(getContext(), mIndex, sequence++);
						break;
					case Text:
						source = new Text(getContext(), mIndex, sequence++);
						break;
					case Wait:
						source = new Wait(getContext(), mIndex, sequence++);
						break;
					case ClearText:
						source = new ClearText(getContext(), mIndex, sequence++);
						break;
					default:
						break;
				}

				if (source != null) {
					source.parse(parser);
					mElementList.add(source);
				}
			}

			if (!hasNext(parser)) {
				break;
			}
		}
	}

	/**
	 * このセクションが抱える子要素をDBに保存する
	 * @param db	保存するためのDBオブジェクト
	 */
	public void save(SQLiteDatabase db) {
		for (SectionElement e : mElementList) {
			e.save(db, new ContentValues());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentsType getContentsType() {
		return ContentsType.Section;
	}
}
