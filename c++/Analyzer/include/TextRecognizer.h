#pragma once

#include <iostream>
#include <fstream>
#include <string>
#include <opencv2/opencv.hpp>
#include <tesseract/baseapi.h>
#include <leptonica/allheaders.h>

struct TextRecognizer
{
private:
    tesseract::TessBaseAPI *_tesseractApi;
    std::vector<double> _rotateAngles;
    std::vector<cv::Scalar> _colorCmps;

    void readConfig();

    std::string findTextTesseract(std::string path);
    std::string findText(const cv::Mat &img);

    std::string rotate(const cv::Mat &img, double alpha);
    std::string rotateAll(const cv::Mat &img);
    std::string condense(const cv::Mat &img);
    std::string filterBySizes(const cv::Mat &img, std::vector<std::vector<cv::Point>> cmps);
    std::string findCmps(const cv::Mat &img, cv::Scalar color);
    std::string findCmpsAll(const cv::Mat &img);
    std::string invertColors(const cv::Mat &img);
    std::string bilateralFilter(const cv::Mat &img);
    std::string processImg(const cv::Mat &img);

public:
    TextRecognizer();
    ~TextRecognizer();

    std::string recognize(std::string path);
};