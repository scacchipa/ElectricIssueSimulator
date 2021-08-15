//
// Created by scacc on 05/08/2021.
//

#include "TissueViewModel.h"
#include <android/log.h>
#include <android/bitmap.h>
static int value = 0;

extern "C"
JNIEXPORT void JNICALL
Java_ar_com_westsoft_joyschool_electrictissuesimulator_TissueViewModel_calcAll(JNIEnv *env, jobject thiz) {

//    __android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "%d\n", value);
    value++;

}
