var exec = require("cordova/exec");
var SERVICE = "FirebasexFunctionsPlugin";

exports.functionsHttpsCallable = function (name, args, success, error) {
    if (typeof name !== "string") return error("'name' must be a string specifying the Cloud Function name");
    exec(success, error, SERVICE, "functionsHttpsCallable", [name, args]);
};
