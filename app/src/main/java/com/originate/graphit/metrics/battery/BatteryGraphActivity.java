package com.originate.graphit.metrics.battery;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.util.PaintUtils;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import com.originate.graphit.R;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BatteryGraphActivity extends ActionBarActivity {
    private static List<Long> timeValues;
    private static List<Integer> chargeValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_graph);

        BatteryModel model = this.getIntent().getParcelableExtra("model");
        List<BatteryEntry> entryList = model.getData(this);

        timeValues = new ArrayList<Long>();
        chargeValues = new ArrayList<Integer>();

        for (BatteryEntry anEntryList : entryList) {
            timeValues.add(anEntryList.getTime());
            chargeValues.add(anEntryList.getPercentage());
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BatteryGraphFragment())
                    .commit();
        }
    }

    public static class BatteryGraphFragment extends Fragment {
        private XYPlot plot;
        private PointF minXY;
        private PointF maxXY;
        private float domainLeftBoundary;
        private float domainRightBoundary;

        public BatteryGraphFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_battery_graph, container, false);
            plot = (XYPlot) rootView.findViewById(R.id.batteryPlot);

            if (timeValues == null || chargeValues == null)
                return rootView;

            long yesterday = Calendar.getInstance().getTimeInMillis()/1000-86400;
            long today = Calendar.getInstance().getTimeInMillis()/1000;

            XYSeries series2 = new SimpleXYSeries(timeValues, chargeValues, "Battery Level");
            domainLeftBoundary = Math.min(Collections.min(timeValues), yesterday);
            domainRightBoundary = Math.max(Collections.max(timeValues), today);

            // Remove certain components
            plot.getLayoutManager().remove(plot.getDomainLabelWidget());
            plot.getLayoutManager().remove(plot.getRangeLabelWidget());
            plot.getLayoutManager().remove(plot.getLegendWidget());
            plot.getLayoutManager().remove(plot.getTitleWidget());

            // Format the main graph
            plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
            plot.getGraphWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_LEFT, 0, YLayoutStyle.ABSOLUTE_FROM_TOP);
            plot.getGraphWidget().setSize(new SizeMetrics(0, SizeLayoutType.FILL, 0, SizeLayoutType.FILL));
            plot.getGraphWidget().setMargins(0,0,0,0);
            plot.getGraphWidget().setPadding(PixelUtils.dpToPix(27),PixelUtils.dpToPix(30),PixelUtils.dpToPix(30),PixelUtils.dpToPix(30));
            plot.getGraphWidget().setBackgroundPaint(null);
            plot.getGraphWidget().setGridBackgroundPaint(null);

            // Range Formatting
            plot.setTicksPerRangeLabel(1);
            plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
            plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 25);
            plot.getGraphWidget().setRangeGridLinePaint(new Paint(Color.BLACK));
            plot.getGraphWidget().setRangeOriginLinePaint(new Paint(Color.BLACK));
            plot.getGraphWidget().setRangeLabelWidth(PixelUtils.dpToPix(25));
            PaintUtils.setFontSizeDp(plot.getGraphWidget().getRangeLabelPaint(), 17);
            plot.getGraphWidget().setRangeLabelVerticalOffset(PixelUtils.dpToPix(-7));
            plot.getGraphWidget().setRangeLabelHorizontalOffset(PixelUtils.dpToPix(5));
            plot.getGraphWidget().getRangeLabelPaint().setColor(Color.GRAY);

            // Domain Formatting
            plot.getGraphWidget().setTicksPerDomainLabel(3);
            plot.setDomainBoundaries(yesterday, today, BoundaryMode.FIXED);
            PaintUtils.setFontSizeDp(plot.getGraphWidget().getDomainLabelPaint(), 9);
            plot.getGraphWidget().setDomainLabelVerticalOffset(PixelUtils.dpToPix(10));
            plot.getGraphWidget().getDomainLabelPaint().setColor(Color.GRAY);
            plot.getGraphWidget().setDomainGridLinePaint(new Paint(Color.BLACK));
            plot.getGraphWidget().setDomainOriginLinePaint(new Paint(Color.BLACK));

            LineAndPointFormatter formatter = new LineAndPointFormatter(Color.BLACK, Color.BLACK, null, null);
            plot.addSeries(series2, formatter);

            plot.setRangeValueFormat(new DecimalFormat("0'%'"));
            plot.setDomainValueFormat(new Format() {
                private SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yy k:mm");

                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    long timestamp = ((Number) obj).longValue() * 1000;
                    Date date = new Date(timestamp);
                    return dateFormat.format(date, toAppendTo, pos);
                }

                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });

            plot.calculateMinMaxVals();
            minXY = new PointF(yesterday, 0);
            maxXY = new PointF(today, 100);

            rootView.setOnTouchListener(new View.OnTouchListener() {
                static final int NONE = 0;
                static final int ONE_FINGER_DRAG = 1;
                static final int TWO_FINGERS_DRAG = 2;
                int mode = NONE;

                PointF firstFinger;
                float distBetweenFingers;
                boolean stopThread = false;

                public boolean onTouch(View arg0, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            firstFinger = new PointF(event.getX(), event.getY());
                            mode = ONE_FINGER_DRAG;
                            stopThread = true;
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            distBetweenFingers = spacing(event);
                            if (distBetweenFingers > 5f)
                                mode = TWO_FINGERS_DRAG;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == ONE_FINGER_DRAG) {
                                PointF oldFirstFinger = firstFinger;
                                firstFinger = new PointF(event.getX(), event.getY());
                                scroll(oldFirstFinger.x - firstFinger.x);
                                plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
                                plot.redraw();

                            } else if (mode == TWO_FINGERS_DRAG) {
                                float oldDist = distBetweenFingers;
                                distBetweenFingers = spacing(event);
                                zoom(oldDist / distBetweenFingers);
                                plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
                                plot.redraw();
                            }
                            break;
                    }
                    return true;
                }

                private void zoom(float scale) {
                    if (timeValues.size() <= 2)
                        return;

                    float domainSpan = maxXY.x - minXY.x;
                    float domainMidPoint = maxXY.x - domainSpan / 2.0f;
                    float offset = domainSpan * scale / 2.0f;
                    minXY.x = domainMidPoint - offset;
                    maxXY.x = domainMidPoint + offset;
                    minXY.x = Math.max(minXY.x, domainLeftBoundary);
                    maxXY.x = Math.min(maxXY.x, domainRightBoundary);
                    clampToDomainBounds(domainSpan);
                }

                private void scroll(float pan) {
                    if (timeValues.size() <= 2)
                        return;

                    float domainSpan = maxXY.x - minXY.x;
                    float step = domainSpan / plot.getWidth();
                    float offset = pan * step;
                    minXY.x = minXY.x + offset;
                    maxXY.x = maxXY.x + offset;
                    clampToDomainBounds(domainSpan);
                }

                private void clampToDomainBounds(float domainSpan) {
                    if (minXY.x < domainLeftBoundary) {
                        minXY.x = domainLeftBoundary;
                        maxXY.x = domainLeftBoundary + domainSpan;
                    } else if (maxXY.x > domainRightBoundary) {
                        maxXY.x = domainRightBoundary;
                        minXY.x = domainRightBoundary - domainSpan;
                    }
                }

                private float spacing(MotionEvent event) {
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    return FloatMath.sqrt(x * x + y * y);
                }
            });

            return rootView;
        }
    }
}
