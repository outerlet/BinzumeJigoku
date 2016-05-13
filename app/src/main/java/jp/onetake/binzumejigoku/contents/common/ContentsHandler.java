package jp.onetake.binzumejigoku.contents.common;

public class ContentsHandler {
	private static ContentsHandler mInstance;

	private String mRubyDelimiter;
	private String mRubyClosure;

	public static ContentsHandler getInstance() {
		if (mInstance == null) {
			mInstance = new ContentsHandler();
		}
		return mInstance;
	}

	public void setRubyDelimiter(String delimiter) {
		mRubyDelimiter = delimiter;
	}

	public void setRubyClosure(String closure) {
		mRubyClosure = closure;
	}

	public String getRubyDelimiter() {
		return mRubyDelimiter;
	}

	public String getRubyClosure() {
		return mRubyClosure;
	}
}
