#include "TextRecognizer.h"

using std::ifstream;
using std::string;
using std::remove;
using std::vector;
using cv::imread;
using cv::Mat;
using cv::Point;
using cv::Scalar;

void TextRecognizer::readConfig()
{
    ifstream in("config/TextRecognizer.cfg");

    int countAngles;
    in >> countAngles;
    while (countAngles--) {
        double angle;
        in >> angle;
        _rotateAngles.push_back(angle);
    }

    int countColors;
    in >> countColors;
    while (countColors--) {
        int r, g, b;
        in >> r >> g >> b;
        _colorCmps.push_back(Scalar(r, g, b));
    }

    in.close();
}

TextRecognizer::TextRecognizer() 
{
    readConfig();

    _tesseractApi = new tesseract::TessBaseAPI();
    _tesseractApi->Init(NULL, "rus", tesseract::OEM_LSTM_ONLY);
    _tesseractApi->SetPageSegMode(tesseract::PSM_SINGLE_BLOCK);
}

string TextRecognizer::findTextTesseract(string path)
{
    char *outText;

    Pix *image = pixRead(path.c_str());
    _tesseractApi->SetImage(image);
    outText = _tesseractApi->GetUTF8Text();

    string res(outText);
    delete [] outText;
    pixDestroy(&image);

    return res;
}

string TextRecognizer::findText(const Mat &img)
{
    imwrite("t.jpg", img);
    string res = findTextTesseract("t.jpg");
    remove("t.jpg");

    return res;
}

string TextRecognizer::rotate(const Mat &img, double alpha)
{
    Mat rotated = img.clone();
    //TODO rotate

    string text;
    text += findText(rotated);
    return text;
}

string TextRecognizer::rotateAll(const Mat &img)
{
    string text;
    for (double angle : _rotateAngles)
    {
        if (text.length() > 0) {
            text += "\n";
        }
        text += rotate(img, angle);
    }

    return text;
}

string TextRecognizer::condense(const Mat &img)
{
    Mat condensed = img.clone();
    //TODO erode

    string text;
    text += rotateAll(condensed);
    return text;
}

string TextRecognizer::filterBySizes(const Mat &img, vector<vector<Point>> cmps)
{
    Mat filtered = img.clone();
    //TODO filter

    string text;
    text += condense(img);
    text += "\n";
    text += rotateAll(img);
    return text;
}

string TextRecognizer::findCmps(const Mat &img, Scalar color)
{
    vector<vector<Point>> cmps;

    //TODO find cmps

    string text;
    text += filterBySizes(img, cmps);
    return text;
}

string TextRecognizer::findCmpsAll(const Mat &img)
{
    string text;
    for (Scalar color : _colorCmps)
    {
        if (text.length() > 0) {
            text += "\n";
        }
        text += findCmps(img, color);
    }

    return text;
}

string TextRecognizer::invertColors(const Mat &img)
{
    Mat inverted = img.clone();
    //TODO invert colors

    string text;
    text += rotateAll(inverted);
    return text;
}

string TextRecognizer::bilateralFilter(const Mat &img)
{
    Mat filtered = img.clone();
    //TODO filter

    string text;
    text += findCmpsAll(filtered);
    text += "\n";
    text += invertColors(filtered);
    return text;
}

string TextRecognizer::processImg(const Mat &img)
{
    string text;
    text += bilateralFilter(img);
    text += "\n";
    text += invertColors(img);
    return text;
}

string TextRecognizer::recognize(const string path)
{
    Mat img = imread(path);
    string text = processImg(img);
    return text;
}

TextRecognizer::~TextRecognizer() 
{
    _tesseractApi->End();
}