package com.originate.graphit.metrics.network;

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
import com.androidplot.xy.*;

import com.originate.graphit.R;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NetworkGraphActivity extends ActionBarActivity {
    private static final String TAG_NETWORK_FRAGMENT = "network_fragment";

    private static NetworkModel model;
    private static List<Long> timeValues;
    private static List<Float> downValues;
    private static NetworkGraphFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_graph);

        model = this.getIntent().getParcelableExtra("model");
        refreshData();

        fragment = (NetworkGraphFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_NETWORK_FRAGMENT);

        if (fragment == null) {
            fragment = new NetworkGraphFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, TAG_NETWORK_FRAGMENT).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graphs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshData();
            fragment.loadData();
        } else if (id == R.id.action_prevDay) {
            fragment.viewPrevDay();
        } else if (id == R.id.action_today) {
            fragment.viewToday();
        } else if (id == R.id.action_nextDay) {
            fragment.viewNextDay();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        List<NetworkEntry> entryList = model.getData(this);

        timeValues = new ArrayList<Long>();
        downValues = new ArrayList<Float>();

        for (NetworkEntry entry : entryList) {
            timeValues.add(entry.getTime());
            downValues.add(entry.getDown()/1048576); // Divide by 1024^2 to display as GB
        }
    }

    public static class NetworkGraphFragment extends Fragment {
        private XYPlot plot;
        private PointF minXY;
        private PointF maxXY;
        private XYSeries series_down;
        private int dayOffset;

        private static final int rangeMax = 100;
        private static final int rangeMin = 0;

        public NetworkGraphFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_network_graph, container, false);
            plot = (XYPlot) rootView.findViewById(R.id.networkPlot);

            loadData();
            viewToday();

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
            PaintUtils.setFontSizeDp(plot.getGraphWidget().getRangeLabelPaint(), 17);
            plot.setTicksPerRangeLabel(1);
            plot.setRangeStep(XYStepMode.SUBDIVIDE, 8);
            plot.getGraphWidget().setRangeGridLinePaint(new Paint(Color.BLACK));
            plot.getGraphWidget().setRangeOriginLinePaint(new Paint(Color.BLACK));
            plot.getGraphWidget().setRangeLabelWidth(PixelUtils.dpToPix(75));
            plot.getGraphWidget().setRangeLabelVerticalOffset(PixelUtils.dpToPix(-6));
            plot.getGraphWidget().setRangeLabelHorizontalOffset(PixelUtils.dpToPix(5));
            plot.getGraphWidget().getRangeLabelPaint().setColor(Color.GRAY);

            // Domain Formatting
            PaintUtils.setFontSizeDp(plot.getGraphWidget().getDomainLabelPaint(), 9);
            plot.setDomainStep(XYStepMode.SUBDIVIDE, 4);
            plot.getGraphWidget().setTicksPerDomainLabel(1);
            plot.getGraphWidget().setDomainLabelVerticalOffset(PixelUtils.dpToPix(10));
            plot.getGraphWidget().getDomainLabelPaint().setColor(Color.GRAY);
            plot.getGraphWidget().setDomainGridLinePaint(new Paint(Color.BLACK));
            plot.getGraphWidget().setDomainOriginLinePaint(new Paint(Color.BLACK));

            plot.setRangeValueFormat(new DecimalFormat("0.000 GB"));
            plot.setDomainValueFormat(new Format() {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yy k:mm");

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
                }

                private void scroll(float pan) {
                    if (timeValues.size() <= 2)
                        return;

                    float domainSpan = maxXY.x - minXY.x;
                    float step = domainSpan / plot.getWidth();
                    float offset = pan * step;
                    minXY.x = minXY.x + offset;
                    maxXY.x = maxXY.x + offset;
                }

                private float spacing(MotionEvent event) {
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    return FloatMath.sqrt(x * x + y * y);
                }
            });

            return rootView;
        }

        public void loadData() {
            if (timeValues == null || downValues == null ||
                    timeValues.size() == 0 || downValues.size() == 0)
                return;

            plot.removeSeries(series_down);
            series_down = new SimpleXYSeries(timeValues, downValues, "Network Down");
            LineAndPointFormatter formatter = new LineAndPointFormatter(Color.BLUE, null, model.getColor() & 0x80FFFFFF, null);
            plot.addSeries(series_down, formatter);
            plot.redraw();
        }

        public void viewPrevDay() {
            dayOffset--;
            viewSelectedDay();
        }

        public void viewNextDay() {
            dayOffset++;
            viewSelectedDay();
        }

        public void viewToday() {
            dayOffset = 0;
            viewSelectedDay();
        }

        private void viewSelectedDay() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long dayStart = calendar.getTimeInMillis()/1000 + dayOffset*86400;
            long dayEnd = calendar.getTimeInMillis()/1000 + (dayOffset+1)*86400;
            plot.setDomainBoundaries(dayStart, dayEnd, BoundaryMode.FIXED);
            plot.setRangeBoundaries(null, null, BoundaryMode.AUTO);
            plot.setRangeTopMin(0.5);
            minXY = new PointF(dayStart, rangeMin);
            maxXY = new PointF(dayEnd, rangeMax);
            plot.redraw();
        }
    }
}
