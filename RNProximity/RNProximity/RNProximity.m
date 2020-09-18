//
//  RNProximity.m
//
//  Created by Liviu Padurariu
//

#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>
#import <AVFoundation/AVFoundation.h>
#import "RNProximity.h"

NSString * ProximityStateDidChange = @"ProximityStateDidChange";

@implementation RNProximity {
    bool hasListeners;
}

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

- (instancetype)init {
    self = [super init];
    if (self) {
        [[UIDevice currentDevice] setProximityMonitoringEnabled:NO];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(sensorStateChange:) name:@"UIDeviceProximityStateDidChangeNotification" object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSArray<NSString *> *)supportedEvents {
    return @[ProximityStateDidChange];
}

// Will be called when this module's first listener is added.
-(void)startObserving {
    hasListeners = YES;
    // Set up any upstream listeners or background tasks as necessary
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
    hasListeners = NO;
    // Remove upstream listeners, stop unnecessary background tasks
}

- (void)sensorStateChange:(NSNotificationCenter *)notification {
    BOOL proximityState = [[UIDevice currentDevice] proximityState];
    if (hasListeners) { // Only send events if anyone is listening
        [self audioOutputHandler:proximityState];
        [self sendEventWithName:ProximityStateDidChange body:@{@"proximity": @(proximityState)}];
    }
}

- (void)audioOutputHandler:(BOOL)isCloseToEar {
    NSError *error = nil;
    if (isCloseToEar) {
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord error:&error];
        [[AVAudioSession sharedInstance] setActive:YES error:&error];
        if (error){
            NSLog(@"%@", error);
        }
    } else {
        NSError *error = nil;
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:&error];
        [[AVAudioSession sharedInstance] setActive:YES error:&error];
        if (error){
            NSLog(@"%@", error);
        }
    }
}

RCT_EXPORT_METHOD(proximityEnabled:(BOOL)enabled) {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIDevice currentDevice] setProximityMonitoringEnabled:enabled];
    });
}

+ (BOOL) requiresMainQueueSetup {
    return YES;
}

@end
