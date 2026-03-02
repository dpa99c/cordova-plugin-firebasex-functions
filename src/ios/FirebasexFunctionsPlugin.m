/**
 * @file FirebasexFunctionsPlugin.m
 * @brief iOS implementation of the Firebase Cloud Functions Cordova plugin.
 */

#import "FirebasexFunctionsPlugin.h"
#import "FirebasexCorePlugin.h"
@import FirebaseFunctions;

@implementation FirebasexFunctionsPlugin

/**
 * Called by Cordova when the plugin is first loaded. Logs initialisation.
 */
- (void)pluginInitialize {
    NSLog(@"FirebasexFunctionsPlugin pluginInitialize");
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
 */
- (void)functionsHttpsCallable:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            NSString *name = [command.arguments objectAtIndex:0];
            id arguments = [command.arguments objectAtIndex:1];
            [[[FIRFunctions functions] HTTPSCallableWithName:name]
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
