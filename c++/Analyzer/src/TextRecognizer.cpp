#include "TextRecognizer.h"

using std::string;
using std::remove;
using cv::imread;
using cv::Mat;
using cv::Point;

TextRecognizer::TextRecognizer() 
{
    tesseractApi = new tesseract::TessBaseAPI();
    tesseractApi->Init(NULL, "rus", tesseract::OEM_LSTM_ONLY);
    tesseractApi->SetPageSegMode(tesseract::PSM_SINGLE_BLOCK);
}

string TextRecognizer::findTextTesseract(string path)
{
    char *outText;

    Pix *image = pixRead(path.c_str());
    tesseractApi->SetImage(image);
    outText = tesseractApi->GetUTF8Text();

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

string TextRecognizer::recognize(const string path)
{
    Mat img = imread(path);

    string text = findText(img);

    return text;
}

TextRecognizer::~TextRecognizer() 
{
    tesseractApi->End();
}