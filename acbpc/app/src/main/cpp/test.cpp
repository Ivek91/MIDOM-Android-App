//
// Created by Ivek2 on 9.3.2017..
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_hr_fer_zari_midom_activities_NDKtest_stringFromJNI
        (JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}