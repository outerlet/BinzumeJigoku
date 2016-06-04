package jp.onetake.binzumejigoku.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
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
		public Image image;
		public ImageView imageView;

		public ImageHolder(ImageView imageView) {
			this.imageView = imageView;
		}

		public void setImage(Image image) {
			this.image = image;

			Bitmap bitmap = this.image.getBitmap();
			if (bitmap != null) {
				imageView.setImageBitmap(this.image.getBitmap());
			}
		}
	}

	private final int NUMBER_OF_LAYERS	= 3;

	private SparseArray<ImageHolder> mImageArray;
	private EffectListener mListener;

	public ContentsImageView(Context context) {
		this(context, null);
	}

	public ContentsImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View view = LayoutInflater.from(getContext()).inflate(R.layout.view_contents_image, this);

		mImageArray = new SparseArray<>();
		for (int i = 0 ; i < NUMBER_OF_LAYERS ; i++) {
			int viewId = getContext().getResources().getIdentifier(
					String.format("imageview_layer%1$d", i), "id", getContext().getPackageName());
			mImageArray.put(i, new ImageHolder((ImageView)view.findViewById(viewId)));
		}
	}

	public int setImage(Image image) {
		int layer = image.getLayer();

		if (layer >= NUMBER_OF_LAYERS) {
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
			holder.imageView.setImageAlpha(isVisible ? 0 : 255);
			holder.imageView.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
		}
	}

	public interface EffectListener {
		void onEffectFinished(ContentsImageView view, int layer);
	}
}
