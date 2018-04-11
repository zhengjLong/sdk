package com.library.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;

import com.library.base.R;

/**
 * 支付密码输入框
 * <PwdEditText
 * android:id="@+id/pwd_et"
 * android:layout_width="wrap_content"
 * android:layout_height="50dp"
 * android:inputType="number"
 * app:dividerLineColor="@color/colorDivider"
 * app:dotColor="@color/colorTextBold"
 * app:dotRadius="8dp"
 * app:inputBoxWidth="50dp"
 * app:outLineColor="@color/colorDivider"
 * app:outLineWidth="0.5dp"
 * android:visibility="gone"
 * app:outRadius="6dp"
 * app:pwdLength="6" />
 */
public class PwdEditText extends android.support.v7.widget.AppCompatEditText {
    // 默认外边缘线条宽度为1dp
    private final float DEFAULT_OUT_LINE_WIDTH = 1f;
    // 默认外边缘线条颜色
    private final int DEFAULT_OUT_LINE_COLOR = Color.parseColor("#bebebe");
    // 默认外边缘弧度
    private final float DEFAULT_OUT_RADIUS = 0f;
    // 默认间隔线宽度
    private final float DEFAULT_DIVIDER_LINE_WIDTH = 0.5f;
    // 默认间隔线颜色
    private final int DEFAULT_DIVIDER_LINE_COLOR = Color.parseColor("#eeeeee");
    // 默认占位圆点半径
    private final float DEFAULT_DOT_RADIUS = 1f;
    // 默认占位圆点颜色
    private final int DEFAULT_DOT_COLOR = Color.BLACK;
    // 默认输入框宽度
    private final float DEFAULT_INPUT_BOX_WIDTH = 1f;
    // 默认密码长度
    private final int DEFAULT_PWD_LENGTH = 6;

    private float mOutLineWidth = DEFAULT_OUT_LINE_WIDTH;
    private float mOutRadius = DEFAULT_OUT_RADIUS;
    private int mOutLineColor = DEFAULT_OUT_LINE_COLOR;
    private float mDividerLineWidth = DEFAULT_DIVIDER_LINE_WIDTH;
    private int mDividerLineColor = DEFAULT_DIVIDER_LINE_COLOR;
    private float mDotRadius = DEFAULT_DOT_RADIUS;
    private int mDotColor = DEFAULT_DOT_COLOR;
    private float mInputBoxWidth = DEFAULT_INPUT_BOX_WIDTH;
    private int mPwdLength = DEFAULT_PWD_LENGTH;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 矩形外边框
    private RectF mOutRect;
    // 当前圆点索引
    private int mCurrDotLength = 0;
    private OnInputListener onInputListener = null;

    public interface OnInputListener {
        void onInput(CharSequence text);

        void onInputFinish(CharSequence text);
    }

    public PwdEditText(Context context) {
        this(context, null);
    }

    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PwdEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PwdEditText, 0, 0);
            mOutLineWidth = array.getDimension(R.styleable.PwdEditText_outLineWidth, DEFAULT_OUT_LINE_WIDTH);
            mOutRadius = array.getDimension(R.styleable.PwdEditText_outRadius, DEFAULT_OUT_RADIUS);
            mOutLineColor = array.getColor(R.styleable.PwdEditText_outLineColor, DEFAULT_OUT_LINE_COLOR);
            mDividerLineWidth = array.getDimension(R.styleable.PwdEditText_dividerLineWidth, DEFAULT_DIVIDER_LINE_WIDTH);
            mDividerLineColor = array.getColor(R.styleable.PwdEditText_dividerLineColor, DEFAULT_DIVIDER_LINE_COLOR);
            mDotRadius = array.getDimension(R.styleable.PwdEditText_dotRadius, DEFAULT_DOT_RADIUS);
            mDotColor = array.getColor(R.styleable.PwdEditText_dotColor, DEFAULT_DOT_COLOR);
            mInputBoxWidth = array.getDimension(R.styleable.PwdEditText_inputBoxWidth, DEFAULT_INPUT_BOX_WIDTH);
            mPwdLength = array.getInt(R.styleable.PwdEditText_pwdLength, DEFAULT_PWD_LENGTH);
            array.recycle();
        }

        mOutRect = new RectF(0f, 0f, getWidth(), getHeight());
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mPwdLength)});
        setCursorVisible(false);
        setBackgroundDrawable(null);
    }

    public void setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = (int) (2 * mOutLineWidth + mPwdLength * mInputBoxWidth + (mPwdLength - 1) * mDividerLineWidth);
        final int height = (int) (2 * mOutLineWidth + mInputBoxWidth);
        setMeasuredDimension(width, height);
        mOutRect.set(0f, 0f, width, height);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        mCurrDotLength = text.length();
        invalidate();
        if (mPwdLength == text.length()) {
            if (null != onInputListener) {
                onInputListener.onInputFinish(text);
            }
        }
        if (null != onInputListener) {
            onInputListener.onInput(text);
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        setSelection(getText().length());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 先绘制外边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mOutLineColor);
        mPaint.setStrokeWidth(mOutLineWidth * 2);
        if (mOutRadius > 0) {
            mPaint.setStrokeWidth(mOutLineWidth);
            mOutRect.set(mOutLineWidth / 2, mOutLineWidth / 2, getWidth() - mOutLineWidth / 2, getHeight() - mOutLineWidth / 2);
        }
        canvas.drawRoundRect(mOutRect, mOutRadius, mOutRadius, mPaint);

        // 绘制间隔线
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mDividerLineColor);
        mPaint.setStrokeWidth(mDividerLineWidth * 2);
        for (int i = 0; i < mPwdLength - 1; i++) {
            final float startX = mOutLineWidth + (i + 1) * mInputBoxWidth + mDividerLineWidth * i;
            final float startY = mOutLineWidth;
            final float stopX = startX;
            final float stopY = startY + mInputBoxWidth;
            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
        }

        // 绘制占位符小圆点
        mPaint.setColor(mDotColor);
        for (int i = 0; i < mCurrDotLength; i++) {
            final float cx = mOutLineWidth + mInputBoxWidth / 2 + mInputBoxWidth * i + mDividerLineWidth * i;
            final float cy = mOutLineWidth + mInputBoxWidth / 2;
            canvas.drawCircle(cx, cy, mDotRadius, mPaint);
        }
    }
}
