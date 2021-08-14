//
// Created by scacc on 12/08/2021.
//

#include "tissue.h"
#include <cstdlib>

Channel* channel[4];
uint32_t* cellColor[4];
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
void DeadCell_calculateCharge(Cell* cell) { };

void buildAlphaVector(const Pair* inPairVector, double** outAlphaVector, size_t* outAlphaSize) {
    int lastTime = 0;
    double lastVm = inPairVector[0].second;
    int alphaVectorSize = 100;
    auto* alphaVector = (double *)malloc(alphaVectorSize * sizeof(double));
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
    auto *tissue = (Tissue *) malloc(sizeof(Tissue));
    tissue->xSize = xSize;
    tissue->ySize = ySize;
    tissue->cells = Tissue_createCells(tissue, xSize, ySize);

    return tissue;
}
Cell** Tissue_createCells(Tissue* tissue, int xSize, int ySize) {
    Cell **cells = (Cell **) malloc(xSize * ySize * sizeof(Cell));

    for (int idx = 0; idx < xSize; ++idx)
        for (int idy = 0; idy < ySize; ++idy)
            MyoCell_create(&cells[idx][idy], tissue, idx, idy);
    return cells;
}

void Tissue_forAllCells(Tissue* tissue, void(*func)(Cell*)) {
    for (int idx = 0; idx < tissue->xSize; ++idx)
        for (int idy = 0; idy < tissue->ySize; ++idy)
            func(&(tissue->cells[idx][idy]));
}
Channel* Channel_create(double inactGateThreadhold, double actGateThreadhold,
        Pair* coords) {
    auto *_channel = (Channel *)malloc(sizeof(Channel));
    _channel->inactivationGateThreadhold = inactGateThreadhold;
    _channel->activationGateThreadhold = actGateThreadhold;
    buildAlphaVector(coords, &(_channel->alphaVector), &(_channel->alphaVectorSize));
    return _channel;
}
void setUp() {
    Pair *pairs[4];
    pairs[MYOCELL] = (Pair[]) {Pair{0, -75.0}, Pair{5, -70.0},
                               Pair{30, 25.0}, Pair{5, 10.0}, Pair{5, 7.0}, Pair{400, 5.0},
                               Pair{10, 4.0}, Pair{10, 3.0}, Pair{10, 2.0}, Pair{10, 0.0},
                               Pair{10, -2.0}, Pair{10, -4.0}, Pair{10, -7.0}, Pair{10, -11.0},
                               Pair{10, -16.0}, Pair{10, -22.0}, Pair{10, -29.0}, Pair{10, -37.0},
                               Pair{10, -45.0}, Pair{10, -54.0}, Pair{10, -62.0}, Pair{10, -67.0},
                               Pair{10, -71.0}, Pair{10, -74.0}, Pair{10, -75.0}, Pair{10, -75.0},
                               Pair{10, -75.0}, Pair{10, -75.0}, Pair{10, -75.0}, Pair{400, -75.0},
                               Pair{0, 0}};
    pairs[AUTOCELL] = (Pair[]) {
            Pair{0, -55}, Pair{15, -53}, Pair{15, -50}, Pair{15, -43.0},
            Pair{15, -35}, Pair{15, -27}, Pair{15, -17}, Pair{15, -7.0},
            Pair{15, -1}, Pair{15, 5}, Pair{15, 7}, Pair{15, 8.0},
            Pair{15, 8}, Pair{15, 8}, Pair{15, 7}, Pair{15, 6.0},
            Pair{15, 4}, Pair{15, 1}, Pair{15, -2}, Pair{15, -6.0},
            Pair{15, -10}, Pair{15, -14}, Pair{15, -19}, Pair{15, -24.0},
            Pair{15, -29}, Pair{15, -34}, Pair{15, -38}, Pair{15, -42.0},
            Pair{15, -46}, Pair{15, -50}, Pair{15, -54}, Pair{15, -57.0},
            Pair{15, -60}, Pair{15, -62}, Pair{15, -64},
            Pair{15, -65}, Pair{15, -65}, Pair{10, -65}, Pair{3000, -12.0},
            Pair{0, 0.0}};
    pairs[FASTCELL] = (Pair[]) {Pair{0, -75.0},
                                Pair{10, 25.0}, Pair{5, 10.0}, Pair{5, 7.0}, Pair{400, 5.0},
                                Pair{10, 4.0}, Pair{10, 3.0}, Pair{10, 2.0}, Pair{10, 0.0},
                                Pair{10, -2.0}, Pair{10, -4.0}, Pair{10, -7.0}, Pair{10, -11.0},
                                Pair{10, -16.0}, Pair{10, -22.0}, Pair{10, -29.0}, Pair{10, -37.0},
                                Pair{10, -45.0}, Pair{10, -54.0}, Pair{10, -62.0}, Pair{10, -67.0},
                                Pair{10, -71.0}, Pair{10, -74.0}, Pair{10, -75.0}, Pair{10, -75.0},
                                Pair{10, -75.0}, Pair{10, -75.0}, Pair{10, -75.0}, Pair{400, -75.0},
                                Pair{0, 0.0}};
    pairs[DEADCELL] = (Pair[]) {Pair{0, 0.0}};
    for (Pair *pPair = pairs[MYOCELL]; pPair->first != 0 && pPair->second != 0.0; ++pPair) {
        pPair->second = (pPair->second + 20.0) * 1.3;
    }
    for (Pair *pPair = pairs[AUTOCELL]; pPair->first != 0 && pPair->second != 0.0; ++pPair) {
        pPair->second = (pPair->second + 10.0) * 1.3;
    }
    for (Pair *pPair = pairs[FASTCELL]; pPair->first != 0 && pPair->second != 0.0; ++pPair) {
        pPair->second = (pPair->second + 20.0) * 1.3;
    }
    channel[MYOCELL] = Channel_create(-55, 0, pairs[MYOCELL]);
    channel[AUTOCELL] = Channel_create(-45, 0, pairs[AUTOCELL]);
    channel[FASTCELL] = Channel_create(-55, 0, pairs[FASTCELL]);
    channel[DEADCELL] = Channel_create(-55, 0, pairs[DEADCELL]);

    cellColor[MYOCELL] = createColorList( 3, 0xFF032B43, 0xFFF9C80E, 0xFFEA3546 );
    cellColor[AUTOCELL] = createColorList( 3, 0xFFFEB38B, 0xFFF9C80E, 0xFFEA3546 );
    cellColor[DEADCELL] = createColorList( 3, 0xFF000000 );
    cellColor[FASTCELL] = createColorList( 3, 0xFF032B43, 0xFFF9C80E, 0xFFEA3546 );


    calcChargeFunctions[MYOCELL] = MyoCell_calculateCharge;
    calcChargeFunctions[AUTOCELL] = AutoCell_calculateCharge;
    calcChargeFunctions[FASTCELL] = FastCell_calculateCharge;
    calcChargeFunctions[DEADCELL] = DeadCell_calculateCharge;

}
uint32_t* createColorList(int num, ...)
{
    auto colors = (uint32_t *)malloc(sizeof(uint32_t));
    va_list valist;
    va_start(valist, num);
    for (int i = 0; i < num; i++)
    {
        colors[i] = va_arg(valist, uint32_t);
    }
    va_end(valist);
    return colors;
}