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
void(*calcChargeFunctions[4])(Cell*, Cell*);

void MyoCell_create(Cell* pCell, Tissue* tissue, int xPos, int yPos)
{
    pCell->pTissue = tissue;
    pCell->colPos = xPos;
    pCell->rowPos = yPos;
    pCell->cellType = MYOCELL;
    pCell->channelState = RESTING;
    pCell->pChannel = channel[pCell->cellType];
    pCell->step = 700;
    pCell->vm = -70;
    pCell->charge = -70;
}
void AutoCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos)
{
    cell->pTissue = tissue;
    cell->colPos = xPos;
    cell->rowPos = yPos;
    cell->cellType = AUTOCELL;
    cell->channelState = RESTING;
    cell->pChannel = channel[cell->cellType];
    cell->step = 700;
    cell->vm = -70;
    cell->charge = -70;
}
void FastCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos)
{
    cell->pTissue = tissue;
    cell->colPos = xPos;
    cell->rowPos = yPos;
    cell->cellType = FASTCELL;
    cell->channelState = RESTING;
    cell->pChannel = channel[cell->cellType];
    cell->step = 700;
    cell->vm = -70;
    cell->charge = -70;
}
void DeadCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos)
{
    cell->pTissue = tissue;
    cell->colPos = xPos;
    cell->rowPos = yPos;
    cell->cellType = DEADCELL;
    cell->channelState = RESTING;
    cell->pChannel = channel[DEADCELL];
    cell->step = 700;
    cell->vm = -70;
    cell->charge = -70;
}
void Cell_membranePotential(Cell* pTargetCells, Cell* pSourceCells)
{
    pTargetCells->vm = (pTargetCells->pChannel->alphaVector)[pTargetCells->step];
}
void Cell_channelUpdate(Cell* pTargetCells, Cell* pSourceCells)
{
    if (pTargetCells->channelState == RESTING
        && pTargetCells->charge > pTargetCells->pChannel->inactivationGateThreadhold)
    {
        pTargetCells->channelState = OPEN;
        pTargetCells->step = 0;
    }
    else if (pTargetCells->channelState == OPEN
             && pTargetCells->charge > pTargetCells->pChannel->activationGateThreadhold)
    {
        pTargetCells->channelState = INACTIVE;
    }
    else if (pTargetCells->channelState == INACTIVE
             && pTargetCells->charge < pTargetCells->pChannel->inactivationGateThreadhold)
    {
        pTargetCells->channelState = RESTING;
    }
    if (pTargetCells->step < pTargetCells->pChannel->alphaVectorSize - 1)
        ++pTargetCells->step;
}
Channel *Channel_create(double inactGateThreadhold, double actGateThreadhold,
                        Pair *coords, uint32_t* cell_colors)
{
    Channel *_channel = (Channel *) malloc(sizeof(Channel));
    _channel->inactivationGateThreadhold = inactGateThreadhold;
    _channel->activationGateThreadhold = actGateThreadhold;
    buildAlphaVector(coords, &(_channel->alphaVector), &(_channel->alphaVectorSize));

    _channel->cell_colors = cell_colors;

    return _channel;
}
void buildAlphaVector(const Pair* inPairVector, double** outAlphaVector, size_t* outAlphaSize) {

    double lastVm = inPairVector[0].second;
    size_t lastTime = 0;
    int alphaVectorSize = 100;
    double * alphaVector = (double *)malloc(alphaVectorSize * sizeof(double));
    alphaVector[lastTime++] = lastVm;
    for(const Pair* pInPair = inPairVector; pInPair->first != 0 || pInPair->second != 0.0; ++pInPair) {
        double deltaVolt = (pInPair->second - lastVm) / pInPair->first;
        for (int time = 0; time < pInPair->first; ++time) {
            if (alphaVectorSize < lastTime) {
                alphaVectorSize += 100;
                alphaVector = (double *)realloc(alphaVector, alphaVectorSize * sizeof(double));
            }
            lastVm += deltaVolt;
            alphaVector[lastTime] = lastVm;
            ++lastTime;
        }
    }
    alphaVector = (double *)realloc(alphaVector, lastTime * sizeof(double));
    *outAlphaVector = alphaVector;
    *outAlphaSize = lastTime;
}

uint32_t* Cell_loadColorList(int num, ...)
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
        return GETPCELL(cell->pTissue, cell->colPos, cell->rowPos - 1);
    else
        return cell;
}
Cell* Cell_lowerCell(Cell* cell)
{
    if (cell->rowPos < cell->pTissue->ySize - 1)
        return GETPCELL(cell->pTissue, cell->colPos, cell->rowPos + 1);
    else
        return cell;
}
Cell* Cell_leftCell(Cell* cell)
{
    if (cell->colPos > 0)
        return GETPCELL(cell->pTissue, cell->colPos - 1, cell->rowPos);
    else
        return cell;
}
Cell* Cell_rightCell(Cell* cell)
{
    if (cell->colPos < cell->pTissue->xSize - 1)
        return GETPCELL(cell->pTissue, cell->colPos + 1, cell->rowPos);
    else
        return cell;
}
Cell* Cell_lowerRightCell(Cell* cell)
{
    if (cell->colPos < cell->pTissue->xSize - 1 && cell->rowPos < cell->pTissue->ySize - 1)
        return GETPCELL(cell->pTissue, cell->colPos + 1, cell->rowPos + 1);
    else
        return cell;
}
Cell* Cell_upperRightCell(Cell* cell)
{
    if (cell->colPos < cell->pTissue->xSize - 1 && cell->rowPos > 0)
        return GETPCELL(cell->pTissue, cell->colPos + 1, cell->rowPos - 1);
    else
        return cell;
}
Cell* Cell_upperLeftCell(Cell* cell)
{
    if (cell->colPos > 0 && cell->rowPos > 0)
        return GETPCELL(cell->pTissue, cell->colPos - 1, cell->rowPos - 1);
    else
        return cell;
}
Cell* Cell_lowerLeftCell(Cell* cell)
{
    if (cell->colPos > 0 && cell->rowPos < cell->pTissue->ySize - 1)
        return GETPCELL(cell->pTissue, cell->colPos - 1, cell->rowPos + 1);
    else
        return cell;
}

void Cell_calculateCharge(Cell* pTargetCell, Cell* pSourceCell) {
    calcChargeFunctions[pTargetCell->cellType](pTargetCell, pSourceCell);
}
void MyoCell_calculateCharge(Cell* pTargetCell, Cell* pSourceCell) {
    pTargetCell->charge = 0.4 * pTargetCell->vm +
                          (Cell_upperCell(pTargetCell)->vm + Cell_lowerCell(pTargetCell)->vm + Cell_leftCell(pTargetCell)->vm +
                           Cell_rightCell(pTargetCell)->vm + Cell_lowerRightCell(pTargetCell)->vm +
                           Cell_upperRightCell(pTargetCell)->vm + Cell_upperLeftCell(pTargetCell)->vm +
                           Cell_lowerLeftCell(pTargetCell)->vm) * 0.075;
}
void AutoCell_calculateCharge(Cell* pTargetCell, Cell* pSourceCell) {
    pTargetCell->charge = 0.6 * pTargetCell->vm +
                          (Cell_upperCell(pTargetCell)->vm + Cell_lowerCell(pTargetCell)->vm + Cell_leftCell(pTargetCell)->vm +
                           Cell_rightCell(pTargetCell)->vm + Cell_lowerRightCell(pTargetCell)->vm +
                           Cell_upperRightCell(pTargetCell)->vm + Cell_upperLeftCell(pTargetCell)->vm +
                           Cell_lowerLeftCell(pTargetCell)->vm) * 0.05;
}
void FastCell_calculateCharge(Cell* pTargetCell, Cell* SourceCell) {
    pTargetCell->charge = 0.4 * pTargetCell->vm +
                          (Cell_upperCell(pTargetCell)->vm + Cell_lowerCell(pTargetCell)->vm + Cell_leftCell(pTargetCell)->vm +
                           Cell_rightCell(pTargetCell)->vm + Cell_lowerRightCell(pTargetCell)->vm +
                           Cell_upperRightCell(pTargetCell)->vm + Cell_upperLeftCell(pTargetCell)->vm +
                           Cell_lowerLeftCell(pTargetCell)->vm) * 0.075;
}
void DeadCell_calculateCharge(Cell* pTargetCell, Cell* pSourceCell) { }
Tissue *Tissue_create(int xSize, int ySize) {
    Tissue *tissue = (Tissue *) malloc(sizeof(Tissue));
    tissue->xSize = xSize;
    tissue->ySize = ySize;
    tissue->pCells = Tissue_createCells(tissue, xSize, ySize);

    return tissue;
}
Tissue* Tissue_clone(Tissue* pTissue) {
    Tissue* pClonedTissue = malloc(sizeof(Tissue));
    memcpy(pClonedTissue, pTissue, sizeof(Tissue));

    pClonedTissue->pCells = Tissue_cloneCells(pTissue);

    return pClonedTissue;
}
void Tissue_destroy(Tissue *tissue) {
    Tissue_destroyCells(tissue->pCells);
    free(tissue);
}
Cell* Tissue_createCells(Tissue* pTissue, int xSize, int ySize)
{
    Cell *pCells = malloc(xSize * ySize * sizeof(Cell));
    for (int idy = 0; idy < ySize; ++idy)
        for (int idx = 0; idx < xSize; ++idx) {
            MyoCell_create(pCells + xSize * idy + idx, pTissue, idx, idy);
        }
    return (Cell*) pCells;
}
Cell* Tissue_cloneCells(Tissue* pTissue) {
    int cellCount = pTissue->xSize * pTissue->ySize;
    Cell* pCells = malloc(cellCount * sizeof(Cell));
    memcpy(pCells, pTissue->pCells, cellCount * sizeof(Cell));
    return pCells;
}
void Tissue_destroyCells(Cell *cells)
{
    free(cells);
}
void Tissue_calcAll(Tissue* pTargetTissue, Tissue* pSourceTissue)
{
    Tissue_forAllCells(pTargetTissue, pSourceTissue, Cell_membranePotential);
    Tissue_forAllCells(pTargetTissue, pSourceTissue, Cell_calculateCharge);
    Tissue_forAllCells(pTargetTissue, pSourceTissue, Cell_channelUpdate);
}
void Tissue_forAllCells(Tissue* pTargetTissue, Tissue* pSourceTissue, void(*func)(Cell*, Cell*)) {
    int length = pTargetTissue->xSize * pTargetTissue->ySize;
    Cell* pTargetCell = pTargetTissue->pCells;
    Cell* pSourceCell = pSourceTissue->pCells;
    for (int idx = 0; idx < length; ++idx)
        func(pTargetCell++, pSourceCell++);
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
