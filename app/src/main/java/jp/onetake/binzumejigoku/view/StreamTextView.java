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
import java.util.List;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.element.Text;

/**
 * ルビつきのテキストを一定時間ごとにストリーム表示するView
 */
public class StreamTextView extends TimerView {
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
	 * StreamTextViewでしか使わないのでインナークラス
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
		 * @return
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
			float lineHeight = textHeight + rubyHeight;
			float posX = 0.0f;
			float posY = Math.abs(rubyMetrics.top);
			int lines = 1;

			String closure = ContentsInterface.getInstance().getRubyClosure();
			String[] blocks = mText.getText().split(closure);
			for (String str : blocks) {
				String delimiter = ContentsInterface.getInstance().getRubyDelimiter();

				// ルビ込み
				int idx = str.indexOf(delimiter);
				if (idx != -1) {
					String text = str.substring(0, idx);
					String ruby = str.substring(idx + 1, str.length());

					float textWidth = text.length() * mTextPaint.getTextSize();
					float rubyWidth = ruby.length() * mRubyPaint.getTextSize();
					float length = (textWidth >= rubyWidth) ? textWidth : rubyWidth;

					if (posX + length > width) {
						posX = 0.0f;
						posY += lineHeight;
						++lines;
					}

					if (textWidth >= rubyWidth) {
						float interval = textWidth / ruby.length();
						float startX = posX + (interval - mRubyPaint.getTextSize()) / 2;

						// ルビの位置決め
						Letter[] rubyLetters = new Letter[ruby.length()];
						for (int i = 0 ; i < ruby.length() ; i++) {
							rubyLetters[i] = new Letter(
									ruby.substring(i, i + 1), startX + interval * i, posY);
						}
						mRubyArray.put(mTextList.size(), rubyLetters);

						// テキストの位置決め
						for (int i = 0 ; i < text.length() ; i++) {
							mTextList.add(new Letter(
									text.substring(i, i + 1),
									posX + mTextPaint.getTextSize() * i,
									posY + rubyMetrics.bottom + Math.abs(textMetrics.top)));
						}
					} else {
						// ルビの位置決め
						Letter[] rubyLetters = new Letter[ruby.length()];
						for (int i = 0 ; i < ruby.length() ; i++) {
							rubyLetters[i] = new Letter(ruby.substring(i, i + 1), posX + mRubyPaint.getTextSize() * i, posY);
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
				// ルビなし
				} else {
					for (int i = 0 ; i < str.length() ; i++) {
						if (posX + mTextPaint.getTextSize() > width) {
							posX = 0.0f;
							posY += lineHeight;
							++lines;
						}

						mTextList.add(new Letter(
								str.substring(i, i + 1),
								posX,
								posY + rubyMetrics.bottom + Math.abs(textMetrics.top)));

						posX += mTextPaint.getTextSize();
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

	public StreamTextView(Context context) {
		this(context, null);
	}

	public StreamTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTextPaint = new Paint();
		mRubyPaint = new Paint();
		mDetailList = new ArrayList<>();

		Resources res = context.getResources();

		if (attrs != null) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StreamTextView);

			mTextPaint.setTextSize(array.getDimensionPixelSize(
					R.styleable.StreamTextView_textSize, res.getDimensionPixelSize(R.dimen.default_text_size)));
			mTextPaint.setColor(array.getColor(R.styleable.StreamTextView_textColor, Color.BLACK));

			mRubyPaint.setTextSize(array.getDimensionPixelSize(
					R.styleable.StreamTextView_rubySize, res.getDimensionPixelSize(R.dimen.default_ruby_size)));
			mRubyPaint.setColor(array.getColor(R.styleable.StreamTextView_rubyColor, Color.BLACK));

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
			DrawDetail latest = mDetailList.get(mDetailList.size() - 1);
			if (!latest.isFinalized()) {
				latest.finalize(canvas.getWidth());
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

				baseY += detail.getHeight();
			}

			return (latest.getTextCount() > calledCount);
		}

		return true;
	}

	public void setText(Text text) {
		mDetailList.add(new DrawDetail(text));
	}
}
