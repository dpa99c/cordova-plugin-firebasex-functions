package org.apache.cordova.firebasex;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Cordova plugin for Firebase Cloud Functions on Android.
 *
 * <p>Provides a bridge to invoke HTTPS-callable Cloud Functions from the Cordova
 * WebView. Supports returning data as JSON objects, arrays, integers, strings,
 * or byte arrays. Errors from Cloud Functions (including structured error details)
 * are propagated to the JavaScript error callback.
 *
 * @see <a href="https://firebase.google.com/docs/functions/callable">Cloud Functions - Call functions from your app</a>
 */
public class FirebasexFunctionsPlugin extends CordovaPlugin {

    /** Log tag for all messages from this plugin. */
    private static final String TAG = "FirebasexFunctions";

    /** config.xml preference set from the {@code FIREBASE_FUNCTIONS_REGION} plugin variable. */
    private static final String REGION_PREFERENCE = "FirebasexFunctionsRegion";

    /** Firebase Cloud Functions client instance for the default region. */
    private FirebaseFunctions functions;

    /** Gson instance for converting Maps/Lists to JSON. */
    private Gson gson;

    /**
     * Initialises the plugin by obtaining the Firebase Functions instance for the
     * region set by the {@code FIREBASE_FUNCTIONS_REGION} plugin variable (or the
     * SDK default region if not set) and creating a Gson serialiser.
     */
    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "pluginInitialize");
        String region = preferences.getString(REGION_PREFERENCE, "").trim();
        functions = region.isEmpty() ? FirebaseFunctions.getInstance() : FirebaseFunctions.getInstance(region);
        gson = new Gson();
    }

    /**
     * Returns the Functions instance to use for a call: the given region if any,
     * otherwise the default instance.
     *
     * @param region the region passed from JavaScript, or {@code null}
     * @return the Firebase Functions instance
     */
    private FirebaseFunctions getFunctions(String region) {
        if (region == null || region.trim().isEmpty()) {
            return functions;
        }
        return FirebaseFunctions.getInstance(region.trim());
    }

    /**
     * Dispatches Cordova actions to plugin methods.
     *
     * <p>Supported actions:
     * <ul>
     *   <li>{@code "functionsHttpsCallable"} - invoke an HTTPS-callable Cloud Function</li>
     * </ul>
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("functionsHttpsCallable".equals(action)) {
            this.functionsHttpsCallable(args, callbackContext);
            return true;
        }
        return false;
    }

    /**
     * Calls an HTTPS-callable Cloud Function.
     *
     * <p>Arguments:
     * <ul>
     *   <li>args[0] (String) - the Cloud Function name</li>
     *   <li>args[1] (any)    - the arguments to pass to the function</li>
     *   <li>args[2] (String) - optional region overriding the default (may be null)</li>
     * </ul>
     *
     * <p>On success, the function's return data is sent to the callback as the
     * appropriate type (JSONObject, JSONArray, int, String, or byte[]).
     * On failure, if the error is a {@link FirebaseFunctionsException} with details,
     * those details are returned; otherwise the exception message is returned.
     *
     * @param args            the Cordova arguments array
     * @param callbackContext the callback for success/error results
     * @throws JSONException if argument parsing fails
     */
    private void functionsHttpsCallable(JSONArray args, CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    String name = args.getString(0);
                    String region = args.length() > 2 && !args.isNull(2) ? args.getString(2) : null;
                    getFunctions(region).getHttpsCallable(name)
                            .call(args.get(1))
                            .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                                @Override
                                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                    try {
                                        if (httpsCallableResult.getData() instanceof Map) {
                                            callbackContext.success(mapToJsonObject((Map<String, Object>) httpsCallableResult.getData()));
                                        } else if (httpsCallableResult.getData() instanceof ArrayList) {
                                            callbackContext.success(objectToJsonArray(httpsCallableResult.getData()));
                                        } else if (httpsCallableResult.getData() instanceof Integer) {
                                            callbackContext.success((int) httpsCallableResult.getData());
                                        } else if (httpsCallableResult.getData() instanceof String) {
                                            callbackContext.success((String) httpsCallableResult.getData());
                                        } else {
                                            callbackContext.success((byte[]) httpsCallableResult.getData());
                                        }
                                    } catch (Exception e) {
                                        FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (e instanceof FirebaseFunctionsException) {
                                        this.onFailure((FirebaseFunctionsException) e);
                                        return;
                                    }
                                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                                }

                                void onFailure(@NonNull FirebaseFunctionsException e) {
                                    if (e.getDetails() == null) {
                                        FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                                        return;
                                    }
                                    if (e.getDetails() instanceof String) {
                                        callbackContext.error(e.getDetails().toString());
                                    } else {
                                        try {
                                            callbackContext.error(mapToJsonObject((Map<String, Object>) e.getDetails()));
                                        } catch (JSONException ex) {
                                            FirebasexCorePlugin.handleExceptionWithContext(ex, callbackContext);
                                        }
                                    }
                                }
                            });
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    /**
     * Converts a Java Map to a {@link JSONObject} via Gson serialisation.
     *
     * @param map the map to convert
     * @return the equivalent JSONObject
     * @throws JSONException if the JSON string is malformed
     */
    private JSONObject mapToJsonObject(Map<String, Object> map) throws JSONException {
        String jsonString = gson.toJson(map);
        return new JSONObject(jsonString);
    }

    /**
     * Converts a Java object (typically an ArrayList) to a {@link JSONArray} via Gson serialisation.
     *
     * @param object the object to convert
     * @return the equivalent JSONArray
     * @throws JSONException if the JSON string is malformed
     */
    private JSONArray objectToJsonArray(Object object) throws JSONException {
        String jsonString = gson.toJson(object);
        return new JSONArray(jsonString);
    }
}
