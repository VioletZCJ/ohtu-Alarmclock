package ohtu.beddit.views.timepicker;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import ohtu.beddit.alarm.AlarmTimePicker;
import ohtu.beddit.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: psaikko
 * Date: 21.5.2012
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class CustomTimePicker extends View implements AlarmTimePicker {
    int minSize;

    // sizes as a fraction of the clock's radius
    private final static float GRAB_POINT_OFFSET = 0.2f;
    private final static float GRAB_POINT_SIZE = 0.1f;
    private final static float HAND_WIDTH = 0.02f;
    private final static float HOUR_HAND_LENGTH = 0.55f;
    private final static float CLOCK_NUMBER_SIZE = 0.2f;

    private final static double MINUTE_INCREMENT = Math.PI / 30.0;
    private final static double HOUR_INCREMENT = Math.PI / 6.0;

    private final static int MAX_INTERVAL = 45;

    Slider intervalSlider;
    AnalogClock analogClock;
    TimeDisplay timeDisplay;
    MinuteHand minuteHand;
    HourHand hourHand;

    List<Movable> movables = new LinkedList<Movable>();

    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.minSize = Integer.parseInt(attrs.getAttributeValue("http://schemas.android.com/apk/res/ohtu.beddit", "minSize"));
        updateSize();
    }

    protected void onDraw(Canvas c) {
        analogClock.draw(c);
        minuteHand.draw(c);
        hourHand.draw(c);
        timeDisplay.draw(c);
        intervalSlider.draw(c);
    }

    final View v = findViewById(R.id.alarmTimePicker);

    public boolean onTouchEvent(MotionEvent me) {
        boolean eventHandled = false;
        float x = me.getX(), y = me.getY();
        switch (me.getAction()) {
            case (MotionEvent.ACTION_UP):
                for (Movable mv : movables) {
                    if (mv.wasClicked())
                        mv.animate(mv.createTargetFromClick(x, y));
                    mv.releaseClick();
                    mv.releaseGrab();
                }
                eventHandled = true;
                break;
            case (MotionEvent.ACTION_DOWN):
                for (Movable mv : movables)
                    if (eventHandled = mv.grab(x, y)) break;
                for (Movable mv : movables)
                    if (eventHandled = mv.click(x, y)) break;
                break;
            case (MotionEvent.ACTION_MOVE):
                for (Movable mv : movables)
                    if (mv.isGrabbed())
                        mv.updatePositionFromClick(x, y);
                eventHandled = true;
                break;
        }
        invalidate();
        return eventHandled;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateSize();
    }

    private void updateSize() {
        int minDimension = Math.min(getWidth(), getHeight());
        float barHeight = minDimension / 8;
        float radius = getHeight() >= 1.25f * getWidth() ?
                getWidth() / 2f : Math.min(getWidth(), getHeight()) / 2f - barHeight;

        float midX = getWidth() / 2f;
        float midY = getHeight() / 2f;

        float grabPointSize = radius * GRAB_POINT_SIZE;
        float grabPointOffset = radius * GRAB_POINT_OFFSET;

        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(radius * HAND_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);

        Paint timePaint = new Paint();
        timePaint.setAntiAlias(true);
        timePaint.setColor(Color.BLACK);
        timePaint.setTextSize(barHeight);

        intervalSlider = new Slider(midX - radius * 0.9f, midY + radius, radius * 1.8f, barHeight, MAX_INTERVAL, 0, linePaint, grabPointSize, this);
        hourHand = new HourHand(midX, midY, 0, HOUR_INCREMENT, radius * HOUR_HAND_LENGTH, linePaint, grabPointOffset, grabPointSize, this);
        minuteHand = new MinuteHand(midX, midY, 0, MINUTE_INCREMENT, radius, linePaint, grabPointOffset, grabPointSize, this, hourHand);
        analogClock = new AnalogClock(midX, midY, radius, radius * CLOCK_NUMBER_SIZE, minuteHand, hourHand);
        timeDisplay = new TimeDisplay(midX, midY - radius, 0, 0, timePaint);
        intervalSlider.addListener(analogClock);
        hourHand.addListener(timeDisplay);
        minuteHand.addListener(hourHand);
        minuteHand.addListener(timeDisplay);

        movables.clear();
        // order is important, we want to handle hour hand before minute hand
        movables.add(hourHand);
        movables.add(minuteHand);
        movables.add(intervalSlider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = minSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = minSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public int getHours() { return hourHand.getValue(); }

    @Override
    public int getMinutes() { return minuteHand.getValue(); }

    @Override
    public int getInterval() { return intervalSlider.getValue(); }

    public void setHours(int hours) { hourHand.setValue(hours); }

    public void setMinutes(int minutes) { minuteHand.setValue(minutes); }

    public void setInterval(int interval) { intervalSlider.setValue(interval); }
}
