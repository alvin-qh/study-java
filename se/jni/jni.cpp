#include "jni.h"
#include <iostream>

JNIEXPORT void JNICALL Java_alvin_study_misc_JNIPrint_print(JNIEnv *env, jobject jThis, jstring jStr) {
    const char *pStr = env->GetStringChars(jStr, 0);

    env->ReleaseStringChars(jStr, pStr);
}
