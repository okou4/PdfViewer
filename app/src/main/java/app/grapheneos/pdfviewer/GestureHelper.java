package app.grapheneos.pdfviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicReference;

/*
    The GestureHelper present a simple gesture api for the PdfViewer
*/

class GestureHelper {
    public interface GestureListener {
        boolean onTapUpLeft();
        boolean onTapUpMiddle();
        boolean onTapUpRight();
        // Can be replaced with ratio when supported
        void onZoomIn(float value);
        void onZoomOut(float value);
        void onZoomEnd();
    }

    @SuppressLint("ClickableViewAccessibility")
    static void attach(Context context, View gestureView, GestureListener listener) {
        AtomicReference<TapZones> tapZones = new AtomicReference<>(new TapZones(0));

        gestureView.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
            // When attach is called by the PdfViewer activity the gestureView (webView) is empty
            // and with the width of 0, it will only get its true width once the pdf has been
            // loaded. This callback will create the TapZones once the pdf has been loaded.
            tapZones.set(new TapZones(view.getWidth()));
        });

        final GestureDetector detector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
                        final float tapX = motionEvent.getX();
                        final TapZones zones = tapZones.get();

                        if(zones.getLeft().contains(tapX, 0)) {
                            return listener.onTapUpLeft();
                        } else if(zones.getMiddle().contains(tapX, 0)) {
                            return listener.onTapUpMiddle();
                        } else if(zones.getRight().contains(tapX, 0)) {
                            return listener.onTapUpRight();
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
