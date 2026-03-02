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
 */
exports.functionsHttpsCallable = function (name, args, success, error) {
    if (typeof name !== "string") return error("'name' must be a string specifying the Cloud Function name");
    exec(success, error, SERVICE, "functionsHttpsCallable", [name, args]);
};
