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

        for (const string &path : post.getImgPaths()) {
            string text = _textRecognizer.recognize(path);
            post.addText(text);
        }

        _connectorDataBase.addPost(post);
    }
}