#include "Controller.h"

using std::string;

Controller::Controller()
{
}

void Controller::run() 
{
    while (true)
    {
        PostData post = _connectorBuffer.getPost();

        std::cout << "1" << std::endl;

        /*for (const string &path : post.getImgPaths()) {
            string text = _textRecognizer.recognize(path);
            post.addText(text);
        }

        _connectorDataBase.addPost(post);*/
    }
}