#pragma once

#include <string>
#include "TextRecognizer.h"
#include "ConnectorBuffer.h"
#include "ConnectorDataBase.h"
#include "Post.h"

struct Controller
{
private:
    ConnectorBuffer connectorBuffer;
    ConnectorDataBase connectorDataBase;
    TextRecognizer textRecognizer;

public:
    Controller();
    void run();
};