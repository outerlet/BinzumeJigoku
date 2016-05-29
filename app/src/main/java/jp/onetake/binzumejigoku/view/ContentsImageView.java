package jp.onetake.binzumejigoku.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import jp.onetake.binzumejigoku.contents.element.Image;

public class ContentsImageView extends ImageView {
	private Image mImage;
	private EffectListener mListener;

	public ContentsImageView(Context context) {
		super(context);
	}

	public ContentsImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setImage(Image image) {
		mImage = image;

		Bitmap bitmap = mImage.getBitmap();
		if (bitmap != null) {
			setImageBitmap(mImage.getBitmap());
		}
	}

	public void setListener(EffectListener listener) {
		mListener = listener;
	}

	public void start() {
		final boolean isVisible = (getVisibility() == View.VISIBLE);

		if (mImage.getEffectType() == Image.EffectType.Fade) {
			float start = isVisible ? 1.0f : 0.0f;
			float end = isVisible ? 0.0f : 1.0f;

			if (!isVisible) {
				setVisibility(View.VISIBLE);
			}

			ObjectAnimator anim = ObjectAnimator.ofFloat(this, "alpha", start, end);
			anim.setDuration(mImage.getDuration());
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if (mListener != null) {
						if (isVisible) {
							setVisibility(View.INVISIBLE);
						}

						mListener.onEffectFinished(ContentsImageView.this);
					}
				}
			});
			anim.start();
		} else if (mImage.getEffectType() == Image.EffectType.Cut) {
			setImageAlpha(isVisible ? 0 : 255);
			setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
		}
	}

	public interface EffectListener {
		void onEffectFinished(ContentsImageView view);
	}
}
