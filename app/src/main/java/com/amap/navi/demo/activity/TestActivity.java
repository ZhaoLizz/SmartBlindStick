package com.amap.navi.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.navi.demo.R;
import com.amap.navi.demo.util.AmapTTSController;
import com.amap.poisearch.searchmodule.AMapSearchUtil;
import com.amap.tripmodule.ITripHostModule;
import com.amap.tripmodule.TripHostModuleDelegate;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.amap.poisearch.searchmodule.ISearchModule.IDelegate.DEST_POI_TYPE;
import static com.amap.poisearch.searchmodule.ISearchModule.IDelegate.START_POI_TYPE;

public class TestActivity extends Activity {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private EditText inputTv;
    private Button testBt;
    AmapTTSController amapTTSController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        inputTv = (EditText) findViewById(R.id.des_tv);
        testBt = (Button) findViewById(R.id.test_bt);

        //初始化语音
        amapTTSController = AmapTTSController.getInstance(getApplicationContext());
        amapTTSController.init();

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        double curLat = aMapLocation.getLatitude();
                        double curLng = aMapLocation.getLongitude();
                        Logger.d("当前位置 : " + aMapLocation.getAddress());
                        Logger.d("当前城市 : " + aMapLocation.getCity() + "code : " + aMapLocation.getCityCode());

                        //test search
                        final long mCurrSearchId = java.lang.System.currentTimeMillis();
                        String inputStr = inputTv.getText().toString();
                        AMapSearchUtil.doSug(getApplicationContext(), mCurrSearchId, inputStr, aMapLocation.getCityCode(), new LatLng(curLat,curLng), new AMapSearchUtil.OnSugListener() {
                            @Override
                            public void onSug(List<PoiItem> list, int i, long searchId) {
                                // 只取最新的结果
                                if (searchId < mCurrSearchId) {
                                    return;
                                }

                                int k = 0;
                                for (PoiItem poiItem : list) {
                                    Log.d(k++ + "search", poiItem.getTitle() + " " + poiItem.getAdName() + " " + poiItem.getCityName() + " " + poiItem.getLatLonPoint());
                                    amapTTSController.onGetNavigationText(poiItem.getTitle());
                                }
                            }
                        });
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        testBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动定位,然后搜索目的地
                mLocationClient.startLocation();
            }
        });
    }


}
