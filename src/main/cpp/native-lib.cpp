#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_mobileappsdevelopment_AccountDisplayActivity_baseUrlFromJNI(JNIEnv *env, jobject) {
    std::string mUrl = "aHR0cHM6Ly82MDA3ZjFhNDMwOWY4YjAwMTdlZTUwMjIubW9ja2FwaS5pby9hcGkvbTEvYWNjb3VudHM=";
    return env->NewStringUTF(mUrl.c_str());
}