package com.amap.navi.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.navi.demo.R;
import com.orhanobut.logger.Logger;


public class WalkRouteCalculateActivity extends BaseActivity {
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getIntent();
        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
        //语音回调在BaseActivity里面
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        if (mIntent != null) {
            LatLng curLatLng = mIntent.getParcelableExtra(TestActivity.EXTRA_CUR_LATLNG);
            LatLng desLatLng = mIntent.getParcelableExtra(TestActivity.EXTRA_DES_LATLNG);
            mAMapNavi.calculateWalkRoute(new NaviLatLng(curLatLng.latitude, curLatLng.longitude), new NaviLatLng(desLatLng.latitude, desLatLng.longitude));
        } else {
            Logger.e("mIntent is null!");
        }
    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
//        mAMapNavi.startNavi(NaviType.GPS);
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }
}
