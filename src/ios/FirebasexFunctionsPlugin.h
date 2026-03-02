/**
 * @file FirebasexFunctionsPlugin.h
 * @brief Cordova plugin for Firebase Cloud Functions on iOS.
 *
 * Provides a bridge to invoke HTTPS-callable Cloud Functions from the Cordova WebView.
 */

#import <Cordova/CDVPlugin.h>

/**
 * Cordova plugin class for Firebase Cloud Functions.
 *
 * Exposes a single action (@c functionsHttpsCallable) that invokes an
 * HTTPS-callable Cloud Function by name and returns its result to JavaScript.
 */
@interface FirebasexFunctionsPlugin : CDVPlugin

/**
 * Invokes an HTTPS-callable Cloud Function.
 *
 * @param command Cordova command with arguments:
 *   - args[0] (NSString) - the Cloud Function name
 *   - args[1] (id)       - arguments to pass to the function
 */
- (void)functionsHttpsCallable:(CDVInvokedUrlCommand *)command;

@end
