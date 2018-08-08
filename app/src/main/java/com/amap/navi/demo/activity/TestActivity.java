package com.amap.navi.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.navi.demo.R;
import com.amap.poisearch.searchmodule.AMapSearchUtil;
import com.amap.tripmodule.ITripHostModule;
import com.amap.tripmodule.TripHostModuleDelegate;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.amap.poisearch.searchmodule.ISearchModule.IDelegate.DEST_POI_TYPE;
import static com.amap.poisearch.searchmodule.ISearchModule.IDelegate.START_POI_TYPE;

public class TestActivity extends AppCompatActivity {
    private TextView desTextView;
    private TripHostModuleDelegate mTripHostDelegate;
    private PoiItem mStartPoi;
    private PoiItem mDestPoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        desTextView = (TextView) findViewById(R.id.des_tv);
        String destination = desTextView.getText().toString();

        mTripHostDelegate = new TripHostModuleDelegate();
        mTripHostDelegate.bindParentDelegate(mParentTripDelegate);
        mTripHostDelegate.onCreate(savedInstanceState);


        //test--------------------
        Logger.d("当前位置: " + mTripHostDelegate.getCurrLocation().getAddress() + " tostring: " + mTripHostDelegate.getCurrLocation());
        //test search
        final long mCurrSearchId = java.lang.System.currentTimeMillis();
        String inputStr = "一中";
        AMapSearchUtil.doSug(getApplicationContext(), mCurrSearchId, inputStr, mTripHostDelegate.getCurrCity().getAdcode(), null, new AMapSearchUtil.OnSugListener() {
            @Override
            public void onSug(List<PoiItem> list, int i, long searchId) {
                // 只取最新的结果
                if (searchId < mCurrSearchId) {
                    return;
                }

                int k = 0;
                for (PoiItem poiItem : list) {
                    Log.d(k++ + "search", poiItem.getTitle() + " " + poiItem.getAdName() + " " + poiItem.getCityName() + " " + poiItem.getLatLonPoint());
                }
            }
        });
    }

    private ITripHostModule.IParentDelegate mParentTripDelegate = new ITripHostModule.IParentDelegate() {
        @Override
        public void onIconClick() {
        }

        @Override
        public void onMsgClick() {
        }

        /**
         * 选择城市
         */
        @Override
        public void onChooseCity() {
        }

        /**
         * 选择目的地
         */
        @Override
        public void onChooseDestPoi() {



        }

        /**
         * 选择起点
         */
        @Override
        public void onChooseStartPoi() {
        }

        @Override
        public void onBackToInputMode() {
        }

        @Override
        public void onStartPoiChange(PoiItem poiItem) {
            if (poiItem == null) {
                return;
            }

            mTripHostDelegate.setStartLocation(poiItem.getTitle());
            mStartPoi = poiItem;
        }

        @Override
        public void onStartCall() {
        }
    };
}
