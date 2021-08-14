//
// Created by scacc on 05/08/2021.
//

#include "TissueViewModel.h"
#include <cstdio>
#include <android/log.h>
static int value = 0;

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_ar_com_westsoft_joyschool_electrictissuesimulator_TissueViewModel_calcAll(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "CallCol!\n");

    jclass class_TissueViewModel = env->GetObjectClass(thiz);
    jfieldID tissueId = env->GetFieldID(class_TissueViewModel, "tissue",
                                        "[[Lar/com/westsoft/joyschool/electrictissuesimulator/Cell;");
    jobjectArray tissueArray = static_cast<jobjectArray>(env->GetObjectField(thiz, tissueId));

    int index = 1;
    jobjectArray rowArray = static_cast<jobjectArray>(env->GetObjectArrayElement(tissueArray,
                                                                                 index));
    __android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "%d\n", value);
    value ++;
    return tissueArray;

}
