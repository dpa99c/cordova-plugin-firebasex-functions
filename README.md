# cordova-plugin-firebasex-functions

Firebase Cloud Functions module for `cordova-plugin-firebasex`.

## Installation

```bash
cordova plugin add cordova-plugin-firebasex-functions
```

This plugin depends on `cordova-plugin-firebasex-core` which will be installed automatically.

## API

### functionsHttpsCallable(name, args, success, error)
Call a Firebase Cloud Function by name.

```javascript
FirebasexFunctionsPlugin.functionsHttpsCallable(
    "myFunction",
    { key: "value" },
    function(result) { console.log("Result:", result); },
    function(error) { console.error(error); }
);
```

| Parameter | Type | Description |
|---|---|---|
| `name` | `string` | Name of the Cloud Function to call |
| `args` | `object` | Arguments to pass to the function |
| `success` | `function` | Success callback with function result |
| `error` | `function` | Error callback with error message |
