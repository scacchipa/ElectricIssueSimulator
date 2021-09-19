//
// Created by scacc on 12/08/2021.
//

#ifndef ELECTRICISSUESIMULATOR_TISSUE_H
#define ELECTRICISSUESIMULATOR_TISSUE_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stddef.h>
#include <jni.h>
#include <android/bitmap.h>

#define GETPCELL(tissue, x, y) (tissue)->pCells + (tissue)->xSize * (y) + (x)

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
    uint32_t* cell_colors;
} Channel;
typedef struct Cell
{
    struct Tissue* pTissue;
    int colPos;
    int rowPos;
    CellType cellType;
    ChannelState channelState;
    Channel* pChannel;
    int step;
    double vm;
    double charge;
} Cell;
typedef struct Tissue
{
    Cell* pCells;
    int ySize;
    int xSize;
} Tissue;

typedef struct Pair
{
    int first;
    double second;
} Pair;

void MyoCell_create(Cell* pCell, Tissue* tissue, int xPos, int yPos);
void AutoCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos);
void FastCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos);
void DeadCell_create(Cell* cell, Tissue* tissue, int xPos, int yPos);

void Cell_membranePotential(Cell* pTargetCells, Cell* pSourceCells);
void Cell_channelUpdate(Cell* pTargetCells, Cell* pSourceCells);
uint32_t* Cell_loadColorList(int num, ...);
Channel* Channel_create(double inactGateThreadhold, double actGateThreadhold,
                        Pair* coords, uint32_t* colors);
void buildAlphaVector(const Pair* inPairVector, double** outAlphaVector, size_t* outAlphaSize);

Cell* Cell_upperCell(Cell* cell);
Cell* Cell_lowerCell(Cell* cell);
Cell* Cell_leftCell(Cell* cell);
Cell* Cell_rightCell(Cell* cell);
Cell* Cell_lowerRightCell(Cell* cell);
Cell* Cell_upperRightCell(Cell* cell);
Cell* Cell_upperLeftCell(Cell* cell);
Cell* Cell_lowerLeftCell(Cell* cell);

void Cell_calculateCharge(Cell* pTargetCells, Cell* pSourceCell);
void MyoCell_calculateCharge(Cell* pTargetCells, Cell* pSourceCell);
void AutoCell_calculateCharge(Cell* pTargetCell, Cell* pSourceCell);
void FastCell_calculateCharge(Cell* pTargetCell, Cell* SourceCell);
void DeadCell_calculateCharge(Cell* pTargetCell, Cell* pSourceCell);

Tissue *Tissue_create(int xSize, int ySize);
Tissue* Tissue_clone(Tissue* pTissue);
void Tissue_destroy(Tissue *tissue);
Cell* Tissue_createCells(Tissue* pTissue, int xSize, int ySize);
Cell* Tissue_cloneCells(Tissue* pTissue);
void Tissue_destroyCells(Cell *cells);
void Tissue_calcAll(Tissue* pTargetTissue, Tissue* pSourceTissue);
void Tissue_forAllCells(Tissue* pTargetTissue, Tissue* pSourceTissue, void(*func)(Cell*, Cell*));


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

#ifdef __cplusplus
}
#endif

#endif //ELECTRICISSUESIMULATOR_TISSUE_H
