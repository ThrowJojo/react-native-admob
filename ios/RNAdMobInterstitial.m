#import "RNAdMobInterstitial.h"

@implementation RNAdMobInterstitial {
  GADInterstitial  *_interstitial;
  NSString *_adUnitID;
  NSString *_testDeviceID;
}

@synthesize bridge = _bridge;

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

#pragma mark exported methods

RCT_EXPORT_METHOD(setAdUnitID:(NSString *)adUnitID)
{
  _adUnitID = adUnitID;
}

RCT_EXPORT_METHOD(setTestDeviceID:(NSString *)testDeviceID)
{
  _testDeviceID = testDeviceID;
}

RCT_EXPORT_METHOD(requestAd)
{
  if ([_interstitial hasBeenUsed] || _interstitial == nil) {
    _interstitial = [[GADInterstitial alloc] initWithAdUnitID:_adUnitID];
    _interstitial.delegate = self;

    GADRequest *request = [GADRequest request];
    if(_testDeviceID) {
      if([_testDeviceID isEqualToString:@"EMULATOR"]) {
        request.testDevices = @[kGADSimulatorID];
      } else {
        request.testDevices = @[_testDeviceID];
      }
    }
    [_interstitial loadRequest:request];
  }
}

RCT_EXPORT_METHOD(showAd)
{
  if ([_interstitial isReady]) {
    [_interstitial presentFromRootViewController:[UIApplication sharedApplication].delegate.window.rootViewController];
  }
}

RCT_EXPORT_METHOD(isReady:(RCTResponseSenderBlock)callback)
{
  callback(@[[NSNumber numberWithBool:[_interstitial isReady]]]);
}


#pragma mark delegate events

- (void)interstitialDidReceiveAd:(GADInterstitial *)ad {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"interstitialDidLoad" body:nil];
}

- (void)interstitial:(GADInterstitial *)interstitial
didFailToReceiveAdWithError:(GADRequestError *)error {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"interstitialDidFailToLoad" body:@{@"name": [error description]}];
}

- (void)interstitialWillPresentScreen:(GADInterstitial *)ad {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"interstitialDidOpen" body:nil];
}

- (void)interstitialDidDismissScreen:(GADInterstitial *)ad {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"interstitialDidClose" body:nil];
}

- (void)interstitialWillLeaveApplication:(GADInterstitial *)ad {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"interstitialWillLeaveApplication" body:nil];
}

@end
