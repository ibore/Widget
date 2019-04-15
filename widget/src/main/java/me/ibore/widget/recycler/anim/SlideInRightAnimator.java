package me.ibore.widget.recycler.anim;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.Interpolator;

public class SlideInRightAnimator extends BaseItemAnimator {

  public SlideInRightAnimator() {

  }

  public SlideInRightAnimator(Interpolator interpolator) {
    mInterpolator = interpolator;
  }

  @Override
  protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .translationX(holder.itemView.getRootView().getWidth())
        .setDuration(getRemoveDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultRemoveVpaListener(holder))
        .setStartDelay(getRemoveDelay(holder))
        .start();
  }

  @Override
  protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
    ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth());
  }

  @Override
  protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .translationX(0)
        .setDuration(getAddDuration())
        .setInterpolator(mInterpolator)
        .setListener(new DefaultAddVpaListener(holder))
        .setStartDelay(getAddDelay(holder))
        .start();
  }
}
