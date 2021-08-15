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
enum CellType {
    MYOCELL = 0,
    AUTOCELL = 1,
    FASTCELL = 2,
    DEADCELL = 3
};
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

void buildAlphaVector(const Pair* inPairVector, double** outAlphaVector, size_t* outAlphaSize);

Tissue *Tissue_create(int xSize, int ySize);
Cell** Tissue_createCells(Tissue* tissue, int xSize, int ySize);
void Tissue_forAllCells(Tissue* tissue, void(*func)(Cell*));
uint32_t* createColorList(int num,...);
void setUp();

void Bitmap_fill(void* pixels, AndroidBitmapInfo* androidBitmapInfo, uint32_t color);

#endif //ELECTRICISSUESIMULATOR_TISSUE_H
