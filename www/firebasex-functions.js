/**
 * @fileoverview Cordova JavaScript interface for the FirebaseX Cloud Functions plugin.
 *
 * Provides a method to invoke HTTPS-callable Cloud Functions from the client.
 *
 * @module firebasex-functions
 * @see https://firebase.google.com/docs/functions/callable
 */

var exec = require("cordova/exec");

/** @private Cordova service name registered in plugin.xml. */
var SERVICE = "FirebasexFunctionsPlugin";

/**
 * Invokes an HTTPS-callable Cloud Function by name.
 *
 * The function is called with the supplied arguments, which are serialised
 * and sent to the server. The server function's return value is passed to
 * the success callback.
 *
 * @param {string} name - The name of the Cloud Function to call.
 * @param {*} args - Arguments to pass to the function (any JSON-serialisable value).
 * @param {function} success - Called with the function's return value on success.
 *   The value type depends on what the Cloud Function returns (object, array, string, number).
 * @param {function} error - Called with an error message or error details object on failure.
 *   If the Cloud Function threw a {@link https://firebase.google.com/docs/reference/functions/providers_https_#httpserror HttpsError}
 *   with details, those details are returned.
 * @param {object} [options] - Optional call options.
 * @param {string} [options.region] - Region the function is deployed to (e.g. "europe-west1").
 *   Overrides the `FIREBASE_FUNCTIONS_REGION` plugin variable for this call.
 *   If neither is set, the Firebase SDK default region (us-central1) is used.
 */
exports.functionsHttpsCallable = function (name, args, success, error, options) {
    if (typeof name !== "string") return error("'name' must be a string specifying the Cloud Function name");
    var region = null;
    if (options !== undefined && options !== null) {
        if (typeof options !== "object") return error("'options' must be an object");
        if (options.region !== undefined && options.region !== null) {
            if (typeof options.region !== "string" || options.region.trim() === "") return error("'options.region' must be a non-empty string");
            region = options.region.trim();
        }
    }
    exec(success, error, SERVICE, "functionsHttpsCallable", [name, args, region]);
};
