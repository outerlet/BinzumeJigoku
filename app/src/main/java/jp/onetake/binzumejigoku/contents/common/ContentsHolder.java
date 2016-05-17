package jp.onetake.binzumejigoku.contents.common;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.contents.element.SectionElement;

public class ContentsHolder {
	private List<SectionElement> mElementList;
	private int mIndex;

	public ContentsHolder() {
		mElementList = new ArrayList<>();
		mIndex = -1;
	}

	public void add(SectionElement elm) {
		mElementList.add(elm);
	}

	public boolean hasNext() {
		return (mIndex + 1 < mElementList.size());
	}

	public SectionElement next() {
		return mElementList.get(++mIndex);
	}
}
