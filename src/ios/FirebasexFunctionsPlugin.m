/**
 * @file FirebasexFunctionsPlugin.m
 * @brief iOS implementation of the Firebase Cloud Functions Cordova plugin.
 */

#import "FirebasexFunctionsPlugin.h"
#if __has_include("FirebasexCorePlugin.h")
    // Cordova-ios 7 / CocoaPods: Files are compiled in a flat target structure
    #import "FirebasexCorePlugin.h"
#else
    // Cordova-ios 8+ / SPM: Plugins are isolated Swift Package modules
    @import cordova_plugin_firebasex_core;
#endif
@import FirebaseFunctions;

/** config.xml preference set from the FIREBASE_FUNCTIONS_REGION plugin variable. */
static NSString *const kRegionPreference = @"FirebasexFunctionsRegion";

@implementation FirebasexFunctionsPlugin {
    /** Region from the FIREBASE_FUNCTIONS_REGION plugin variable, or nil for the SDK default. */
    NSString *_defaultRegion;
}

/**
 * Called by Cordova when the plugin is first loaded. Reads the default region
 * from the FIREBASE_FUNCTIONS_REGION plugin variable.
 */
- (void)pluginInitialize {
    NSLog(@"FirebasexFunctionsPlugin pluginInitialize");
    // Cordova lower-cases preference names in commandDelegate.settings.
    id region = [self.commandDelegate.settings objectForKey:[kRegionPreference lowercaseString]];
    _defaultRegion = [self normalizedRegion:region];
}

/**
 * Returns a trimmed, non-empty region string, or nil.
 */
- (NSString *)normalizedRegion:(id)region {
    if (![region isKindOfClass:[NSString class]]) {
        return nil;
    }
    NSString *trimmed = [(NSString *)region stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    return trimmed.length > 0 ? trimmed : nil;
}

/**
 * Returns the Functions instance for the given region, falling back to the
 * plugin variable region and then to the SDK default instance.
 */
- (FIRFunctions *)functionsInstanceForRegion:(NSString *)region {
    NSString *effectiveRegion = region ?: _defaultRegion;
    return effectiveRegion ? [FIRFunctions functionsForRegion:effectiveRegion] : [FIRFunctions functions];
}

/**
 * Invokes an HTTPS-callable Cloud Function.
 *
 * Runs asynchronously in the background. On success, returns the function's
 * result data as a dictionary to the JS callback. On failure, sends an
 * error result with the @c NSError description.
 *
 * @param command Cordova command with arguments:
 *   - args[0] (NSString) - the Cloud Function name
 *   - args[1] (id)       - arguments to pass to the function
 *   - args[2] (NSString) - optional region overriding the default (may be NSNull)
 */
- (void)functionsHttpsCallable:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            NSString *name = [command.arguments objectAtIndex:0];
            id arguments = [command.arguments objectAtIndex:1];
            NSString *region = command.arguments.count > 2 ? [self normalizedRegion:[command.arguments objectAtIndex:2]] : nil;
            [[[self functionsInstanceForRegion:region] HTTPSCallableWithName:name]
                callWithObject:arguments
                    completion:^(FIRHTTPSCallableResult *_Nullable result,
                                 NSError *_Nullable error) {
                        if (error != nil) {
                            [[FirebasexCorePlugin sharedInstance] sendPluginErrorWithError:error command:command];
                        } else {
                            [[FirebasexCorePlugin sharedInstance] sendPluginDictionaryResult:result.data
                                                                                    command:command
                                                                                 callbackId:command.callbackId];
                        }
                    }];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

@end
