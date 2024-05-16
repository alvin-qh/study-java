#include "jni_demo.h"
#include <iostream>

JNIEXPORT
jstring JNICALL Java_alvin_study_se_runtime_JNIDemo_itoa(JNIEnv *env, jobject jThis, jint jInt)
{
    // 将整数转为字符串
    std::string s = std::to_string(jInt);

    // 将字符串转为 Java String
    return env->NewStringUTF(s.c_str());
}
