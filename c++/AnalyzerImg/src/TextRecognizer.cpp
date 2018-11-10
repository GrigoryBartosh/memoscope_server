#include "TextRecognizer.h"

using std::ifstream;
using std::string;
using std::remove;
using std::vector;
using std::cout;
using std::endl;
using cv::imread;
using cv::Mat;
using cv::Point;
using cv::Scalar;
using cv::Vec3b;
using cv::bilateralFilter;
using cv::dilate;
using cv::imshow;
using cv::waitKey;

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

bool TextRecognizer::similarColors(Scalar a, Scalar b)
{
    return  abs(a.val[0] - b.val[0]) < SIMILAR_COLORS_BOARD &&
            abs(a.val[1] - b.val[1]) < SIMILAR_COLORS_BOARD &&
            abs(a.val[2] - b.val[2]) < SIMILAR_COLORS_BOARD;
}

Scalar TextRecognizer::getColor(Mat img, int x, int y)
{
    return Scalar(  img.at<Vec3b>(Point(x, y)).val[0],
                    img.at<Vec3b>(Point(x, y)).val[1],
                    img.at<Vec3b>(Point(x, y)).val[2]);
}

void setColor(Mat &img, int x, int y, int color)
{
    img.at<uchar>(Point(x, y)) = (uchar)color;
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
    imwrite("t.png", img);
    string res = findTextTesseract("t.png");
    remove("t.png");

    return res;
}

string TextRecognizer::rotate(const Mat &img, double angle)
{
    Mat iverted = Scalar::all(255) - img;
    cv::Point2f center((iverted.cols-1)/2.0, (iverted.rows-1)/2.0);
    Mat rot = cv::getRotationMatrix2D(center, angle, 1.0);
    cv::Rect2f bbox = cv::RotatedRect(cv::Point2f(), iverted.size(), angle).boundingRect2f();
    rot.at<double>(0,2) += bbox.width/2.0 - iverted.cols/2.0;
    rot.at<double>(1,2) += bbox.height/2.0 - iverted.rows/2.0;

    cv::Mat rotated;
    cv::warpAffine(iverted, rotated, rot, bbox.size());
    rotated = cv::Scalar::all(255) - rotated;

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
    Mat condensed;
    int w = img.cols;
    int h = img.rows;
    int v = sqrt((double)(w) * (double)(w) + (double)(h) * (double)(h)) * CONDENSE;
    if (v == 0) {
        v = 1;
    }
    dilate(img, condensed, cv::Mat(), cv::Point(-1, -1), 1);

    string text;
    text += rotateAll(condensed);
    return text;
}

string TextRecognizer::filterBySizes(const Mat &img, vector<vector<Point>> cmps)
{
    Mat filtered = img.clone();
    
    int w = img.cols;
    int h = img.rows;
    for (const vector<Point> &cmp : cmps) {
        double squarePercent = (double)(cmp.size()) / (w * h);
        if (SMALL_CMP_BOARD_LOWER < squarePercent && squarePercent < SMALL_CMP_BOARD_UPPER) {
            continue;
        }

        for (Point p : cmp) {
            setColor(filtered, p.x, p.y, 255);
        }
    }

    string text;
    text += condense(filtered);
    text += "\n";
    text += rotateAll(filtered);
    return text;
}

string TextRecognizer::findCmps(const Mat &img, Scalar color)
{
    static const int dx[4] = {-1, 0, 0, 1};
    static const int dy[4] = {0, -1, 1, 0};

    vector<vector<Point>> cmps;

    int w = img.cols;
    int h = img.rows;
    vector<vector<int>> cmpNums(w, vector<int>(h, -1));
    int cnt = 0;

    for (int x = 0; x < w; x++) {
        for (int y =  0; y < h; y++) {
            if (cmpNums[x][y] != -1) continue;
            if (!similarColors(getColor(img, x, y), color)) continue;

            size_t l = 0;
            vector<Point> q;
            cmpNums[x][y] = cnt;
            q.push_back(Point(x, y));
            while (l < q.size()) {
                Point cur = q[l++];

                for (int i = 0; i < 4; i++) {
                    int tx = cur.x + dx[i];
                    int ty = cur.y + dy[i];

                    if (tx < 0 || tx >= w || ty < 0 || ty >= h) continue;
                    if (cmpNums[tx][ty] != -1) continue;
                    if (!similarColors(getColor(img, tx, ty), color)) continue;

                    cmpNums[tx][ty] = cnt;
                    q.push_back(Point(tx, ty));
                }
            }

            cmps.push_back(q);
            cnt++;
        }
    }

    Mat imgBinary(img.size(), CV_8U);
    for (int x = 0; x < w; x++) {
        for (int y =  0; y < h; y++) {
            int v = 255 * (1 - similarColors(getColor(img, x, y), color));
            setColor(imgBinary, x, y, v);
        }
    }

    string text;
    text += filterBySizes(imgBinary, cmps);
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
    Mat inverted =  Scalar::all(255) - img;

    string text;
    text += rotateAll(inverted);
    return text;
}

string TextRecognizer::applyBilateralFilter(const Mat &img)
{
    Mat filtered;
    bilateralFilter(img, filtered, 21, 150, 150);

    string text;
    text += findCmpsAll(filtered);
    text += "\n";
    text += invertColors(filtered);
    text += "\n";
    text += rotateAll(filtered);
    return text;
}

string TextRecognizer::processImg(const Mat &img)
{
    string text;
    text += applyBilateralFilter(img);
    text += "\n";
    text += invertColors(img);
    text += "\n";
    text += rotateAll(img);
    return text;
}

string TextRecognizer::recognize(const string path)
{
    Mat img;

    size_t itr = 0;
    while (true) {
        img = imread(path);
        if(!img.data) {
            cout << "failed to load image: " << path << endl;

            itr++;
            if (itr >= MAX_ITRS_FAIL_READ) {
                return "";
            }

            usleep(MICROSECONDS_WAIT);
        } else {
            break;
        }
    }

    string text = processImg(img);
    return text;
}

TextRecognizer::~TextRecognizer() 
{
    _tesseractApi->End();
}