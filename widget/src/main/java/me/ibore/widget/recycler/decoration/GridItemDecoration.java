package me.ibore.widget.recycler.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

public class GridItemDecoration extends BaseItemDecoration {
    private static final String TAG = "GridItemDecoration";
    protected int mSpanCount = 1;
    private Drawable mTopDivider;        //item上方分割线的drawable
    private Drawable mBottomDivider;    //item下方分割线的drawable
    private Drawable mLeftDivider;        //item左边方分割线的drawable
    private Drawable mRightDivider;        //item右方分割线的drawable
    private boolean isSticky;//固定头部
    private int mStickyHeightOrWidth;//固定头部高度或宽度
    private Drawable mStickyDrawable;//固定头部样式
    private int mStickyTextPaddingLeft = 48;//固定头部的文本左边距
    private int mStickyTextPaddingTop = 60;//固定头部的文本上边距
    private int mStickyTextColor = Color.WHITE;//固定头部的文本的字体颜色
    private int mStickyTextSize = 42;//固定头部的文本的字体大小
    private Paint mStickyTextPaint;
    private float mOffsetY;//字体中间线到基准线baseline的偏移量
    private float mTextHeight;

    public GridItemDecoration(Context context, int mSpanCount) {
        super(context, GRID, VERTICAL);
        this.mSpanCount = mSpanCount;
    }

    public GridItemDecoration(Context context, int orientation, int mSpanCount) {
        super(context, GRID, orientation);
        this.mSpanCount = mSpanCount;
    }

    public GridItemDecoration(Context context, int orientation, int mSpanCount, @DrawableRes int drawableId) {
        super(context, GRID, orientation, drawableId);
        this.mSpanCount = mSpanCount;
    }

    private GridItemDecoration(Builder builder) {
        super(builder);
        this.mSpanCount = builder.mSpanCount;
        this.isSticky = builder.isSticky;
        this.mStickyHeightOrWidth = builder.mStickyHeightOrWidth;
        this.mStickyDrawable = builder.mStickyDrawable;
        this.mStickyTextPaddingLeft = builder.mStickyTextPaddingLeft;
        this.mStickyTextPaddingTop = builder.mStickyTextPaddingTop;
        this.mStickyTextColor = builder.mStickyTextColor;
        this.mStickyTextSize = builder.mStickyTextSize;
        if (isSticky) {
            mStickyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStickyTextPaint.setColor(mStickyTextColor);//注意：颜色需为argb，否则，绘制不出
            mStickyTextPaint.setTextSize(mStickyTextSize);
            if (mOrientation == HORIZONTAL) {
                mStickyTextPaint.setTextAlign(Paint.Align.CENTER);
            }
            Paint.FontMetricsInt mFontMetricsInt = mStickyTextPaint.getFontMetricsInt();
            mTextHeight = (mFontMetricsInt.descent - mFontMetricsInt.ascent);
            float textCenter = mTextHeight / 2.0f;
            mOffsetY = -mFontMetricsInt.ascent - textCenter;
        }
    }

    public GridItemDecoration setShowOtherStyle(boolean showOtherStyle) {
        isShowOtherStyle = showOtherStyle;
        return this;
    }

    public BaseItemDecoration setSticky(boolean sticky) {
        isSticky = sticky;
        return this;
    }

    public BaseItemDecoration setStickyHeightOrWidth(@IntRange(from = 0) int stickyHeaderHeight) {
        this.mStickyHeightOrWidth = stickyHeaderHeight;
        return this;
    }

    public BaseItemDecoration setStickyDrawable(@DrawableRes int drawableId) {
        this.mStickyDrawable = ContextCompat.getDrawable(mContext.getApplicationContext(), drawableId);
        ;
        if (this.mStickyHeightOrWidth == 0) {
            this.mStickyHeightOrWidth = mStickyDrawable.getIntrinsicHeight();
        }
        return this;
    }

    public BaseItemDecoration setStickyTextPaddingLeft(int textPaddingLeft) {
        this.mStickyTextPaddingLeft = textPaddingLeft;
        return this;
    }

    public BaseItemDecoration setStickyTextPaddingTop(int textPaddingTop) {
        this.mStickyTextPaddingTop = textPaddingTop;
        return this;
    }

    /**
     * @param textColor 颜色需为argb，否则不生效
     * @return
     */
    public BaseItemDecoration setStickyTextColor(int textColor) {
        this.mStickyTextColor = textColor;
        return this;
    }

    public BaseItemDecoration setStickyTextSize(int textSize) {
        this.mStickyTextSize = textSize;
        return this;
    }

    //绘制水平分割线
    @Override
    protected void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();//可视item的个数
        int itemCount = parent.getAdapter().getItemCount();//item总个数
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        boolean headerPosHandle = true;
        boolean footerPosHandle = true;
        boolean isSubDividerHandle = true;
        boolean isRedrawDividerHandle = true;
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int topDividerHeight = mDividerHeight;
            int bottomDividerHeight = mDividerHeight;
            mTopDivider = mDivider;
            mBottomDivider = mDivider;
            if (mOrientation == VERTICAL) {
                int leftDividerWidth = mDividerWidth;
                int rightDividerWidth = mDividerWidth;
                if (noDrawLeftDivider) {//最左边分割线处理
                    if (i % mSpanCount == 0) {
                        leftDividerWidth = 0;
                    }
                }
                if (noDrawRightDivider) {//最右边分割线处理
                    if (i % mSpanCount == mSpanCount - 1) {
                        rightDividerWidth = 0;
                    }
                }
                left = child.getLeft() - params.leftMargin - leftDividerWidth;//计算分割线的左边
                right = child.getRight() + params.rightMargin + rightDividerWidth;//计算分割线的右边
                //<editor-fold desc="顶部分割线绘制与定制">
                if (noDrawHeaderDivider) {//顶部分割线处理
                    if (headerPosHandle) {
                        int adapterPosition = parent.getChildAdapterPosition(child);
                        if (mSpanCount > adapterPosition) {
                            topDividerHeight = 0;
                            if (adapterPosition == mSpanCount - 1) {
                                headerPosHandle = false;
                            }
                        } else {
                            headerPosHandle = false;
                        }
                    }
                } else {
                    if (isRedrawHeaderDivider) {//顶部分割线的定制
                        if (headerPosHandle) {
                            int adapterPosition = parent.getChildAdapterPosition(child);
                            if (mSpanCount > adapterPosition) {
                                topDividerHeight = mHeaderDividerHeight;
                                if (adapterPosition == mSpanCount - 1) {
                                    headerPosHandle = false;
                                }
                                if (mHeaderDividerDrawable != null) {
                                    mTopDivider = mHeaderDividerDrawable;
                                }
                            } else {
                                headerPosHandle = false;
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="底部分割线绘制与定制">
                if (noDrawFooterDivider) {//底部分割线处理
                    if (footerPosHandle) {
                        int rowNum = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                        int startNum = childCount - (itemCount - rowNum * mSpanCount);
                        if (startNum <= i) {
                            int adapterPosition = parent.getChildAdapterPosition(child);
                            if (rowNum * mSpanCount <= adapterPosition) {
                                bottomDividerHeight = 0;
                                if (adapterPosition == itemCount - 1) {
                                    footerPosHandle = false;
                                }
                            } else {
                                footerPosHandle = false;
                            }
                        }
                    }
                } else {
                    if (isRedrawFooterDivider) {//底部分割线的定制
                        if (footerPosHandle) {
                            int rowNum = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                            int startNum = childCount - (itemCount - rowNum * mSpanCount);
                            if (startNum <= i) {
                                int adapterPosition = parent.getChildAdapterPosition(child);
                                if (rowNum * mSpanCount <= adapterPosition) {
                                    bottomDividerHeight = mFooterDividerHeight;
                                    if (adapterPosition == itemCount - 1) {
                                        footerPosHandle = false;
                                    }
                                    if (mFooterDividerDrawable != null) {
                                        mBottomDivider = mFooterDividerDrawable;
                                    }
                                } else {
                                    footerPosHandle = false;
                                }
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="分割线批量定制">
                if (isSubDivider) {//分割线的截取
                    if (isSubDividerHandle) {
                        int adapterPosition = parent.getChildAdapterPosition(child);
                        int rowCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount : itemCount / mSpanCount + 1;
                        int rowNum = (adapterPosition + 1) % mSpanCount == 0 ? (adapterPosition + 1) / mSpanCount - 1 : (adapterPosition + 1) / mSpanCount;
                        if (rowNum > mStartIndex) {
                            int maxRow = Math.min(mEndIndex, rowCount - 1);
                            if (rowNum < maxRow) {
                                bottomDividerHeight = mSubDividerHeight;
                                topDividerHeight = mSubDividerHeight;
                                if (mSubDrawable != null) {
                                    mTopDivider = mSubDrawable;
                                    mBottomDivider = mSubDrawable;
                                }
                            } else if (rowNum == maxRow) {
                                topDividerHeight = mSubDividerHeight;
                                if (mSubDrawable != null) {
                                    mTopDivider = mSubDrawable;
                                }
                            } else {
                                isSubDividerHandle = false;
                            }
                        } else if (rowNum == mStartIndex) {
                            bottomDividerHeight = mSubDividerHeight;
                            if (mSubDrawable != null) {
                                mBottomDivider = mSubDrawable;
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="分割线定制">
                if (isRedrawDivider) {//分割线的定制
                    if (isRedrawDividerHandle) {
                        int adapterPosition = parent.getChildAdapterPosition(child);
                        int rowNum = (adapterPosition + 1) % mSpanCount == 0 ? (adapterPosition + 1) / mSpanCount : (adapterPosition + 1) / mSpanCount + 1;
                        if ((rowNum - 1) == mDividerIndex) {
                            bottomDividerHeight = mRedrawDividerHeight;
                            if (mDividerDrawable != null) {
                                mBottomDivider = mDividerDrawable;
                            }
                        } else if (mDividerIndex + 1 == rowNum - 1) {
                            topDividerHeight = mRedrawDividerHeight;
                            if (mDividerDrawable != null) {
                                mTopDivider = mDividerDrawable;
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="---item的上方的分割线绘制---">
                bottom = child.getTop() - params.topMargin;//计算分割线的下边
                top = bottom - topDividerHeight;//计算分割线的上边
                mTopDivider.setBounds(left, top, right, bottom);
                mTopDivider.draw(c);
                //</editor-fold>
                //<editor-fold desc="---item的下方的分割线绘制---">
                top = child.getBottom() + params.bottomMargin;//计算分割线的上边
                bottom = top + bottomDividerHeight;//计算分割线的下边
                mBottomDivider.setBounds(left, top, right, bottom);
                mBottomDivider.draw(c);
                //</editor-fold>
            } else {
                left = child.getLeft() - params.leftMargin;//计算分割线的左边
                right = child.getRight() + params.rightMargin;//计算分割线的右边
                if (noDrawHeaderDivider) {//顶部分割线处理
                    if (i % mSpanCount == 0) {
                        topDividerHeight = 0;
                    }
                }
                if (noDrawFooterDivider) {//底部分割线处理
                    if (i % mSpanCount == mSpanCount - 1) {
                        bottomDividerHeight = 0;
                    }
                }
                //<editor-fold desc="---item的上方的分割线绘制---">
                bottom = child.getTop() - params.topMargin;//计算分割线的下边
                top = bottom - topDividerHeight;//计算分割线的上边
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                //</editor-fold>
                //<editor-fold desc="---item的下方的分割线绘制---">
                top = child.getBottom() + params.bottomMargin;//计算分割线的上边
                bottom = top + bottomDividerHeight;//计算分割线的下边
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                //</editor-fold>
            }
        }
    }

    //绘制竖直分割线
    @Override
    protected void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();//可视item的个数
        int itemCount = parent.getAdapter().getItemCount();//item个数
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        boolean leftPosHandle = true;
        boolean rightPosHandle = true;
        boolean isSubDividerHandle = true;
        boolean isRedrawDividerHandle = true;
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int leftDividerWidth = mDividerWidth;
            int rightDividerWidth = mDividerWidth;
            mLeftDivider = mDivider;
            mRightDivider = mDivider;
            if (mOrientation == VERTICAL) {
                top = child.getTop() - params.topMargin;
                bottom = child.getBottom() + params.bottomMargin;
                if (noDrawLeftDivider) {
                    if (i % mSpanCount == 0) {
                        leftDividerWidth = 0;
                    }
                }
                if (noDrawRightDivider) {
                    if (i % mSpanCount == mSpanCount - 1) {
                        rightDividerWidth = 0;
                    }
                }
                //<editor-fold desc="---item的左边的分割线绘制---">
                right = child.getLeft() - params.leftMargin;
                left = right - leftDividerWidth;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                //</editor-fold>
                //<editor-fold desc="---item的右边的分割线绘制---">
                left = child.getRight() + params.rightMargin;
                right = left + rightDividerWidth;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                //</editor-fold>
            } else {
                int topDividerWidth = mDividerHeight;
                int bottomDividerWidth = mDividerHeight;
                if (noDrawHeaderDivider) {//头部分割线处理
                    if (i % mSpanCount == 0) {
                        topDividerWidth = 0;
                    }
                }
                if (noDrawFooterDivider) {//底部分割线处理
                    if (i % mSpanCount == mSpanCount - 1) {
                        bottomDividerWidth = 0;
                    }
                }
                top = child.getTop() - params.topMargin - topDividerWidth;
                bottom = child.getBottom() + params.bottomMargin + bottomDividerWidth;
                //<editor-fold desc="最左边分割线绘制与定制">
                if (noDrawLeftDivider) {//左边分割线处理
                    if (leftPosHandle) {
                        int adapterPosition = parent.getChildAdapterPosition(child);
                        if (mSpanCount > adapterPosition) {
                            leftDividerWidth = 0;
                            if (adapterPosition == mSpanCount - 1) {
                                leftPosHandle = false;
                            }
                        } else {
                            leftPosHandle = false;
                        }
                    }
                } else {
                    if (isRedrawLeftDivider) {//左边分割线定制
                        if (leftPosHandle) {
                            int adapterPosition = parent.getChildAdapterPosition(child);
                            if (mSpanCount > adapterPosition) {
                                leftDividerWidth = mLeftDividerWidth;
                                if (adapterPosition == mSpanCount - 1) {
                                    leftPosHandle = false;
                                }
                                if (mLeftDividerDrawable != null) {
                                    mLeftDivider = mLeftDividerDrawable;
                                }
                            } else {
                                leftPosHandle = false;
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="最右边分割线绘制与定制">
                if (noDrawRightDivider) {//右边分割线处理
                    if (rightPosHandle) {
                        int columnNum = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                        int startNum = childCount - (itemCount - columnNum * mSpanCount);
                        if (startNum <= i) {
                            int adapterPosition = parent.getChildAdapterPosition(child);
                            if (columnNum * mSpanCount <= adapterPosition) {
                                rightDividerWidth = 0;
                                if (adapterPosition == itemCount - 1) {
                                    rightPosHandle = false;
                                }
                            } else {
                                rightPosHandle = false;
                            }
                        }
                    }
                } else {
                    if (isRedrawRightDivider) {//右边分割线定制
                        if (rightPosHandle) {
                            int columnNum = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                            int startNum = childCount - (itemCount - columnNum * mSpanCount);
                            if (startNum <= i) {
                                int adapterPosition = parent.getChildAdapterPosition(child);
                                if (columnNum * mSpanCount <= adapterPosition) {
                                    rightDividerWidth = mRightDividerWidth;
                                    if (adapterPosition == itemCount - 1) {
                                        rightPosHandle = false;
                                    }
                                    if (mRightDividerDrawable != null) {
                                        mRightDivider = mRightDividerDrawable;
                                    }
                                } else {
                                    rightPosHandle = false;
                                }
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="分割线批量定制">
                if (isSubDivider) {//分割线的截取
                    if (isSubDividerHandle) {
                        int adapterPosition = parent.getChildAdapterPosition(child);
                        int rowCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount : itemCount / mSpanCount + 1;
                        int rowNum = (adapterPosition + 1) % mSpanCount == 0 ? (adapterPosition + 1) / mSpanCount - 1 : (adapterPosition + 1) / mSpanCount;
                        if (rowNum > mStartIndex) {
                            int maxRow = Math.min(mEndIndex, rowCount - 1);
                            if (rowNum < maxRow) {
                                leftDividerWidth = mSubDividerWidth;
                                rightDividerWidth = mSubDividerWidth;
                                if (mSubDrawable != null) {
                                    mLeftDivider = mSubDrawable;
                                    mRightDivider = mSubDrawable;
                                }
                            } else if (rowNum == maxRow) {
                                leftDividerWidth = mSubDividerWidth;
                                if (mSubDrawable != null) {
                                    mLeftDivider = mSubDrawable;
                                }
                            } else {
                                isSubDividerHandle = false;
                            }
                        } else if (rowNum == mStartIndex) {
                            rightDividerWidth = mSubDividerWidth;
                            if (mSubDrawable != null) {
                                mRightDivider = mSubDrawable;
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="分割线定制">
                if (isRedrawDivider) {//分割线的定制
                    if (isRedrawDividerHandle) {
                        int adapterPosition = parent.getChildAdapterPosition(child);
                        int rowNum = (adapterPosition + 1) % mSpanCount == 0 ? (adapterPosition + 1) / mSpanCount : (adapterPosition + 1) / mSpanCount + 1;
                        if ((rowNum - 1) == mDividerIndex) {
                            rightDividerWidth = mRedrawDividerWidth;
                            if (mDividerDrawable != null) {
                                mRightDivider = mDividerDrawable;
                            }
                        } else if (mDividerIndex + 1 == rowNum - 1) {
                            leftDividerWidth = mRedrawDividerWidth;
                            if (mDividerDrawable != null) {
                                mLeftDivider = mDividerDrawable;
                            }
                        }
                    }
                }
                //</editor-fold>
                //<editor-fold desc="---item的左边的分割线绘制---">
                right = child.getLeft() - params.leftMargin;
                left = right - leftDividerWidth;
                mLeftDivider.setBounds(left, top, right, bottom);
                mLeftDivider.draw(c);
                //</editor-fold>
                //<editor-fold desc="---item的右边的分割线绘制---">
                left = child.getRight() + params.rightMargin;
                right = left + rightDividerWidth;
                mRightDivider.setBounds(left, top, right, bottom);
                mRightDivider.draw(c);
                //</editor-fold>
            }
        }
    }

    /**
     * 要想清楚outRect作用，请看{@link android.support.v7.widget.GridLayoutManager}源码，如：measureChild().
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = parent.getChildAdapterPosition(view);
        int left = mDividerWidth;
        int top = mDividerHeight;
        int right = mDividerWidth;
        int bottom = mDividerHeight;
        if (mOrientation == VERTICAL) {
            //<editor-folder desc="悬浮头部">
            if (isSticky) {//悬浮头部
                RecyclerView.Adapter adapter = parent.getAdapter();
                if (adapter instanceof OnHeaderListener) {
                    OnHeaderListener listener = ((OnHeaderListener) adapter);
                    String headerName = listener.getHeaderName(itemPosition);
                    if (!TextUtils.isEmpty(headerName)) {
                        if (itemPosition == 0 || !headerName.equals(listener.getHeaderName(itemPosition - 1))) {
                            top = mStickyHeightOrWidth;
                            outRect.set(0, top, 0, bottom);
                            return;
                        }
                    }
                }
            }
            //</editor-folder>
            //<editor-fold desc="顶部分割线绘制与定制">
            if (noDrawHeaderDivider) {
                if (mSpanCount > itemPosition) {
                    top = 0;
                }
            } else {
                if (isRedrawHeaderDivider) {
                    if (mSpanCount > itemPosition) {
                        top = mHeaderDividerHeight;
                    }
                }
            }
            //</editor-fold>
            //<editor-fold desc="底部分割线绘制与定制">
            if (noDrawFooterDivider) {
                int itemCount = parent.getAdapter().getItemCount();
                int rowNum = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                if (rowNum * mSpanCount <= itemPosition) {
                    bottom = 0;
                }
            } else {
                if (isRedrawFooterDivider) {
                    int itemCount = parent.getAdapter().getItemCount();
                    int rowNum = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                    if (rowNum * mSpanCount <= itemPosition) {
                        bottom = mFooterDividerHeight;
                    }
                }
            }
            //</editor-fold>
            if (noDrawLeftDivider) {
                if (itemPosition % mSpanCount == 0) {
                    left = 0;
                }
            }
            if (noDrawRightDivider) {
                if (itemPosition % mSpanCount == mSpanCount - 1) {
                    right = 0;
                }
            }
            //<editor-fold desc="分割线批量定制">
            if (isSubDivider) {
                int itemCount = parent.getAdapter().getItemCount();
                int rowCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount : itemCount / mSpanCount + 1;
                if (mStartIndex >= rowCount - 1) {
                    isSubDivider = false;
                } else {
                    int rowNum = (itemPosition + 1) % mSpanCount == 0 ? (itemPosition + 1) / mSpanCount - 1 : (itemPosition + 1) / mSpanCount;//计算第几行
                    if (rowNum > mStartIndex) {
                        int maxRow = Math.min(mEndIndex, rowCount - 1);
                        if (rowNum < maxRow) {
                            top = mSubDividerHeight;
                            bottom = mSubDividerHeight;
                        } else if (rowNum == maxRow) {
                            top = mSubDividerHeight;
                        }
                    } else if (rowNum == mStartIndex) {
                        bottom = mSubDividerHeight;
                    }
                }
            }
            //</editor-fold>
            //<editor-fold desc="分割线定制">
            if (isRedrawDivider) {
                int itemCount = parent.getAdapter().getItemCount();
                int rowCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount : itemCount / mSpanCount + 1;//计算总行数
                if (mDividerIndex >= rowCount - 1) {
                    isRedrawDivider = false;
                }
                int rowNum = (itemPosition + 1) % mSpanCount == 0 ? (itemPosition + 1) / mSpanCount : (itemPosition + 1) / mSpanCount + 1;//计算第几行
                if ((rowNum - 1) == mDividerIndex) {
                    bottom = mRedrawDividerHeight;
                } else if (mDividerIndex + 1 == rowNum - 1) {
                    top = mRedrawDividerHeight;
                }
            }
            //</editor-fold>
        } else {
            if (noDrawHeaderDivider) {
                if (itemPosition % mSpanCount == 0) {
                    top = 0;
                }
            }
            if (noDrawFooterDivider) {
                if (itemPosition % mSpanCount == mSpanCount - 1) {
                    bottom = 0;
                }
            }
            //<editor-fold desc="最左边分割线绘制与定制">
            if (noDrawLeftDivider) {
                if (mSpanCount > itemPosition) {
                    left = 0;
                }
            } else {
                if (isRedrawLeftDivider) {
                    if (mSpanCount > itemPosition) {
                        left = mLeftDividerWidth;
                    }
                }
            }
            //</editor-fold>
            //<editor-fold desc="最右边分割线绘制与定制">
            if (noDrawRightDivider) {
                int itemCount = parent.getAdapter().getItemCount();
                int columnCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                if (columnCount * mSpanCount <= itemPosition) {
                    right = 0;
                }
            } else {
                if (isRedrawRightDivider) {
                    int itemCount = parent.getAdapter().getItemCount();
                    int columnCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount - 1 : itemCount / mSpanCount;
                    if (columnCount * mSpanCount <= itemPosition) {
                        right = mRightDividerWidth;
                    }
                }
            }
            //</editor-fold>
            //<editor-fold desc="分割线批量定制">
            if (isSubDivider) {
                int itemCount = parent.getAdapter().getItemCount();
                int rowCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount : itemCount / mSpanCount + 1;
                if (mStartIndex >= rowCount - 1) {
                    isSubDivider = false;
                } else {
                    int rowNum = (itemPosition + 1) % mSpanCount == 0 ? (itemPosition + 1) / mSpanCount - 1 : (itemPosition + 1) / mSpanCount;//计算第几行
                    if (rowNum > mStartIndex) {
                        int maxRow = Math.min(mEndIndex, rowCount - 1);
                        if (rowNum < maxRow) {
                            left = mSubDividerWidth;
                            right = mSubDividerWidth;
                        } else if (rowNum == maxRow) {
                            left = mSubDividerWidth;
                        }
                    } else if (rowNum == mStartIndex) {
                        right = mSubDividerWidth;
                    }
                }
            }
            //</editor-fold>
            //<editor-fold desc="分割线定制">
            if (isRedrawDivider) {
                int itemCount = parent.getAdapter().getItemCount();
                int rowCount = itemCount % mSpanCount == 0 ? itemCount / mSpanCount : itemCount / mSpanCount + 1;//计算总行数
                if (mDividerIndex >= rowCount - 1) {
                    isRedrawDivider = false;
                }
                int rowNum = (itemPosition + 1) % mSpanCount == 0 ? (itemPosition + 1) / mSpanCount : (itemPosition + 1) / mSpanCount + 1;//计算第几行
                if ((rowNum - 1) == mDividerIndex) {
                    right = mRedrawDividerWidth;
                } else if (mDividerIndex + 1 == rowNum - 1) {
                    left = mRedrawDividerWidth;
                }
            }
            //</editor-fold>
        }

        /*
         * left：代表item的左边分割线占有的x轴长度
         * top：代表item的顶部分割线占有的y轴长度
         * right：代表item的右边分割线占有的x轴长度
         * bottom：代表item的底部分割线占有的y轴长度
         * */
        if (isShowOtherStyle) {
            outRect.set(0, top, right, 0);
        } else {
            outRect.set(left, top, right, bottom);
        }
    }

    public static class Builder extends BaseBuilder {
        private int mSpanCount = 1;
        private boolean isSticky;//固定头部
        private int mStickyHeightOrWidth;//固定头部高度或宽度
        private Drawable mStickyDrawable;//固定头部样式
        private int mStickyTextPaddingLeft = 48;//固定头部的文本左边距
        private int mStickyTextPaddingTop = 60;//固定头部的文本上边距
        private int mStickyTextColor = Color.WHITE;//固定头部的文本的字体颜色
        private int mStickyTextSize = 42;//固定头部的文本的字体大小

        public Builder(Context context, int mSpanCount) {
            super(context, GRID);
            this.mSpanCount = mSpanCount;
            if (this.mSpanCount == 1) {
                noDrawLeftDivider = true;
                noDrawRightDivider = true;
            }
        }

        public Builder(Context context, int mOrientation, int mSpanCount) {
            super(context, GRID, mOrientation);
            this.mSpanCount = mSpanCount;
            if (this.mSpanCount == 1) {
                noDrawLeftDivider = true;
                noDrawRightDivider = true;
            }
        }

        public BaseBuilder setShowOtherStyle(boolean showOtherStyle) {
            this.isShowOtherStyle = showOtherStyle;
            return this;
        }

        public Builder setSticky(boolean sticky) {
            isSticky = sticky;
            return this;
        }

        public Builder setStickyHeightOrWidth(@IntRange(from = 0) int stickyHeaderHeight) {
            this.mStickyHeightOrWidth = stickyHeaderHeight;
            return this;
        }

        public Builder setStickyDrawable(@DrawableRes int drawableId) {
            this.mStickyDrawable = ContextCompat.getDrawable(mContext.getApplicationContext(), drawableId);
            ;
            if (this.mStickyHeightOrWidth == 0) {
                this.mStickyHeightOrWidth = mStickyDrawable.getIntrinsicHeight();
            }
            return this;
        }

        public Builder setStickyTextPaddingLeft(int textPaddingLeft) {
            this.mStickyTextPaddingLeft = textPaddingLeft;
            return this;
        }

        public Builder setStickyTextPaddingTop(int textPaddingTop) {
            this.mStickyTextPaddingTop = textPaddingTop;
            return this;
        }

        /**
         * @param textColor 颜色需为argb，否则不生效
         * @return
         */
        public Builder setStickyTextColor(int textColor) {
            this.mStickyTextColor = textColor;
            return this;
        }

        public Builder setStickyTextSize(int textSize) {
            this.mStickyTextSize = textSize;
            return this;
        }

        public GridItemDecoration build() {
            return new GridItemDecoration(this);
        }
    }

}
