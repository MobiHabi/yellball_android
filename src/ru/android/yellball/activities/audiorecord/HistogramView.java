package ru.android.yellball.activities.audiorecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import ru.android.yellball.utils.ContextParamsUtil;

/**
 * View, that works as a canvas to display a sound histogram.
 * <p/>
 * Created by user on 22.12.2014.
 */
public class HistogramView extends View {
    private byte[] buffer;
    private int bytesCount;
    private Paint paintSettings;

    public HistogramView(Context context) {
        super(context);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (buffer != null) {
            final int segmentsCount = bytesCount / 2 - 1;
            final double width = getWidth();
            final double height = getHeight();
            final double segmentWidth = width / segmentsCount;

            // Creating a line path to draw as a histogram
            Path hist = new Path();

            double xOffset = 0;
            for (int i = 0; i <= segmentsCount; ++i, xOffset += segmentWidth) {
                short value = (short) (buffer[2 * i] | buffer[2 * i + 1] << 8);
                double valueHeight = height * (0.5 - value / 65536.0);
                if (i == 0) {
                    hist.moveTo((float) xOffset, (float) valueHeight);
                } else {
                    hist.lineTo((float) xOffset, (float) valueHeight);
                }
            }

            Paint paintSettings = getPaintSettings();
            canvas.drawPath(hist, paintSettings);
        }
    }

    /**
     * Sets the current data buffer to visualize.
     *
     * @param buffer
     * @param bytesCount
     */
    public void setBuffer(byte[] buffer, int bytesCount) {
        this.buffer = buffer;
        this.bytesCount = bytesCount;
        invalidate();
    }

    /**
     * Creates and returns an object of {@link android.graphics.Paint}.
     *
     * @return
     */
    private Paint getPaintSettings() {
        if (paintSettings != null) {
            return paintSettings;
        }

        paintSettings = new Paint();
        paintSettings.setAntiAlias(true);
        paintSettings.setStyle(Paint.Style.STROKE);
        paintSettings.setStrokeWidth(1);
        paintSettings.setColor(ContextParamsUtil.getSoundHistogramColor(getContext()));
        return paintSettings;
    }
}
