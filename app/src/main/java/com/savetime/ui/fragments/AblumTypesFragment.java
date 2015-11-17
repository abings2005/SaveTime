package com.savetime.ui.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.savetime.R;
import com.savetime.injector.components.DaggerHomeComponent;
import com.savetime.injector.model.AblumModule;
import com.savetime.injector.model.ActivityModule;
import com.savetime.model.entities.AlbumType;
import com.savetime.mvp.ablumtype.HomeFragmentPresenter;
import com.savetime.mvp.ablumtype.IHomeView;
import com.savetime.netstatus.NetUtils;
import com.savetime.ui.base.BaseFragment;
import com.savetime.ui.itemtouchhelper.OnStartDragListener;
import com.savetime.ui.itemtouchhelper.RecyclerDragAdapter;
import com.savetime.ui.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.savetime.ui.utils.RotateAnimation;
import com.savetime.ui.widgets.PullZoomNestedScrollView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Administrator on 2015-11-11.
 */
public class AblumTypesFragment extends BaseFragment implements PullZoomNestedScrollView.OnPullZoomListener,RotateAnimation.InterpolatedTimeListener,IHomeView,OnStartDragListener {
   
    @Bind(R.id.main_layout)
    View main_layout;
    @Bind(R.id.mCoverMaskView)
    ImageView mCoverMaskView;
    @Bind(R.id.mCoverView)
    ImageView mCoverView;
    @Bind(R.id.mlist)
    RecyclerView  mListView;
    @Bind(R.id.scroll)
    PullZoomNestedScrollView  mScroll;
    @Inject
    HomeFragmentPresenter mHomeFragmentPresenter;
    

    private RecyclerDragAdapter adapter;
    private FrameLayout.LayoutParams mLayoutParams;
    private ItemTouchHelper mItemTouchHelper;
    
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_ablumtype;
    }
    
    
    @Override
    protected void initializeDependencyInjector() {
        DaggerHomeComponent.builder().activityModule(new ActivityModule(getActivity()))
                .appComponent(app.getAppComponent()).ablumModule(new AblumModule("")).build().inject(this);
        mHomeFragmentPresenter.attachView(this, getActivity());        
    }

    @Override
    protected void initViewsAndEvents() {
        adapter = new RecyclerDragAdapter(getActivity(), this);
        //载入可以拖动的GRID
        mListView.setHasFixedSize(true);
        mListView.setAdapter(adapter);
        final int spanCount = 3;
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        mListView.setLayoutManager(layoutManager);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mListView);

        mScroll.setOnPullZoomListener(this);
    }

    @Override
    protected void onFirstUserVisible() {
        // 初始数据
        if (NetUtils.isNetworkConnected(getActivity())) {
            toggleShowLoading(true,"正在载入数据");
            mHomeFragmentPresenter.loadAblumTypeData(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        } else {
            toggleNetworkError(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //    mHomeFragmentPresenter.loadAblumTypeData(mScreenWidth, mLayoutParams.height);
                }
            });
        }
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadingTargetView() {
        return main_layout;
    }

    @Override
    public void bindDraggableGridViewPagerDataList(List<AlbumType> list) {
        for (int i=0;i<78;i++){
            adapter.addData("测试"+i);
        }

        toggleShowLoading(false,"");
    }

    @Override
    public void showLoadingView(boolean toggle, String msg) {

    }

    @Override
    public void toggleShow(int Type, boolean toggle, String msg) {
       
    }

    @Override
    public void LoadingHeardImage(DrawableRequestBuilder<String> drb) {
        if (null != drb) {
/*            drb.bitmapTransform(new CenterCrop(getActivity()))
                    .into(mCoverView);*/

/*            drb.bitmapTransform(new BlurTransformation(getActivity(), 25, 1)).crossFade().into(mCoverMaskView);
            ViewHelper.setAlpha(mCoverMaskView, 10);*/
        }
        Log.e("测试","透明度--"+mCoverMaskView.getAlpha());
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        Log.e("测试", "点击了onItemClick" + viewHolder.getLayoutPosition());
     //   scrollView.scrollTo(0, 0);
    }


    @Override
    public void interpolatedTime(float interpolatedTime) {
        // 翻转图片时候更换图片
        // 监听到翻转进度过半时，更新图片内容
        if (interpolatedTime > 0.5f) {
/*            Glide.with(getActivity()).load(drawable_id[current_id])
                    .override(image_centerHeader.getWidth(), image_centerHeader.getHeight())
                    .bitmapTransform(new CenterCrop(getActivity()),
                            new MaskTransformation(getActivity(), R.drawable.mask_starfish))
                    .into(image_centerHeader);*/
        }
    }


    private void bounceAnimateView(View view) {
        if (view == null) {
            return;
        }

        Animator swing = ObjectAnimator.ofFloat(view, "rotationX", 0, 30, -20, 0);
        swing.setDuration(400);
        swing.setInterpolator(new AccelerateInterpolator());
        swing.start();
    }


    @Override
    public void onPullZoomProgress(ViewGroup view, int oldTopViewHeight, int nowTopViewHeight) {
        RotateAnimation animation = new RotateAnimation();
        animation.setFillAfter(true);
        animation.setInterpolatedTimeListener(this);
        //image_centerHeader.startAnimation(animation);
        // current_id = current_id < drawable_id.length - 1 ? ++current_id : 0;
       // ViewHelper.setAlpha(mCoverMaskView, Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(nowTopViewHeight) / oldTopViewHeight)));
        log("透明度="+Math.max(0f,255-(nowTopViewHeight-oldTopViewHeight)*2));
        mCoverMaskView.setAlpha(Math.max(0f,255-(nowTopViewHeight-oldTopViewHeight)*2));
    }

    @Override
    public void onPullZoomAnimEnd(ViewGroup view, int state, int deltaY) {

    }
}
