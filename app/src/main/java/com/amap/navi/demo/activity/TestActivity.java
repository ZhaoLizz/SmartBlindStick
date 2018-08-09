package com.amap.navi.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.navi.demo.R;
import com.amap.navi.demo.util.AmapTTSController;
import com.amap.poisearch.searchmodule.AMapSearchUtil;
import com.orhanobut.logger.Logger;

import java.util.List;

public class TestActivity extends Activity {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private EditText inputTv;
    private Button searchBt;
    private Button upBt;
    private Button downBt;
    private Button confrimBt;
    AmapTTSController amapTTSController;
    public static final String EXTRA_CUR_LATLNG = "extra_cur_latlng";
    public static final String EXTRA_DES_LATLNG = "extra_des_latlng";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        inputTv = (EditText) findViewById(R.id.des_tv);
        searchBt = (Button) findViewById(R.id.search_bt);
        upBt = (Button) findViewById(R.id.up_bt);
        downBt = (Button) findViewById(R.id.down_bt);
        confrimBt = (Button) findViewById(R.id.confirm_bt);
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
                        final LatLng curLatLng = new LatLng(curLat, curLng);
                        Logger.d("当前位置 : " + aMapLocation.getAddress());
                        Logger.d("当前城市 : " + aMapLocation.getCity() + "code : " + aMapLocation.getCityCode());

                        //test search
                        final long mCurrSearchId = java.lang.System.currentTimeMillis();
                        String inputStr = inputTv.getText().toString();
                        AMapSearchUtil.doSug(getApplicationContext(), mCurrSearchId, inputStr, aMapLocation.getCityCode(), curLatLng, new AMapSearchUtil.OnSugListener() {
                            @Override
                            public void onSug(final List<PoiItem> list, int i, long searchId) {
                                // 只取最新的结果
                                if (searchId < mCurrSearchId) {
                                    return;
                                }

                                final int[] k = {0};
                                final int m = list.size();
                                amapTTSController.onGetNavigationText("检测到" + m + "个位置选项,第一个为" + list.get(0).getTitle());

                                //输出测试
                                for (PoiItem poiItem : list) {
                                    Log.d("search", poiItem.getTitle() + " " + poiItem.getAdName() + " " + poiItem.getCityName() + " " + poiItem.getLatLonPoint());
                                }

                                downBt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        amapTTSController.stopSpeaking();
                                        if (k[0] < m -1) {
                                            k[0]++;
                                            Logger.d(k[0]);
                                            amapTTSController.onGetNavigationText("第" + k[0] + "个选项为" + list.get(k[0]).getTitle());
                                        } else {
                                            amapTTSController.onGetNavigationText("已经是最后一个位置选项");
                                        }
                                    }
                                });

                                upBt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        amapTTSController.stopSpeaking();

                                        if (k[0] > 0) {
                                            k[0]--;
                                            Logger.d(k[0]);
                                            amapTTSController.onGetNavigationText("第" + k[0] + "个选项为" + list.get(k[0]).getTitle());
                                        } else {
                                            amapTTSController.onGetNavigationText("已经是第一个位置选项");
                                        }
                                    }
                                });

                                confrimBt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        amapTTSController.stopSpeaking();
                                        PoiItem desPoiItem = list.get(k[0]);
                                        LatLng desLatLng = new LatLng(desPoiItem.getLatLonPoint().getLatitude(), desPoiItem.getLatLonPoint().getLongitude());
                                        Intent intent = new Intent(TestActivity.this, WalkRouteCalculateActivity.class);
                                        intent.putExtra(EXTRA_CUR_LATLNG, curLatLng);
                                        intent.putExtra(EXTRA_DES_LATLNG, desLatLng);
                                        startActivity(intent);
                                    }
                                });

                            }
                        });
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
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

        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动定位,然后搜索目的地
                mLocationClient.startLocation();
            }
        });
    }

    /**
     * 选择一个目标点,然后打开步行导航
     *
     * @param list
     * @return
     */
    private void selectPoiItem(List<PoiItem> list) {

    }

    @Override
    protected void onPause() {
        super.onPause();

//        仅仅是停止你当前在说的这句话，一会到新的路口还是会再说的
        amapTTSController.stopSpeaking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        amapTTSController.stopSpeaking();
    }
}
