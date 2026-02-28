#import "FirebasexFunctionsPlugin.h"
#import "FirebasexCorePlugin.h"
@import FirebaseFunctions;

@implementation FirebasexFunctionsPlugin

- (void)pluginInitialize {
    NSLog(@"FirebasexFunctionsPlugin pluginInitialize");
}

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
