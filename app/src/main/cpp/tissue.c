//
// Created by scacc on 12/08/2021.
//

#include "tissue.h"
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <android/bitmap.h>
#include <android/log.h>

Channel* channel[4];
uint32_t* cellColors[4];
void(*calcChargeFunctions[4])(Cell*);

void MyoCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos)
{
    cell->tissue = tissue;
    cell->colPos = xPos;
    cell->rowPos = yPos;
    cell->channelState = RESTING;
    cell->channel = channel[MYOCELL];
}
void AutoCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos)
{
    cell->tissue = tissue;
    cell->colPos = xPos;
    cell->rowPos = yPos;
    cell->channelState = RESTING;
    cell->channel = channel[AUTOCELL];
}
void FastCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos)
{
    cell->tissue = tissue;
    cell->colPos = xPos;
    cell->rowPos = yPos;
    cell->channelState = RESTING;
    cell->channel = channel[FASTCELL];
}
void DeadCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos)
{
    cell->tissue = tissue;
    cell->colPos = xPos;
    cell->rowPos = yPos;
    cell->channelState = RESTING;
    cell->channel = channel[DEADCELL];
}
void Cell_membranePotential(Cell* cell)
{
    cell->vm = (cell->channel->alphaVector)[cell->step];
}
void Cell_channelUpdate(Cell* cell)
{
    if (cell->channelState == RESTING && cell->charge > -45)
    {
        cell->channelState = OPEN;
        cell->step = 0;
    }
    else if (cell->channelState == OPEN && cell->charge > 0)
    {
        cell->channelState = INACTIVE;
    }
    else if (cell->channelState == INACTIVE && cell->charge < -45)
    {
        cell->channelState = RESTING;
    }
    if (cell->step < cell->channel->alphaVectorSize - 1)
        ++cell->step;
}
uint32_t* Cell_createColorList(int num, ...)
{
    uint32_t * colors = (uint32_t *)malloc(sizeof(uint32_t));
    va_list valist;
    va_start(valist, num);
    for (int i = 0; i < num; i++)
    {
        colors[i] = va_arg(valist, uint32_t);
    }
    va_end(valist);
    return colors;
}
Cell* Cell_upperCell(Cell* cell)
{
    if (cell->rowPos > 0)
        return &(cell->tissue->cells[cell->colPos][cell->rowPos - 1]);
    else
        return cell;
}
Cell* Cell_lowerCell(Cell* cell)
{
    if (cell->rowPos < cell->tissue->ySize - 1)
        return &(cell->tissue->cells[cell->colPos][cell->rowPos + 1]);
    else
        return cell;
}
Cell* Cell_leftCell(Cell* cell)
{
    if (cell->colPos > 0)
        return &(cell->tissue->cells[cell->colPos - 1][cell->rowPos]);
    else
        return cell;
}
Cell* Cell_rightCell(Cell* cell)
{
    if (cell->colPos < cell->tissue->xSize - 1)
        return &(cell->tissue->cells[cell->colPos + 1][cell->rowPos]);
    else
        return cell;
}
Cell* Cell_lowerRightCell(Cell* cell)
{
    if (cell->colPos < cell->tissue->xSize - 1 && cell->rowPos < cell->tissue->ySize - 1)
        return &(cell->tissue->cells[cell->colPos + 1][cell->rowPos + 1]);
    else
        return cell;
}
Cell* Cell_upperRightCell(Cell* cell)
{
    if (cell->colPos < cell->tissue->xSize - 1 && cell->rowPos > 0)
        return &(cell->tissue->cells[cell->colPos + 1][cell->rowPos - 1]);
    else
        return cell;
}
Cell* Cell_upperLeftCell(Cell* cell)
{
    if (cell->colPos > 0 && cell->rowPos > 0)
        return &(cell->tissue->cells[cell->colPos - 1][cell->rowPos - 1]);
    else
        return cell;
}
Cell* Cell_lowerLeftCell(Cell* cell)
{
    if (cell->colPos > 0 && cell->rowPos < cell->tissue->ySize - 1)
        return &(cell->tissue->cells[cell->colPos - 1][cell->rowPos + 1]);
    else
        return cell;
}
void MyoCell_calculateCharge(Cell* cell) {
    cell->charge = 0.4 * cell->vm +
                   (Cell_upperCell(cell)->vm + Cell_lowerCell(cell)->vm + Cell_leftCell(cell)->vm +
                    Cell_rightCell(cell)->vm + Cell_lowerRightCell(cell)->vm +
                    Cell_upperRightCell(cell)->vm + Cell_upperLeftCell(cell)->vm +
                    Cell_lowerLeftCell(cell)->vm) * 0.075;
}
void AutoCell_calculateCharge(Cell* cell) {
    cell->charge = 0.6 * cell->vm +
                   (Cell_upperCell(cell)->vm + Cell_lowerCell(cell)->vm + Cell_leftCell(cell)->vm +
                    Cell_rightCell(cell)->vm + Cell_lowerRightCell(cell)->vm +
                    Cell_upperRightCell(cell)->vm + Cell_upperLeftCell(cell)->vm +
                    Cell_lowerLeftCell(cell)->vm) * 0.05;
}
void FastCell_calculateCharge(Cell* cell) {
    cell->charge = 0.4 * cell->vm +
                   (Cell_upperCell(cell)->vm + Cell_lowerCell(cell)->vm + Cell_leftCell(cell)->vm +
                    Cell_rightCell(cell)->vm + Cell_lowerRightCell(cell)->vm +
                    Cell_upperRightCell(cell)->vm + Cell_upperLeftCell(cell)->vm +
                    Cell_lowerLeftCell(cell)->vm) * 0.075;
}
void DeadCell_calculateCharge(Cell* cell) { }

void buildAlphaVector(const Pair* inPairVector, double** outAlphaVector, size_t* outAlphaSize) {
    int lastTime = 0;
    double lastVm = inPairVector[0].second;
    int alphaVectorSize = 100;
    double * alphaVector = (double *)malloc(alphaVectorSize * sizeof(double));
    alphaVector[lastTime] = lastVm; ++lastTime;
    for(const Pair* pInPair = inPairVector; pInPair->first != 0 && pInPair->second != 0.0; ++pInPair) {
        double deltaVolt = (pInPair->second - lastVm) / pInPair->first;
        for (int time = 0; time < pInPair->first; ++time) {
            lastVm += deltaVolt;
            alphaVector[lastTime] = lastVm;
            ++lastTime;
            if (alphaVectorSize < lastTime + time) {
                alphaVectorSize += 100;
                alphaVector = (double *)realloc(alphaVector, alphaVectorSize);
            }
        }
    }
    alphaVector = (double *)realloc(alphaVector, lastTime);
    *outAlphaVector = alphaVector;
}
Tissue *Tissue_create(int xSize, int ySize) {
    Tissue *tissue = (Tissue *) malloc(sizeof(Tissue));
    tissue->xSize = xSize;
    tissue->ySize = ySize;
    tissue->cells = Tissue_createCells(tissue, xSize, ySize);

    return tissue;
}
void Tissue_destroy(Tissue *tissue) {
    Tissue_destroyCells(tissue->cells);
    free(tissue);
}
Cell** Tissue_createCells(Tissue* tissue, int xSize, int ySize)
{
    Cell **cells = (Cell **) malloc(xSize * ySize * sizeof(Cell));

    for (int idx = 0; idx < xSize; ++idx)
        for (int idy = 0; idy < ySize; ++idy)
            MyoCell_create(&cells[idx][idy], tissue, idx, idy);
    return cells;
}
void Tissue_destroyCells(Cell **cells)
{
    free(cells);
}
void Tissue_forAllCells(Tissue* tissue, void(*func)(Cell*)) {
    for (int idx = 0; idx < tissue->xSize; ++idx)
        for (int idy = 0; idy < tissue->ySize; ++idy)
            func(&(tissue->cells[idx][idy]));
}
Channel* Channel_create(double inactGateThreadhold, double actGateThreadhold,
        Pair* coords) {
    Channel *_channel = (Channel *)malloc(sizeof(Channel));
    _channel->inactivationGateThreadhold = inactGateThreadhold;
    _channel->activationGateThreadhold = actGateThreadhold;
    buildAlphaVector(coords, &(_channel->alphaVector), &(_channel->alphaVectorSize));
    return _channel;
}

JNIEXPORT void JNICALL
Java_ar_com_westsoft_hearttissue_TissueView_printBitmap(JNIEnv *env,
                                                                             jobject thiz,jobject jBitmap) {
    AndroidBitmapInfo androidBitmapInfo ;
    void* pixels;
    AndroidBitmap_getInfo(env, jBitmap, &androidBitmapInfo);
    AndroidBitmap_lockPixels(env, jBitmap, &pixels);
    unsigned char* pixelsChar = (unsigned char*) pixels;

    Bitmap_fillAll(pixels, &androidBitmapInfo, 0x203040FF);
//    for (uint32_t x = 10; x < 20; ++x)
//        for (uint32_t y = 10; y < 20; ++y)
//            Bitmap_drawPoint(pixels, &androidBitmapInfo, x, y, 0x908070FF);
    for (uint32_t x = 10; x < 20; ++x)
        for (uint32_t y = 10; y < 20; ++y)
            Bitmap_drawLine(pixels, &androidBitmapInfo,60, 80, x, y, 0xFF908070);
//    Bitmap_drawPaintedBox(pixels, &androidBitmapInfo, 20, 19, 50, 50, 0xFF888888);
    Bitmap_drawBox(pixels, &androidBitmapInfo, 20, 20, 60, 60, 0xFF000000);

    AndroidBitmap_unlockPixels(env, jBitmap);


}

void Bitmap_fillAll(void* pixels, AndroidBitmapInfo* androidBitmapInfo, uint32_t color)
{
    uint32_t * pPoint = pixels;
    uint32_t pixelCount = androidBitmapInfo->width * androidBitmapInfo->height;
    for(uint32_t idx = 0; idx < pixelCount; ++idx)
        *(pPoint++) = color;

}
int sign(int x)
{
    return (x > 0) - (x < 0);
}
void Bitmap_drawPoint(void* pixels, AndroidBitmapInfo* androidBitmapInfo, uint32_t x,
                      uint32_t y, uint32_t color)
{
    *(uint32_t *)(pixels + sizeof (uint32_t) * x + y * androidBitmapInfo->stride) = color;
}
void Bitmap_drawLine(void* pixels, AndroidBitmapInfo* androidBitmapInfo,
                     uint32_t x1, uint32_t y1, uint32_t x2, uint32_t y2, uint32_t color)
 {
    int32_t xDelta = x2 - x1;
    int32_t yDelta = y2 - y1;
    if (abs(xDelta) > abs(yDelta)) {
        int xStep = sign(xDelta);
        double yStep = (double)yDelta / (double)abs(xDelta);
        double yPos = y1;
        for (uint32_t xPos = x1; xPos != x2; xPos += xStep) {
            Bitmap_drawPoint(pixels, androidBitmapInfo, xPos, yPos, color);
            yPos += yStep;
        }
    } else {
        int yStep = sign(yDelta);
        double xStep = (double)xDelta / (double)abs(yDelta);
        double xPos = x1;
        for (uint32_t yPos = y1; yPos != y2; yPos += yStep) {
            Bitmap_drawPoint(pixels, androidBitmapInfo, xPos, yPos, color);
            xPos += xStep;
        }
    }
}
void Bitmap_drawBox(void* pixels, AndroidBitmapInfo* androidBitmapInfo,
                    uint32_t x1, uint32_t y1, uint32_t x2, uint32_t y2, uint32_t color) {
    Bitmap_drawLine(pixels, androidBitmapInfo, x1, y1, x2, y1, color);
    Bitmap_drawLine(pixels, androidBitmapInfo, x2, y1, x2, y2, color);
    Bitmap_drawLine(pixels, androidBitmapInfo, x2, y2, x1, y2, color);
    Bitmap_drawLine(pixels, androidBitmapInfo, x1, y2, x1, y1, color);
}
void Bitmap_drawPaintedBox(void* pixels, AndroidBitmapInfo* androidBitmapInfo,
                           uint32_t x1, uint32_t y1, uint32_t x2, uint32_t y2,
                           uint32_t innterColor) {
    uint32_t * pRow = pixels + androidBitmapInfo->stride * y1 + sizeof(uint32_t) * x1;
    for(uint32_t y = y1; y <= y2; ++y)
    {
        uint32_t* pPoint = pRow;
        for(uint32_t x = x1; x <= x2; ++x)
            *(pPoint++) = innterColor;
        pRow += androidBitmapInfo->width;
    };
}
