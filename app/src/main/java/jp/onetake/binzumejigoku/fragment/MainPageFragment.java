package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.onetake.binzumejigoku.R;

public class MainPageFragment extends Fragment {
	public interface PageEventListener {
		void onPageSelected(int pageIndex);
	}

	private static final String KEY_PAGE_INDEX			= "MainPageFragment.KEY_PAGE_INDEX";
	private static final String KEY_TITLE				= "MainPageFragment.KEY_TITLE";
	private static final String KEY_SUMMARY				= "MainPageFragment.KEY_SUMMARY";
	private static final String KEY_BACKGROUND_RESID	= "MainPageFragment.KEY_BACKGROUND_RESID";
	private static final int UNKNOWN_DRAWABLE_RESID		= -1;

	public static MainPageFragment newInstance(int pageIndex, String title, String summary) {
		return newInstance(pageIndex, title, summary, UNKNOWN_DRAWABLE_RESID);
	}

	public static MainPageFragment newInstance(int pageIndex, String title, String summary, int bgResId) {
		Bundle params = new Bundle();
		params.putInt(KEY_PAGE_INDEX, pageIndex);
		params.putString(KEY_TITLE, title);
		params.putString(KEY_SUMMARY, summary);
		params.putInt(KEY_BACKGROUND_RESID, bgResId);

		MainPageFragment fragment = new MainPageFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_page, null);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getActivity() instanceof PageEventListener) {
					((PageEventListener)getActivity()).onPageSelected(getPageIndex());
				}
			}
		});

		TextView textView = (TextView)view.findViewById(R.id.textview_summary);
		textView.setText(getArguments().getString(KEY_SUMMARY));

		ImageView imageView = (ImageView)view.findViewById(R.id.imageview_background);
		int resId = getArguments().getInt(KEY_BACKGROUND_RESID);
		if (resId != UNKNOWN_DRAWABLE_RESID) {
			imageView.setBackgroundResource(resId);
		} else {
			imageView.setVisibility(View.INVISIBLE);
		}

		return view;
	}

	public int getPageIndex() {
		return getArguments().getInt(KEY_PAGE_INDEX);
	}

	public String getTitle() {
		return getArguments().getString(KEY_TITLE);
	}
}
