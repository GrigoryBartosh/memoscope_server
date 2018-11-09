#pragma once

#include <string>
#include <opencv2/opencv.hpp>
#include <tesseract/baseapi.h>
#include <leptonica/allheaders.h>

struct TextRecognizer
{
private:
    tesseract::TessBaseAPI *_tesseractApi;

    std::string findTextTesseract(std::string path);
    std::string findText(const cv::Mat &img);

public:
    TextRecognizer();
    ~TextRecognizer();

    std::string recognize(std::string path);
};