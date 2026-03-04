interface FirebasexFunctions {
    functionsHttpsCallable(
        functionName: string,
        args: object,
        success: (result: object) => void,
        error: (err: string) => void
    ): void;
}
