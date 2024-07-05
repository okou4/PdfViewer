package app.grapheneos.pdfviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

/*
    The GestureHelper present a simple gesture api for the PdfViewer
*/

class GestureHelper {
    public interface GestureListener {
        boolean onTapLeft();
        boolean onTapMiddle();
        boolean onTapRight();
        // Can be replaced with ratio when supported
        void onZoomIn(float value);
        void onZoomOut(float value);
        void onZoomEnd();
    }

    static TapZones createTapZones(final int containerWidth) {
        final float leftAndRightWidth = (float)(containerWidth * 0.4);  // Left and Right zones are 40% each
        final float middleWidth = (float)(containerWidth * 0.2);        // Middle zone is 20%

        final float leftZoneLeft = 0.0f;
        final float middleZoneRight = leftAndRightWidth + middleWidth;
        final float rightZoneRight = middleZoneRight + leftAndRightWidth;

        final RectF leftZone = new RectF(leftZoneLeft, 0, leftAndRightWidth, 1);
        final RectF middleZone = new RectF(leftAndRightWidth, 0, middleZoneRight, 1);
        final RectF rightZone = new RectF(middleZoneRight, 0, rightZoneRight, 1);

        return new TapZones(leftZone, middleZone, rightZone);
    }

    @SuppressLint("ClickableViewAccessibility")
    static void attach(Context context, View gestureView, GestureListener listener) {

        final GestureDetector detector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
                        final int viewWidth = gestureView.getWidth();
                        final float tapX = motionEvent.getX();
                        final TapZones zones = createTapZones(viewWidth);

                        if(zones.getLeft().contains(tapX, 0)) {
                            return listener.onTapLeft();
                        } else if(zones.getMiddle().contains(tapX, 0)) {
                            return listener.onTapMiddle();
                        } else if(zones.getRight().contains(tapX, 0)) {
                            return listener.onTapRight();
                        }

                        return super.onSingleTapUp(motionEvent);
                    }
                });

        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    final float SPAN_RATIO = 600;
                    float initialSpan;
                    float prevNbStep;

                    @Override
                    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                        initialSpan = detector.getCurrentSpan();
                        prevNbStep = 0;
                        return true;
                    }

                    @Override
                    public boolean onScale(@NonNull ScaleGestureDetector detector) {
                        float spanDiff = initialSpan - detector.getCurrentSpan();
                        float curNbStep = spanDiff / SPAN_RATIO;

                        float stepDiff = curNbStep - prevNbStep;
                        if (stepDiff > 0) {
                            listener.onZoomOut(stepDiff);
                        } else {
                            listener.onZoomIn(Math.abs(stepDiff));
                        }
                        prevNbStep = curNbStep;

                        return true;
                    }

                    @Override
                    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                        listener.onZoomEnd();
                    }
                });

        gestureView.setOnTouchListener((view, motionEvent) -> {
            detector.onTouchEvent(motionEvent);
            scaleDetector.onTouchEvent(motionEvent);
            return false;
        });
    }

}
