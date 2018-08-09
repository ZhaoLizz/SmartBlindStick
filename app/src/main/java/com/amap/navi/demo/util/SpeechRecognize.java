package com.amap.navi.demo.util;

import android.content.Context;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

public class SpeechRecognize {
    /**
     * 初始化语音识别
     */
    public void initSpeech(final Context context,RecognizerDialogListener listener) {
        try {
            //1.创建RecognizerDialog对象
            RecognizerDialog mDialog = new RecognizerDialog(context, null);
            //2.设置accent、language等参数
            mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
            //3.设置回调接口
            mDialog.setListener(listener);
            //4.显示dialog，接收语音输入
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析语音json
     */
    public static String parseVoice(String resultString) {
        Gson gson = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }
}
