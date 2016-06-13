package jp.onetake.binzumejigoku.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.element.Image;

public class ContentsImageView extends FrameLayout {
	private class ImageHolder {
		Image image;
		ImageView imageView;

		ImageHolder(ImageView imageView) {
			this.imageView = imageView;
		}

		void setImage(Image image) {
			this.image = image;

			Bitmap bitmap = this.image.getBitmap();
			if (bitmap != null) {
				imageView.setImageBitmap(this.image.getBitmap());
			}
		}
	}

	private final int DEFAULT_NUMBER_OF_LAYERS	= 1;

	private int mNumberOfLayers;
	private SparseArray<ImageHolder> mImageArray;
	private EffectListener mListener;

	public ContentsImageView(Context context) {
		this(context, null);
	}

	public ContentsImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContentsImageView);

			mNumberOfLayers = typedArray.getInt(R.styleable.ContentsImageView_numberOfLayers, DEFAULT_NUMBER_OF_LAYERS);

			typedArray.recycle();
		}

		setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

		mImageArray = new SparseArray<>();
		for (int i = 0 ; i < mNumberOfLayers ; i++) {
			ImageView imageView = (ImageView)LayoutInflater.from(
					getContext()).inflate(R.layout.view_contents_image_layer, this, false);

			this.addView(imageView);
			mImageArray.put(i, new ImageHolder(imageView));
		}
	}

	public int setImage(Image image) {
		int layer = image.getLayer();

		if (layer >= mNumberOfLayers) {
			throw new IllegalArgumentException(getContext().getString(R.string.exception_message_layer_number));
		}

		mImageArray.get(layer).setImage(image);

		return layer;
	}

	public void setListener(EffectListener listener) {
		mListener = listener;
	}

	public void start(int layer) {
		final ImageHolder holder = mImageArray.get(layer);
		final boolean isVisible = (holder.imageView.getVisibility() == View.VISIBLE);

		if (holder.image.getEffectType() == Image.EffectType.Fade) {
			if (!isVisible) {
				holder.imageView.setVisibility(View.VISIBLE);
			}

			ObjectAnimator anim = ObjectAnimator.ofFloat(
					holder.imageView, "alpha", isVisible ? 1.0f : 0.0f, isVisible ? 0.0f : 1.0f);
			anim.setDuration(holder.image.getDuration());
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if (isVisible) {
						holder.imageView.setVisibility(View.INVISIBLE);
					}

					if (mListener != null) {
						mListener.onEffectFinished(ContentsImageView.this, holder.image.getLayer());
					}
				}
			});
			anim.start();
		} else if (holder.image.getEffectType() == Image.EffectType.Cut) {
			holder.imageView.setAlpha(isVisible ? 0.0f : 1.0f);
			holder.imageView.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
		}
	}

	public void immediate(int layer) {
		ImageHolder holder = mImageArray.get(layer);

		if (holder.image.getBitmap() != null) {
			holder.imageView.setAlpha(1.0f);
			holder.imageView.setVisibility(View.VISIBLE);
		}
	}

	public interface EffectListener {
		void onEffectFinished(ContentsImageView view, int layer);
	}
}
