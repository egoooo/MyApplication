package newjohn.com.myapplication.listviewscroll;

/**
 * Created by Administrator on 2017/10/25.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

public class CHScrollView extends HorizontalScrollView {

    private Context context;
    float startx = 0;
    float offset;

    public CHScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public CHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CHScrollView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 进行触摸赋值
        CHScrollViewHelper.mTouchView = this;
        return super.onTouchEvent(ev);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // 当当前的CHSCrollView被触摸时，滑动其它
        if (CHScrollViewHelper.mTouchView == this) {
            onScrollChanged(l, t, oldl, oldt, 0);
        } else {
            super.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt, int none) {
        for (CHScrollView scrollView : CHScrollViewHelper.mHScrollViews) {
            // 防止重复滑动
            if (CHScrollViewHelper.mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }

    public static class CHScrollViewHelper {
        public static HorizontalScrollView mTouchView;
        public static List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();

        public static void addHViews(final CHScrollView hScrollView, AutoListView autoListView) {
            if (!CHScrollViewHelper.mHScrollViews.isEmpty()) {
                int size = CHScrollViewHelper.mHScrollViews.size();
                CHScrollView scrollView = CHScrollViewHelper.mHScrollViews.get(size - 1);
                final int scrollX = scrollView.getScrollX();
                // 第一次满屏后，向下滑动，有一条数据在开始时未加入
                if (scrollX != 0) {
                    autoListView.post(new Runnable() {
                        @Override
                        public void run() {
                            // 当listView刷新完成之后，把该条移动到最终位置
                            hScrollView.scrollTo(scrollX, 0);
                        }
                    });
                }
            }
            CHScrollViewHelper.mHScrollViews.add(hScrollView);
        }
    }
}

