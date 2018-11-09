#pragma once

#include <string>
#include "TextRecognizer.h"
#include "ConnectorBuffer.h"
#include "ConnectorDataBase.h"
#include "PostData.h"

struct Controller
{
private:
    ConnectorBuffer _connectorBuffer;
    ConnectorDataBase _connectorDataBase;
    TextRecognizer _textRecognizer;

public:
    Controller();
    void run();
};