package com.walker.numberprogressbar.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.logging.Logger;

/**
 * Created by ys826 on 2017-06-24.
 */

public class NumProgressBar extends View {
    public enum ProgressTextVisibility {
        Visible, Invisible
    }

    private Boolean textVisibile;
    private int currentProgress;

    private int maxValue;

    private int textColor;

    private int progressRechedColor;

    private int progressUnreachedColor;

    private float textSize;
    private float barHeight;
    private int defaultMaxValue = 100;

    private int defaultTextColor = Color.rgb(255, 0, 0);

    private int defaultProgressReachedColor = Color.rgb(53, 9, 227);

    private int defaultProgressUnreachedColor = Color.rgb(255, 182, 193);

    private float defaultTextSize;
    private float defaultBarHeight=10;


    private Paint textPaint;

    private Paint progressReachedPaint;

    private Paint progressUnreachedPaint;

    private final static int PROGRESS_TEXT_VISIBILE = 0;
    private final static int PROGRESS_TEXT_INVISIBILE = 1;

    private RectF reachedBarRectF = new RectF(0, 0, 0, 0);
    private RectF unReachedBarRectF = new RectF(0, 0, 0, 0);

    private float textStart;
    private float textEnd;
    private boolean ifDrawText;

    private String drawedText;
    private float drawedTextWidth;

    private boolean ifDrawReachedBar;
    private boolean ifDrawUnReachedBar;



    public NumProgressBar(Context context) {

        this(context, null);
    }

    public NumProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        defaultTextSize = sp2px(10);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumProgressBar, defStyleAttr, 0);
        textSize = attributes.getDimension(R.styleable.NumProgressBar_progress_text_size, defaultTextSize);
        barHeight = attributes.getDimension(R.styleable.NumProgressBar_progress_bar_height,defaultBarHeight);
        textColor = attributes.getColor(R.styleable.NumProgressBar_progress_text_color, defaultTextColor);

        progressRechedColor = attributes.getColor(R.styleable.NumProgressBar_progress_reached_color, defaultProgressReachedColor);
        progressUnreachedColor = attributes.getColor(R.styleable.NumProgressBar_progress_unreached_color, defaultProgressUnreachedColor);

        maxValue = attributes.getInt(R.styleable.NumProgressBar_max, defaultMaxValue);
        int textVisibility = attributes.getInt(R.styleable.NumProgressBar_progress_value_visibility, PROGRESS_TEXT_VISIBILE);
        ifDrawText = textVisibility == PROGRESS_TEXT_VISIBILE;
        attributes.recycle();
        initPaint();
    }

    @Override
    protected int getSuggestedMinimumHeight() {

        return (int) Math.max(textSize,barHeight);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) textSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (ifDrawText) {
            calculateRectF();
        } else {
            calculateRectfWithoutText();
        }
        if (ifDrawReachedBar) {
            canvas.drawRect(reachedBarRectF, progressReachedPaint);
        }
        if (ifDrawUnReachedBar) {
            canvas.drawRect(unReachedBarRectF, progressUnreachedPaint);
        }
        if (ifDrawText) {
            canvas.drawText(drawedText, textStart, textEnd, textPaint);
        }

    }
    private void calculateRectfWithoutText(){
        if (currentProgress == 0) {
            ifDrawReachedBar = false;
        } else {
            ifDrawReachedBar = true;
            reachedBarRectF.left = getPaddingLeft();
            reachedBarRectF.top =getHeight()/2.0f- barHeight /2.0f;
            reachedBarRectF.right = (getWidth() - getPaddingRight() - getPaddingLeft()) / 100 * currentProgress + getPaddingRight();
            reachedBarRectF.bottom = getHeight()/2.0f+ barHeight /2.0f;
        }
        if(currentProgress == 100){
            ifDrawUnReachedBar = false;
        }else{
            ifDrawUnReachedBar = true;
            unReachedBarRectF.left = reachedBarRectF.right;
            unReachedBarRectF.top = getHeight()/2.0f- barHeight /2.0f;
            unReachedBarRectF.bottom = getHeight()/2.0f+ barHeight /2.0f;
            unReachedBarRectF.right = getWidth() - getPaddingRight();
        }


    }


    private void calculateRectF() {
        drawedText = (currentProgress * 100 / maxValue) + "%";
        drawedTextWidth = textPaint.measureText(drawedText);

        if (currentProgress == 0) {
            ifDrawReachedBar = false;
            textStart = getPaddingLeft();
        } else {
            ifDrawReachedBar = true;
            reachedBarRectF.left = getPaddingLeft();
            reachedBarRectF.top =getHeight()/2.0f- barHeight /2.0f;
            reachedBarRectF.right = (getWidth() - getPaddingRight() - getPaddingLeft()) / 100 * currentProgress + getPaddingRight();
            reachedBarRectF.bottom = getHeight()/2.0f + barHeight /2.0f;
            textStart = reachedBarRectF.right + 2.0f;


        }
        textEnd = getHeight()/2.0f - ((textPaint.descent() + textPaint.ascent()) / 2.0f);
        if ((textStart + drawedTextWidth + 2.0f) >= getWidth() - getPaddingRight()) {
            ifDrawUnReachedBar = false;
            reachedBarRectF.right = getWidth() - getPaddingRight() - 2.0f;
            textStart = getWidth() - getPaddingRight() - 2.0f - drawedTextWidth;
        } else {
            ifDrawUnReachedBar = true;
            unReachedBarRectF.top = getHeight()/2.0f- barHeight /2.0f;
            unReachedBarRectF.bottom = getHeight()/2.0f + barHeight /2.0f;
            unReachedBarRectF.left = textStart + drawedTextWidth + 2.0f;
            unReachedBarRectF.right = getWidth() - getPaddingRight();
        }


    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingBottom() + getPaddingTop();

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result = result + padding;

            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    Log.e("NumProgressBar","result:"+result+"   size:"+size);
                    result = Math.min(
                            result, size);
                }
            }
        }
        return result;
    }

    private void initPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        progressReachedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressReachedPaint.setColor(progressRechedColor);

        progressUnreachedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressUnreachedPaint.setColor(progressUnreachedColor);
    }

    public Boolean getTextVisibile() {
        return textVisibile;
    }

    public void setTextVisibile(Boolean textVisibile) {
        this.textVisibile = textVisibile;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getProgressRechedColor() {
        return progressRechedColor;
    }

    public void setProgressRechedColor(int progressRechedColor) {
        this.progressRechedColor = progressRechedColor;
    }

    public int getProgressUnreachedColor() {
        return progressUnreachedColor;
    }

    public void setProgressUnreachedColor(int progressUnreachedColor) {
        this.progressUnreachedColor = progressUnreachedColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
