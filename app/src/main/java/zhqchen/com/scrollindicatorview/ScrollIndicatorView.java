package zhqchen.com.scrollindicatorview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 *自定义指示条，实现weibo标题的指示器滑动效果
 * Created by CHENZHIQIANG247 on 2017-12-07.
 */
public class ScrollIndicatorView extends View {

    private int indicatorColor;//指示条颜色
    private int indicatorHeight;//指示条高度, 默认2dp
    private int indicatorOffset;//指示条相对于indicateView的水平偏移量, 默认10dp
    private int itemMargin;//indicateView之间的间距, 默认20dp

    private View indicateView;//被指示的view

    private Paint mPaint;
    private Path mPath;

    private int movedMaxWidth;//指示条单向滑动的最大距离

    private float positionOffset;//ViewPager的便宜比例
    private float relateScrollWidth;//指示条滑动的偏移量

    private int startY;

    public ScrollIndicatorView(Context context) {
        this(context, null);
    }

    public ScrollIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        indicatorColor = Color.RED;
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());//默认5dp
        indicatorOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());//默认10dp
        itemMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());//默认20dp

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollIndicatorView);
        indicatorColor = typedArray.getColor(R.styleable.ScrollIndicatorView_indicator_color, indicatorColor);
        indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.ScrollIndicatorView_indicator_height, indicatorHeight);
        indicatorOffset = typedArray.getDimensionPixelSize(R.styleable.ScrollIndicatorView_indicator_offset, indicatorOffset);
        itemMargin = typedArray.getDimensionPixelSize(R.styleable.ScrollIndicatorView_item_margin, itemMargin);
        typedArray.recycle();

        mPath = new Path();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(indicatorColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(indicatorHeight);
    }

    /**
     * 自定义View， LayoutParams设置 MATCH_PARENT 或 WRAP_CONTENT，系统都会按照MATCH_PARENT的模式来测量宽高，
     * 因此，这种情况下，我们需要自己测量宽高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height;
        if(heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = indicatorHeight;
        }
        setMeasuredDimension(widthSize, height);
        if(movedMaxWidth == 0) {
            startY = getMeasuredHeight() / 2;
            movedMaxWidth = itemMargin + indicateView.getMeasuredWidth();//指示条单向滑动的距离
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        float movingX;
        if(positionOffset <= 0.5) {//indicatorOffset---> getMeasuredWidth() - indicatorOffset
            movingX = relateScrollWidth + indicateView.getMeasuredWidth() - indicatorOffset;
            mPath.moveTo(indicatorOffset, startY);
            mPath.lineTo(movingX, startY);
        } else {
            movingX = relateScrollWidth - movedMaxWidth + indicatorOffset;
            mPath.moveTo(movingX, startY);
            mPath.lineTo(getMeasuredWidth() - indicatorOffset, startY);
        }
//        Log.e("onDraw", "movingX-->" + movingX + "getMeasuredWidth() - indicatorOffset-->"  + (getMeasuredWidth() - indicatorOffset));
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 绑定要指示的view
     * @param itemView view
     */
    public void bindIndicateView(View itemView) {
        this.indicateView = itemView;
    }

    public void setupWithViewPager(final ViewPager viewPager) {
        if(viewPager == null || viewPager.getAdapter() == null) {
            throw new IllegalArgumentException("viewPager should setAdapter first");
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                rlScrollDot.scrollTo(-position * titleMoveWidth - Math.round(positionOffset * titleMoveWidth), 0);
                if(position != 0 && positionOffset == 0) {
                    positionOffset = 1;
                }
                ScrollIndicatorView.this.positionOffset = positionOffset;
                relateScrollWidth = 2 * movedMaxWidth * positionOffset;//当ViewPager滑动一半时，indicator也正向滑动一次，ViewPager滑动后半段，indicator反向滑动一次，
                postInvalidate();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
