//
// Created by scacc on 12/08/2021.
//

#ifndef ELECTRICISSUESIMULATOR_TISSUE_H
#define ELECTRICISSUESIMULATOR_TISSUE_H

#include <stddef.h>
#include <jni.h>
#include <android/bitmap.h>

typedef enum ChannelState
{
    RESTING = 0,
    OPEN = 1,
    INACTIVE = 2
} ChannelState;
typedef enum CellType {
    MYOCELL = 0,
    AUTOCELL = 1,
    FASTCELL = 2,
    DEADCELL = 3
} CellType;
typedef struct Channel
{
    double inactivationGateThreadhold;
    double activationGateThreadhold;
    double* alphaVector;
    size_t alphaVectorSize;
} Channel;
typedef struct Cell
{
    struct Tissue* tissue;
    int colPos;
    int rowPos;
    CellType cellType;
    ChannelState channelState;
    Channel* channel;
    int step;
    double vm;
    double charge;
} Cell;
typedef struct Tissue
{
    Cell** cells;
    int ySize;
    int xSize;
} Tissue;

typedef struct Pair
{
    int first;
    double second;
} Pair;

void MyoCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos);
void AutoCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos);
void FastCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos);
void DeadCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos);

void Cell_membranePotential(Cell* cell);
void Cell_channelUpdate(Cell* cell);
uint32_t* Cell_createColorList(int num, ...);

Cell* Cell_upperCell(Cell* cell);
Cell* Cell_lowerCell(Cell* cell);
Cell* Cell_leftCell(Cell* cell);
Cell* Cell_rightCell(Cell* cell);
Cell* Cell_lowerRightCell(Cell* cell);
Cell* Cell_upperRightCell(Cell* cell);
Cell* Cell_upperLeftCell(Cell* cell);
Cell* Cell_lowerLeftCell(Cell* cell);
void MyoCell_calculateCharge(Cell* cell);
void AutoCell_calculateCharge(Cell* cell);
void FastCell_calculateCharge(Cell* cell);
void DeadCell_calculateCharge(Cell* cell);
Channel* Channel_create(double inactGateThreadhold, double actGateThreadhold,
                        Pair* coords);

void buildAlphaVector(const Pair* inPairVector, double** outAlphaVector, size_t* outAlphaSize);

Tissue *Tissue_create(int xSize, int ySize);
void Tissue_destroy(Tissue *tissue);
Cell** Tissue_createCells(Tissue* tissue, int xSize, int ySize);
void Tissue_destroyCells(Cell **cells);
void Tissue_forAllCells(Tissue* tissue, void(*func)(Cell*));


void Bitmap_fillAll(void* pixels, AndroidBitmapInfo* androidBitmapInfo, uint32_t color);
int sign(int x);
void Bitmap_drawPoint(void* pixels, AndroidBitmapInfo* androidBitmapInfo, uint32_t x,
                      uint32_t y, uint32_t color);
void Bitmap_drawLine(void* pixels, AndroidBitmapInfo* androidBitmapInfo,
                     uint32_t x1, uint32_t y1, uint32_t x2, uint32_t y2, uint32_t color);
void Bitmap_drawBox(void* pixels, AndroidBitmapInfo* androidBitmapInfo,
                    uint32_t x1, uint32_t y1, uint32_t x2, uint32_t y2, uint32_t color);
void Bitmap_drawPaintedBox(void* pixels, AndroidBitmapInfo* androidBitmapInfo,
                           uint32_t x1, uint32_t y1, uint32_t x2, uint32_t y2,
                           uint32_t innterColor);
#endif //ELECTRICISSUESIMULATOR_TISSUE_H
