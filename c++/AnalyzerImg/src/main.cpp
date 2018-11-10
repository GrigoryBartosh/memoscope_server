#include <iostream>
#include "Controller.h"

//#include <iostream>
//#include <opencv2/opencv.hpp>

//using namespace cv;
using namespace std;

int main() {
    Controller controller;
    controller.run();

    /*Mat img_src;
    img_src = imread("1.jpg");
    show(img_src);
    waitKey(0); 

    Mat img_filterd;
    bilateralFilter(img_src, img_filterd, 21, 150, 150);
    show(img_filterd);
    waitKey(0);*/

    return 0;
}

