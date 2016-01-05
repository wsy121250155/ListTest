package com.example.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import tools.ViewMeasure;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyListView extends ListView {
	private String TAG = MyListView.this.getClass().getName();
	private View footer;
	private View header;

	private void init(Context context) {
		initView(context);
		addBottomListener();
	};

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private int headerHeight;// 头布局的高度

	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);

		header = inflater.inflate(R.layout.header_layout, null);
		// 让系统测量header的大小,获得header的高度
		ViewMeasure.measureView(header);
		headerHeight = header.getMeasuredHeight();
		// 隐藏header
		ViewMeasure.topPadding(header, -headerHeight);
		this.addHeaderView(header);

		footer = inflater.inflate(R.layout.footer_layout, null);
		// 注意这里不是设置footer的可见性
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
		this.addFooterView(footer);
	}

	public void setReachBottomListener(ReachBottomListener reachBottomListener) {
		this.reachBottomListener = reachBottomListener;
	}

	private boolean isRemark;// 标记，当前listview是在最顶端被摁下的
	private int startY;
	private int state;

	public interface HeadFreshListener {
		public void refresh();
	}

	private HeadFreshListener headFreshListener;

	public void setHeadFreshListener(HeadFreshListener headFreshListener) {
		this.headFreshListener = headFreshListener;
	}

	public void reflashComplete() {
		state = NONE;
		isRemark = false;
		reflashViewByState();
		TextView lastupdatetime = (TextView) header
				.findViewById(R.id.lastupdate_time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String time = format.format(date);
		lastupdatetime.setText(time);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 如果并未设置下拉刷新监听，则直接返回。
		if (null == headFreshListener) {
			return super.onTouchEvent(ev);
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isRemark) {
				startY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			if (state == RELEASE) {
				state = REFLASHING;
				reflashViewByState();
				headFreshListener.refresh();
			} else if (state == PULL) {
				state = NONE;
				isRemark = false;
				reflashViewByState();
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void onMove(MotionEvent ev) {
		if (!isRemark) {
			return;
		}
		int tempY = (int) ev.getY();
		int space = tempY - startY;// 手指滑动的纵向距离
		int topPadding = space - headerHeight;// 准备设置的header的toppadding
		// 限制header布局的最大toppadding，使得不会一直拖拽到最下
		if (topPadding > 40) {
			return;
		}
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				reflashViewByState();
			}
			break;
		case PULL:
			Log.i(TAG, "topPadding: " + topPadding + " ; space: " + space);
			Log.i(TAG, "headerHeight + 30: " + (headerHeight + 30));
			ViewMeasure.topPadding(header, topPadding);
			if (space > headerHeight + 30
					&& scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				state = RELEASE;
				reflashViewByState();
			}
			break;
		case RELEASE:
			ViewMeasure.topPadding(header, topPadding);
			if (space < headerHeight + 30) {
				state = PULL;
				reflashViewByState();
			} else if (space <= 0) {
				state = NONE;
				isRemark = false;
				reflashViewByState();
			}
			break;
		}
	}

	final int NONE = 0;// 正常状态
	final int PULL = 1;// 提示下拉可以刷新的状态
	final int RELEASE = 2;// 提示松开释放的状态
	final int REFLASHING = 3;// 提示正在刷新的状态

	private void reflashViewByState() {
		TextView tip = (TextView) header.findViewById(R.id.tip);
		ImageView arrow = (ImageView) header.findViewById(R.id.arrow);
		ProgressBar progress = (ProgressBar) header.findViewById(R.id.progress);
		RotateAnimation anim = new RotateAnimation(0, 180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(500);
		anim.setFillAfter(true);
		RotateAnimation anim1 = new RotateAnimation(180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim1.setDuration(500);
		anim1.setFillAfter(true);
		switch (state) {
		case NONE:
			arrow.clearAnimation();
			ViewMeasure.topPadding(header, -headerHeight);
			break;

		case PULL:
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			tip.setText("下拉刷新");
			arrow.clearAnimation();
			arrow.setAnimation(anim1);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			tip.setText("释放刷新");
			arrow.clearAnimation();
			arrow.setAnimation(anim);
			break;
		case REFLASHING:
			ViewMeasure.topPadding(header, 50);// *
			arrow.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
			tip.setText("正在刷新...");
			arrow.clearAnimation();
			break;
		}
	}

	private boolean reachBottom;
	private boolean isFreshing;// 确保一次到底恰好触发一次刷新事件
	private int scrollState;

	private void addBottomListener() {
		// add bottom listener
		this.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				MyListView.this.scrollState = scrollState;
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (!isFreshing && reachBottom
							&& reachBottomListener != null) {
						isFreshing = true;
						footer.findViewById(R.id.load_layout).setVisibility(
								View.VISIBLE);
						reachBottomListener.reachBottom();
					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				reachBottom = false;
				// 最后一个已经显示出来
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					reachBottom = true;
				}
				if (0 == firstVisibleItem) {
					isRemark = true;
				}
			}
		});
	}

	public void loadComplete() {
		isFreshing = false;
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
	}

	public interface ReachBottomListener {
		public void reachBottom();
	}

	private ReachBottomListener reachBottomListener;

}
