#pragma once

#include <iostream>
#include <fstream>
#include <string>
#include <unistd.h>
#include <cmath>
#include <opencv2/opencv.hpp>
#include <tesseract/baseapi.h>
#include <leptonica/allheaders.h>

struct TextRecognizer
{
private:
    const double CONDENSE = 0.0013; //percents of dioganale
    const double SMALL_CMP_BOARD_LOWER = 0.00007; //percents of img square
    const double SMALL_CMP_BOARD_UPPER = 0.026; //percents of img square
    static const int SIMILAR_COLORS_BOARD = 50;

    static const size_t MICROSECONDS_WAIT = 500000;
    static const size_t MAX_ITRS_FAIL_READ = 10;

    tesseract::TessBaseAPI *_tesseractApi;
    std::vector<double> _rotateAngles;
    std::vector<cv::Scalar> _colorCmps;

    void readConfig();
    bool similarColors(cv::Scalar a, cv::Scalar b);
    cv::Scalar getColor(cv::Mat img, int x, int y);

    std::string findTextTesseract(std::string path);
    std::string findText(const cv::Mat &img);

    std::string rotate(const cv::Mat &img, double angle);
    std::string rotateAll(const cv::Mat &img);
    std::string condense(const cv::Mat &img);
    std::string filterBySizes(const cv::Mat &img, std::vector<std::vector<cv::Point>> cmps);
    std::string findCmps(const cv::Mat &img, cv::Scalar color);
    std::string findCmpsAll(const cv::Mat &img);
    std::string invertColors(const cv::Mat &img);
    std::string applyBilateralFilter(const cv::Mat &img);
    std::string processImg(const cv::Mat &img);

public:
    TextRecognizer();
    ~TextRecognizer();

    std::string recognize(std::string path);
};