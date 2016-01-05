package tools;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

public class ViewMeasure {
	public static void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	public static void topPadding(View view, int topPadding) {
		view.setPadding(view.getPaddingLeft(), topPadding,
				view.getPaddingRight(), view.getPaddingBottom());
		view.invalidate();
	}
}
