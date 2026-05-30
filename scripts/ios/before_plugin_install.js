var fs = require("fs");
var path = require("path");

var packageSwiftTemplate = [
    "// swift-tools-version:5.9",
    "",
    "import PackageDescription",
    "",
    "let firebaseSDKVersion: Version = \"12.9.0\"",
    "",
    "let package = Package(",
    "    name: \"cordova-plugin-firebasex-functions\"",
    "    platforms: [.iOS(.v13)],",
    "    products: [",
    "        .library(",
    "            name: \"cordova-plugin-firebasex-functions\"",
    "            targets: [\"cordova-plugin-firebasex-functions\"]",
    "        )",
    "    ],",
    "    dependencies: [",
    "        .package(url: \"https://github.com/apache/cordova-ios.git\", branch: \"master\"),",
    "        .package(url: \"https://github.com/firebase/firebase-ios-sdk.git\", exact: firebaseSDKVersion)",
    "    ],",
    "    targets: [",
    "        .target(",
    "            name: \"cordova-plugin-firebasex-functions\"",
    "            dependencies: [",
    "                .product(name: \"Cordova\", package: \"cordova-ios\"),",
    "                .product(name: \"FirebaseFunctions\", package: \"firebase-ios-sdk\")",
    "            ],",
    "            path: \"src/ios\",",
    "            publicHeadersPath: \".\"",
    "        )",
    "    ]",
    ")",
    ""
].join("\n");

module.exports = function(context) {
    var pluginDir = path.join(context.opts.projectRoot, "plugins", "cordova-plugin-firebasex-functions");
    var packageSwiftPath = path.join(pluginDir, "Package.swift");

    if (fs.existsSync(packageSwiftPath)) {
        return;
    }

    fs.writeFileSync(packageSwiftPath, packageSwiftTemplate, "utf8");
    console.log("[FirebasexFunctions] Restored missing Package.swift in installed plugin copy");
};