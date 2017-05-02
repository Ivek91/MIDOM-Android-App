
#include <jni.h>
#include <stdio.h>


extern "C"
JNIEXPORT jlong JNICALL
Java_hr_fer_zari_midom_utils_decode_CBPredictor_calcDistance
        (JNIEnv *env, jobject jobj, jint originVector, jint currVector){
    jlong dist;
    dist = (originVector - currVector) * (originVector - currVector);
    return dist;
}
