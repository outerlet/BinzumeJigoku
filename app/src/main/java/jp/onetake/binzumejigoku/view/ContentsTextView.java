package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.element.Text;

/**
 * ルビつきのテキストを一定時間ごとにストリーム表示するView<br />
 * XMLに指定できる属性値についてはそれぞれ以下の通り
 * <dl>
 *     <dt>textSize</dt>
 *     <dd>本文のテキストサイズをdimensionで指定</dd>
 *     <dt>textColor</dt>
 *     <dd>本文のテキスト色を指定</dd>
 *     <dt>rubySize</dt>
 *     <dd>ルビのテキストサイズをdimensionで指定</dd>
 *     <dt>rubyColor</dt>
 *     <dd>ルビのテキスト色を指定</dd>
 *     <dt>lineSpace</dt>
 *     <dd>行間のサイズをdimensionで指定</dd>
 *     <dt>sentenceSpace</dt>
 *     <dd>1つの文章の間のサイズをdimensionで指定</dd>
 * </dl>
 */
public class ContentsTextView extends TimerView {
	/**
	 * 1文字の内容と描画位置(X, Y)を保持するクラス<br />
	 * StreamTextViewでしか使わないのでインナークラス
	 */
	private class Letter {
		public float x;
		public float y;
		public String letter;

		public Letter(String letter, float x, float y) {
			this.letter = letter;
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * テキストとルビそれぞれのLetterを文字数分保持し、レイアウトを決定＋保持するクラス<br />
	 * ContentsTextViewでしか使わないのでインナークラス
	 */
	private class DrawDetail {
		private List<Letter> mTextList;
		private SparseArray<Letter[]> mRubyArray;
		private Text mText;
		private float mHeight;
		private boolean mIsFinalized;

		/**
		 * コンストラクタ
		 * @param text	テキストとルビを含んだ文字列
		 */
		public DrawDetail(Text text) {
			mText = text;
			mTextList = new ArrayList<>();
			mRubyArray = new SparseArray<>();
			mIsFinalized = false;
		}

		/**
		 * テキストの文字数を取得する
		 * @return	テキストの文字数
		 */
		public int getTextCount() {
			return mTextList.size();
		}

		/**
		 * インデックス値indexにあるテキスト1文字を取得する
		 * @param index	取得したいテキストのインデックス
		 * @return	テキスト
		 */
		public Letter getTextAt(int index) {
			return mTextList.get(index);
		}

		/**
		 * インデックス値indexにあるルビのテキスト群を取得する
		 * @param index	取得したいルビのインデックス
		 * @return	ルビ
		 */
		public Letter[] getRubyAt(int index) {
			return mRubyArray.get(index);
		}

		/**
		 * 描画範囲の高さを取得する
		 * @return	描画範囲の高さ
		 */
		public float getHeight() {
			return mHeight;
		}

		/**
		 * finalizeメソッドが実行され、既に描画位置が決定されているかどうかを知る
		 * @return	finalizeメソッド実行済みかどうか。済みならtrue
		 */
		public boolean isFinalized() {
			return mIsFinalized;
		}

		/**
		 * widthに基づいて、このクラスに与えられた文字の描画位置を決定する
		 * @param width	描画範囲の幅
		 */
		public void finalize(int width) {
			Paint.FontMetrics textMetrics = mTextPaint.getFontMetrics();
			Paint.FontMetrics rubyMetrics = mRubyPaint.getFontMetrics();

			float textHeight = textMetrics.bottom - textMetrics.top;
			float rubyHeight = rubyMetrics.bottom - rubyMetrics.top;
			float lineHeight = textHeight + rubyHeight + mLineSpace;

			int lines = 1;

			float posX = (mText.getAlign() == Text.Align.Left) ? 0.0f + mText.getIndent() * mTextPaint.getTextSize() : 0.0f;
			float posY = Math.abs(rubyMetrics.top);
			float textEndX = 0.0f;
			float rubyEndX = 0.0f;

			ContentsInterface cif = ContentsInterface.getInstance();
			String[] blocks = mText.getText().split(cif.getRubyClosure());
			for (String str : blocks) {
				// ルビ込み
				int idx = str.indexOf(cif.getRubyDelimiter());
				if (idx != -1) {
					String text = str.substring(0, idx);
					String ruby = str.substring(idx + 1, str.length());

					float textWidth = text.length() * mTextPaint.getTextSize();
					float rubyWidth = ruby.length() * mRubyPaint.getTextSize();
					float length = (textWidth >= rubyWidth) ? textWidth : rubyWidth;

					// 次の文字の描画位置が描画可能範囲をはみ出す場合は次の行へ
					if (posX + length > width) {
						posX = 0.0f;
						posY += lineHeight;
						++lines;
					}

					// テキスト描画長の方がルビのそれより長い
					if (textWidth >= rubyWidth) {
						float interval = textWidth / ruby.length();
						float startX = posX + (interval - mRubyPaint.getTextSize()) / 2;

						// ルビの位置決め
						Letter[] rubys = new Letter[ruby.length()];
						for (int i = 0 ; i < ruby.length() ; i++) {
							rubys[i] = new Letter(
									ruby.substring(i, i + 1), startX + interval * i, posY);
						}
						mRubyArray.put(mTextList.size(), rubys);

						// テキストの位置決め
						for (int i = 0 ; i < text.length() ; i++) {
							Letter letter = new Letter(
									text.substring(i, i + 1),
									posX + mTextPaint.getTextSize() * i,
									posY + rubyMetrics.bottom + Math.abs(textMetrics.top));
							mTextList.add(letter);

							float end = letter.x + mTextPaint.getTextSize();
							if (end > textEndX) {
								textEndX = end;
							}
						}
					// ルビ描画長の方がテキストのそれより長い
					} else {
						// ルビの位置決め
						Letter[] rubyLetters = new Letter[ruby.length()];
						for (int i = 0 ; i < ruby.length() ; i++) {
							Letter letter = new Letter(ruby.substring(i, i + 1), posX + mRubyPaint.getTextSize() * i, posY);
							rubyLetters[i] = letter;

							float end = letter.x + mRubyPaint.getTextSize();
							if (end > rubyEndX) {
								rubyEndX = end;
							}
						}
						mRubyArray.put(mTextList.size(), rubyLetters);

						// テキストの位置決め
						float interval = rubyWidth / text.length();
						float startX = posX + ((interval - mTextPaint.getTextSize()) / 2);
						for (int i = 0 ; i < text.length() ; i++) {
							mTextList.add(new Letter(
									text.substring(i, i + 1),
									startX + interval * i,
									posY + rubyMetrics.bottom + Math.abs(textMetrics.top)));
						}
					}

					posX += length;
				// ルビなし(1文まるごとルビがない場合もこちら)
				} else {
					for (int i = 0 ; i < str.length() ; i++) {
						// 次の文字の描画位置が描画可能範囲をはみ出す場合は次の行へ
						if (posX + mTextPaint.getTextSize() > width) {
							posX = 0.0f;
							posY += lineHeight;
							++lines;
						}

						Letter letter = new Letter(
								str.substring(i, i + 1),
								posX,
								posY + rubyMetrics.bottom + Math.abs(textMetrics.top));
						mTextList.add(letter);

						float end = letter.x + mTextPaint.getTextSize();
						if (end > textEndX) {
							textEndX = end;
						}

						posX += mTextPaint.getTextSize();
					}
				}
			}

			if (mText.getAlign() == Text.Align.Right) {
				if (lines > 1) {
					throw new IllegalArgumentException("Length of 'text' is too long ('align' needs to be 'left')");
				} else {
					float maxEnd = (textEndX >= rubyEndX) ? textEndX : rubyEndX;
					float moveX = width - maxEnd - mText.getIndent() * mTextPaint.getTextSize();

					for (Letter text : mTextList) {
						text.x += moveX;
					}

					for (int i = 0 ; i < mRubyArray.size() ; i++) {
						Letter[] rubyLetters = mRubyArray.valueAt(i);
						float moveRubyX = width - rubyLetters.length * mRubyPaint.getTextSize();
						for (Letter ruby : rubyLetters) {
							ruby.x += moveRubyX;
						}
					}
				}
			}

			mIsFinalized = true;

			mHeight = lines * lineHeight;
		}
	}

	private Paint mTextPaint;
	private Paint mRubyPaint;
	private List<DrawDetail> mDetailList;
	private float mLineSpace;
	private float mSentenceSpace;
	private float mTotalHeight;

	public ContentsTextView(Context context) {
		this(context, null);
	}

	public ContentsTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTextPaint = new Paint();
		mRubyPaint = new Paint();
		mDetailList = new ArrayList<>();
		mLineSpace = 0.0f;
		mSentenceSpace = 0.0f;
		mTotalHeight = 0.0f;

		Resources res = context.getResources();

		if (attrs != null) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContentsTextView);

			mTextPaint.setTextSize(array.getDimensionPixelSize(
					R.styleable.ContentsTextView_textSize, res.getDimensionPixelSize(R.dimen.default_text_size)));
			mTextPaint.setColor(array.getColor(R.styleable.ContentsTextView_textColor, Color.BLACK));

			mRubyPaint.setTextSize(array.getDimensionPixelSize(
					R.styleable.ContentsTextView_rubySize, res.getDimensionPixelSize(R.dimen.default_ruby_size)));
			mRubyPaint.setColor(array.getColor(R.styleable.ContentsTextView_rubyColor, Color.BLACK));

			mLineSpace = (float)array.getDimensionPixelSize(R.styleable.ContentsTextView_lineSpace, 0);
			mSentenceSpace = (float)array.getDimensionPixelSize(R.styleable.ContentsTextView_sentenceSpace, 0);

			array.recycle();
		} else {
			mTextPaint.setTextSize(res.getDimensionPixelSize(R.dimen.default_text_size));
			mTextPaint.setColor(Color.BLACK);

			mRubyPaint.setTextSize(res.getDimensionPixelSize(R.dimen.default_ruby_size));
			mRubyPaint.setColor(Color.BLACK);
		}
	}

	@Override
	protected boolean executeDraw(Canvas canvas, int calledCount) {
		if (mDetailList.size() > 0) {
			DrawDetail latestDetail = mDetailList.get(mDetailList.size() - 1);

			if (!latestDetail.isFinalized()) {
				latestDetail.finalize(canvas.getWidth());

				mTotalHeight += latestDetail.getHeight();

				if (mTotalHeight + (mSentenceSpace * (mDetailList.size() - 1)) > canvas.getHeight()) {
					while (mDetailList.size() != 1) {
						mDetailList.remove(0);
					}

					mTotalHeight = latestDetail.getHeight();
				}
			}

			float baseY = 0.0f;

			for (int i = 0 ; i < mDetailList.size() ; i++) {
				DrawDetail detail = mDetailList.get(i);

				int count = (i == mDetailList.size() - 1) ? calledCount : detail.getTextCount();

				for (int x = 0 ; x < count ; x++) {
					Letter text = detail.getTextAt(x);
					canvas.drawText(text.letter, text.x, baseY + text.y, mTextPaint);

					Letter[] rubyLetters = detail.getRubyAt(x);
					if (rubyLetters != null) {
						for (Letter ruby : rubyLetters) {
							canvas.drawText(ruby.letter, ruby.x, baseY + ruby.y, mRubyPaint);
						}
					}
				}

				baseY += (detail.getHeight() + mSentenceSpace);
			}

			return (latestDetail.getTextCount() > calledCount);
		}

		return true;
	}

	public void setText(Text text) {
		mDetailList.add(new DrawDetail(text));
	}

	public void clear() {
		mDetailList.clear();
		mTotalHeight = 0.0f;

		invalidate();
	}
}
