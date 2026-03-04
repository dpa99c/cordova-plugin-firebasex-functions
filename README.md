# cordova-plugin-firebasex-functions [![Latest Stable Version](https://img.shields.io/npm/v/cordova-plugin-firebasex-functions.svg)](https://www.npmjs.com/package/cordova-plugin-firebasex-functions)

Firebase Cloud Functions module for the [modular FirebaseX Cordova plugin suite](https://github.com/dpa99c/cordova-plugin-firebasex#modular-plugins).

This plugin wraps the [Firebase Functions SDK](https://firebase.google.com/docs/reference/js/firebase.functions) and provides methods to call Firebase Cloud Functions from your Cordova app.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Installation](#installation)
- [API](#api)
  - [functionsHttpsCallable](#functionshttpscallable)
- [Reporting issues](#reporting-issues)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Installation

Install the plugin by adding it to your project's config.xml:

    cordova plugin add cordova-plugin-firebasex-functions

or by running:

    cordova plugin add cordova-plugin-firebasex-functions

**This module depends on `cordova-plugin-firebasex-core` which will be installed automatically as a dependency.**

## Plugin variables

| Variable | Default | Description |
|---|---|---|
| `ANDROID_FIREBASE_FUNCTIONS_VERSION` | `22.1.0` | Android Firebase Functions SDK version. |
| `ANDROID_GSON_VERSION` | `2.13.2` | Google Gson library version (Functions dependency). |
| `IOS_FIREBASE_SDK_VERSION` | `12.9.0` | iOS Firebase SDK version (for functions pod). |

# API

The following methods are available via the `FirebasexFunctions` global object.

## functionsHttpsCallable

Call a Firebase [HTTPS Callable function](https://firebase.google.com/docs/functions/callable).

**Parameters**:

-   {string} name - the name of the function
-   {object} args - arguments to send to the function
-   {function} success - callback function to call on successfully completing the function call.
    Will be passed an {object/array/string} containing the data returned by the function.
-   {function} error - callback function which will be passed a {string/object} error message as an argument.

```javascript
var functionName = "myBackendFunction";
var args = {
    arg1: "First argument",
    arg2: "second argument",
};
FirebasexFunctions.functionsHttpsCallable(
    functionName,
    args,
    function (result) {
        console.log("Successfully called function: " + JSON.stringify(result));
    },
    function (error) {
        console.error("Error calling function: " + JSON.stringify(error));
    }
);
```

# Reporting issues

Before reporting an issue with this plugin, please do the following:
- Check the existing [issues](https://github.com/dpa99c/cordova-plugin-firebasex-functions/issues) to see if the issue has already been reported.
- Check the [issue template](https://github.com/dpa99c/cordova-plugin-firebasex-functions/issues/new/choose) and provide all requested information.
- The more information and context you provide, the easier it is for the maintainers to understand the issue and provide a resolution.
