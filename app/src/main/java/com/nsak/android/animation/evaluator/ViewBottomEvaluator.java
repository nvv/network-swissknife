package com.nsak.android.animation.evaluator;

/**
 * @author Vlad Namashko
 */
import android.animation.IntEvaluator;
import android.view.View;

public class ViewBottomEvaluator extends IntEvaluator {

    private View v;
    public ViewBottomEvaluator(View v) {
        this.v = v;
    }

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int num = super.evaluate(fraction, startValue, endValue);
        v.setBottom(num);
        return num;
    }

}
