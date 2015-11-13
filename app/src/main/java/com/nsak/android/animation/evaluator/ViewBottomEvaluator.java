package com.nsak.android.animation.evaluator;

/**
 * @author Vlad Namashko
 */
import android.animation.IntEvaluator;
import android.view.View;

public class ViewBottomEvaluator extends IntEvaluator {

    private View mView;
    public ViewBottomEvaluator(View v) {
        mView = v;
    }

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int num = super.evaluate(fraction, startValue, endValue);
        mView.setBottom(num);
        return num;
    }

}
