interface FirebasexFunctionsCallOptions {
    /**
     * Region the function is deployed to, e.g. "europe-west1".
     * Overrides the FIREBASE_FUNCTIONS_REGION plugin variable for this call.
     */
    region?: string;
}

interface FirebasexFunctions {
    functionsHttpsCallable(
        functionName: string,
        args: object,
        success: (result: object) => void,
        error: (err: string) => void,
        options?: FirebasexFunctionsCallOptions
    ): void;
}
