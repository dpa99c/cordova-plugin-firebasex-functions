package org.anthropic.cordova.firebase;

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

public class FirebasexFunctionsPlugin extends CordovaPlugin {

    private static final String TAG = "FirebasexFunctions";

    private FirebaseFunctions functions;
    private Gson gson;

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "pluginInitialize");
        functions = FirebaseFunctions.getInstance();
        gson = new Gson();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("functionsHttpsCallable".equals(action)) {
            this.functionsHttpsCallable(args, callbackContext);
            return true;
        }
        return false;
    }

    private void functionsHttpsCallable(JSONArray args, CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    String name = args.getString(0);
                    functions.getHttpsCallable(name)
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

    private JSONObject mapToJsonObject(Map<String, Object> map) throws JSONException {
        String jsonString = gson.toJson(map);
        return new JSONObject(jsonString);
    }

    private JSONArray objectToJsonArray(Object object) throws JSONException {
        String jsonString = gson.toJson(object);
        return new JSONArray(jsonString);
    }
}
