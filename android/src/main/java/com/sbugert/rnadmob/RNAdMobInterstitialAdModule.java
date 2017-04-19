package com.sbugert.rnadmob;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class RNAdMobInterstitialAdModule extends ReactContextBaseJavaModule {
  InterstitialAd mInterstitialAd;
  String adUnitID;
  String testDeviceID;

  @Override
  public String getName() {
    return "RNAdMobInterstitial";
  }

  public RNAdMobInterstitialAdModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mInterstitialAd = new InterstitialAd(reactContext);

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        mInterstitialAd.setAdListener(new AdListener() {
          @Override
          public void onAdClosed() {
            sendEvent("interstitialDidClose", null);
          }
          @Override
          public void onAdFailedToLoad(int errorCode) {
            WritableMap event = Arguments.createMap();
            String errorString = null;
            switch (errorCode) {
              case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorString = "ERROR_CODE_INTERNAL_ERROR";
                break;
              case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorString = "ERROR_CODE_INVALID_REQUEST";
                break;
              case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorString = "ERROR_CODE_NETWORK_ERROR";
                break;
              case AdRequest.ERROR_CODE_NO_FILL:
                errorString = "ERROR_CODE_NO_FILL";
                break;
            }
            event.putString("error", errorString);
            sendEvent("interstitialDidFailToLoad", event);
          }
          @Override
          public void onAdLeftApplication() {
            sendEvent("interstitialWillLeaveApplication", null);
          }
          @Override
          public void onAdLoaded() {
            sendEvent("interstitialDidLoad", null);
          }
          @Override
          public void onAdOpened() {
            sendEvent("interstitialDidOpen", null);
          }
        });
      }
    });
  }
  private void sendEvent(String eventName, @Nullable WritableMap params) {
    getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
  }

  @ReactMethod
  public void setAdUnitID(String adUnitID) {
    mInterstitialAd.setAdUnitId(adUnitID);
  }

  @ReactMethod
  public void setTestDeviceID(String testDeviceID) {
    this.testDeviceID = testDeviceID;
  }

  @ReactMethod
  public void requestAd() {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run () {
        if (mInterstitialAd.isLoaded() || mInterstitialAd.isLoading()) return;
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        if (testDeviceID != null){
          if (testDeviceID.equals("EMULATOR")) {
            adRequestBuilder = adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
          } else {
            adRequestBuilder = adRequestBuilder.addTestDevice(testDeviceID);
          }
        }
        AdRequest adRequest = adRequestBuilder.build();
        mInterstitialAd.loadAd(adRequest);
      }
    });
  }

  @ReactMethod
  public void showAd() {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run () {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
          mInterstitialAd.show();
        }
      }
    });
  }

  @ReactMethod
  public void isReady(final Callback callback) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run () {
        callback.invoke(mInterstitialAd.isLoaded());
      }
    });
  }
}
