//
// Created by scacc on 05/08/2021.
//

#include "TissueViewModel.h"
#include <android/log.h>
#include <android/bitmap.h>
#include <time.h>
#include "tissue.h"
clock_t accum_calcAll_clock = 0;
int calcAll_times = 0;
clock_t accum_paint_clock = 0;
int paint_times = 0;

static Tissue* tissue = nullptr;
extern void(*calcChargeFunctions[4])(Cell*);
extern Channel* channel[4];

int xCellSize = 10;
int yCellSize = 10;

extern "C"
JNIEXPORT void JNICALL
Java_ar_com_westsoft_hearttissue_dominio_Tissue_calcAll(JNIEnv *env, jobject thiz) {

    clock_t begin = clock();

    Tissue_calcAll(tissue);
    clock_t end = clock();

    clock_t spent_time = end - begin;
    accum_calcAll_clock += spent_time;
    ++calcAll_times;

    __android_log_print(ANDROID_LOG_VERBOSE, "CalcAll","Spent time: %ld; Calc Mean Time: %lf",
                        spent_time, (double)accum_calcAll_clock / (double)calcAll_times / CLOCKS_PER_SEC * 1000);
}

extern "C"
JNIEXPORT void JNICALL
Java_ar_com_westsoft_hearttissue_dominio_Tissue_setUp(JNIEnv *env, jobject thiz,
                                                      jint cell_col_count, jint cell_row_count)
{
    if (tissue != nullptr) Tissue_destroy(tissue);
    tissue = Tissue_create(cell_col_count, cell_row_count);
}

extern "C"
JNIEXPORT void JNICALL
Java_ar_com_westsoft_hearttissue_MainActivity_00024Companion_nativeInit(JNIEnv *env, jobject thiz) {
    Pair *pairs[4];
    pairs[MYOCELL] = (Pair[]) {
                    { 0, -75.0 }, { 5, -70.0 },
                    { 30, 25.0 }, { 5, 10.0 }, { 5, 7.0 }, { 400, 5.0 },
                    { 10, 4.0 }, { 10, 3.0 }, { 10, 2.0 }, { 10, 0.0 },
                    { 10, -2.0 }, { 10, -4.0 }, { 10, -7.0 }, { 10, -11.0 },
                    { 10, -16.0 }, { 10, -22.0 }, { 10, -29.0 }, { 10, -37.0 },
                    { 10, -45.0 }, { 10, -54.0 }, { 10, -62.0 }, { 10, -67.0 },
                    { 10, -71.0 }, { 10, -74.0 }, { 10, -75.0 }, { 10, -75.0 },
                    { 10, -75.0 }, { 10, -75.0 }, { 10, -75.0 }, { 400, -75.0 },
                    { 0, 0 } };
    pairs[AUTOCELL] = (Pair[]) {
                    { 0, -55}, {15, -53}, {15, -50}, {15, -43.0},
                    { 15, -35}, {15, -27}, {15, -17}, {15, -7.0},
                    { 15, -1}, {15, 5}, {15, 7}, {15, 8.0},
                    { 15, 8}, {15, 8}, {15, 7}, {15, 6.0},
                    { 15, 4}, {15, 1}, {15, -2}, {15, -6.0},
                    { 15, -10}, {15, -14}, {15, -19}, {15, -24.0},
                    { 15, -29}, {15, -34}, {15, -38}, {15, -42.0},
                    { 15, -46}, {15, -50}, {15, -54}, {15, -57.0},
                    { 15, -60}, {15, -62}, {15, -64},
                    { 15, -65}, {15, -65}, {10, -65}, {3000, -12.0},
                    { 0, 0.0} };
    pairs[FASTCELL] = (Pair[]) {
                    {0, -75.0}, {10, 25.0}, {5, 10.0}, {5, 7.0}, {400, 5.0},
                    {10, 4.0}, {10, 3.0}, {10, 2.0}, {10, 0.0},
                    {10, -2.0}, {10, -4.0}, {10, -7.0}, {10, -11.0},
                    {10, -16.0}, {10, -22.0}, {10, -29.0}, {10, -37.0},
                    {10, -45.0}, {10, -54.0}, {10, -62.0}, {10, -67.0},
                    {10, -71.0}, {10, -74.0}, {10, -75.0}, {10, -75.0},
                    {10, -75.0}, {10, -75.0}, {10, -75.0}, {400, -75.0},
                    {0, 0.0} },
    pairs[DEADCELL] = (Pair[]) {{0, 0.0}};

    for (Pair *pPair = pairs[MYOCELL]; pPair->first != 0 || pPair->second != 0.0; ++pPair)
    {
        pPair->second = (pPair->second + 20.0) * 1.3;
    }
    for (Pair *pPair = pairs[AUTOCELL]; pPair->first != 0 || pPair->second != 0.0; ++pPair)
    {
        pPair->second = (pPair->second + 10.0) * 1.3;
    }
    for (Pair *pPair = pairs[FASTCELL]; pPair->first != 0 || pPair->second != 0.0; ++pPair)
    {
        pPair->second = (pPair->second + 20.0) * 1.3;
    }

    channel[MYOCELL] = Channel_create(-55, 0, pairs[MYOCELL],
                                      Cell_loadColorList(3, 0xFF032B43, 0xFFF9C80E, 0xFFEA3546));
    channel[AUTOCELL] = Channel_create(-45, 0, pairs[AUTOCELL],
                                       Cell_loadColorList(3, 0xFFFEB38B, 0xFFF9C80E, 0xFFEA3546));
    channel[FASTCELL] = Channel_create(-55, 0, pairs[FASTCELL],
                                       Cell_loadColorList(3, 0xFF032B43, 0xFFF9C80E, 0xFFEA3546));
    channel[DEADCELL] = Channel_create(-55, 0, pairs[DEADCELL],
                                       Cell_loadColorList(3, 0xFF000000));

    calcChargeFunctions[MYOCELL] = MyoCell_calculateCharge;
    calcChargeFunctions[AUTOCELL] = AutoCell_calculateCharge;
    calcChargeFunctions[FASTCELL] = FastCell_calculateCharge;
    calcChargeFunctions[DEADCELL] = DeadCell_calculateCharge;
}
extern "C"
JNIEXPORT void JNICALL
Java_ar_com_westsoft_hearttissue_dominio_Tissue_setCell(JNIEnv *env, jobject thiz, jobject jCell,
                                                jint x, jint y) {
    jclass jCellClass = env->GetObjectClass(jCell);
    jfieldID jCellType = env->GetFieldID(jCellClass, "cellType", "Lar/com/westsoft/hearttissue/CellType;");
    jfieldID jChannelStateId = env->GetFieldID(jCellClass, "state", "Lar/com/westsoft/hearttissue/ChannelState;");
    jfieldID jCellVmId = env->GetFieldID(jCellClass, "vm", "D");
    jfieldID jCargeId = env->GetFieldID(jCellClass, "charge", "D");
    jfieldID jStepId = env->GetFieldID(jCellClass, "step", "I");

    jobject jCellTypeObj = env->GetObjectField(jCell, jCellType);
    jclass jCellTypeClass = env->FindClass("ar/com/westsoft/hearttissue/CellType");
    jmethodID jCellTypeMtdId = env->GetMethodID(jCellTypeClass, "getValue", "()I");
    jint jCellTypeInt = env->CallIntMethod(jCellTypeObj, jCellTypeMtdId);

    jobject jChannelStateObj = env->GetObjectField(jCell, jChannelStateId);
    jclass jChannelStateClass = env->FindClass("ar/com/westsoft/hearttissue/ChannelState");
    jmethodID jChannelStateMtdId = env->GetMethodID(jChannelStateClass, "getValue", "()I");
    jint jChannelStateInt = env->CallIntMethod(jChannelStateObj, jChannelStateMtdId);

    jdouble jCellVm = env->GetDoubleField(jCell, jCellVmId);
    jdouble jCellCharge = env->GetDoubleField(jCell, jCargeId);
    jint jCellStepInt = env->GetIntField(jCell, jStepId);

    Cell* cell = GETPCELL(tissue,x,y);
    cell->tissue = tissue;
    cell->colPos = x;
    cell->rowPos = y;
    switch (jCellTypeInt) {
        case MYOCELL: cell->channel = channel[MYOCELL]; break;
        case AUTOCELL: cell->channel = channel[AUTOCELL]; break;
        case FASTCELL: cell->channel = channel[FASTCELL]; break;
        case DEADCELL: cell->channel = channel[DEADCELL];
    }
    switch (jChannelStateInt) {
        case RESTING: cell->channelState = RESTING; break;
        case OPEN: cell->channelState = OPEN; break;
        case INACTIVE: cell->channelState = INACTIVE;
    }
    cell->vm = jCellVm;
    cell->charge = jCellCharge;
    cell->step = jCellStepInt;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_ar_com_westsoft_hearttissue_dominio_Tissue_getCell(JNIEnv *env, jobject thiz, jint x,
                                                jint y) {
    Cell* pCell = (Cell*)(GETPCELL(tissue, x, y));

    jclass jCellClass = env->FindClass("ar/com/westsoft/hearttissue/Cell");
    jmethodID jConstructorID = env->GetMethodID(jCellClass, "<init>", "(Lar/com/westsoft/hearttissue/CellType;Lar/com/westsoft/hearttissue/ChannelState;DDI)V");

    jclass jCellTypeClass = env->FindClass("ar/com/westsoft/hearttissue/CellType");

    jfieldID jCellTypeClassId;
    switch (pCell->cellType) {
        case MYOCELL:
            jCellTypeClassId = env->GetStaticFieldID(jCellTypeClass, "MYOCELL", "Lar/com/westsoft/hearttissue/CellType;");
            break;
        case AUTOCELL:
            jCellTypeClassId = env->GetStaticFieldID(jCellTypeClass, "MYOCELL", "Lar/com/westsoft/hearttissue/CellType;");
            break;
        case FASTCELL:
            jCellTypeClassId = env->GetStaticFieldID(jCellTypeClass, "MYOCELL", "Lar/com/westsoft/hearttissue/CellType;");
            break;
        case DEADCELL:
            jCellTypeClassId = env->GetStaticFieldID(jCellTypeClass, "MYOCELL", "Lar/com/westsoft/hearttissue/CellType;");
            break;
    }
    jobject jCellType = env->GetStaticObjectField(jCellTypeClass, jCellTypeClassId);

    jclass jChannelStateClass = env->FindClass("ar/com/westsoft/hearttissue/ChannelState");
    jfieldID jChannelStateClassId;
    switch (pCell->channelState) {
        case RESTING:
            jChannelStateClassId = env->GetStaticFieldID(jChannelStateClass,"RESTING", "Lar/com/westsoft/hearttissue/ChannelState;");
            break;
        case OPEN:
            jChannelStateClassId = env->GetStaticFieldID(jChannelStateClass,"OPEN", "Lar/com/westsoft/hearttissue/ChannelState;");
            break;
        case INACTIVE:
            jChannelStateClassId = env->GetStaticFieldID(jChannelStateClass,"INACTIVE", "Lar/com/westsoft/hearttissue/ChannelState;");
            break;
    }
    jobject jChannelState = env->GetStaticObjectField(jChannelStateClass, jChannelStateClassId);

    jdouble jVmDouble = pCell->vm;
    jdouble jChargeDouble = pCell->charge;
    jint jStepInt = pCell->step;

    return env->NewObject(jCellClass, jConstructorID, jCellType, jChannelState,
                   jVmDouble, jChargeDouble, jStepInt);
}

extern "C"
JNIEXPORT void JNICALL
Java_ar_com_westsoft_hearttissue_TissueView_printBitmap(JNIEnv *env,
                    jobject thiz,jobject jBitmap, jintArray colors) {
    clock_t begin = clock();

    AndroidBitmapInfo androidBitmapInfo ;
    void* pixels;
    AndroidBitmap_getInfo(env, jBitmap, &androidBitmapInfo);
    AndroidBitmap_lockPixels(env, jBitmap, &pixels);

    jintArray pColor = colors;
    for (uint32_t x = 0; x < tissue->xSize; ++x)
        for (uint32_t y = 0; y < tissue-> ySize; ++y) {
            Bitmap_drawPaintedBox(pixels, &androidBitmapInfo,
                                  x * xCellSize + 2, y * yCellSize + 2,
                                  (x + 1) * xCellSize - 2, (y + 1) * yCellSize - 2,
                                  *(uint32_t *)pColor);
        }
    AndroidBitmap_unlockPixels(env, jBitmap);
    clock_t end = clock();

    clock_t spent_time = end - begin;
    accum_paint_clock += spent_time;
    ++paint_times;

    __android_log_print(ANDROID_LOG_VERBOSE, "Paint time","Spent time: %ld; Calc Mean Time: %lf",
                        spent_time, (double)accum_paint_clock / (double)paint_times / CLOCKS_PER_SEC * 1000);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_ar_com_westsoft_hearttissue_dominio_Tissue_getPosCell(JNIEnv *env, jobject thiz,
                                                   jint x_pixels, jint y_pixels) {
    jint xPos = x_pixels / xCellSize;
    jint yPos = y_pixels / yCellSize;

    jclass pointClass = env->FindClass("android/graphics/Point");
    jmethodID initPointId = env->GetMethodID(pointClass, "<init>", "(II)V");

    jobject pointObj = env->NewObject(pointClass, initPointId, xPos, yPos);

    return pointObj;
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_ar_com_westsoft_hearttissue_dominio_Tissue_getColorArray(JNIEnv *env, jobject thiz) {
    int size = tissue->xSize * tissue->ySize;
    jintArray result = env->NewIntArray(size);
    Cell* pCell = tissue->cells;
    jint colors[tissue->xSize * tissue->ySize];
    jint* pColor = colors;
    for (int x = 0; x < size; ++x) {
            *pColor = pCell->channel->cell_colors[pCell->channelState];
            ++pColor;
        }

    env->SetIntArrayRegion(result, 0, size, colors);
    return result;
}
