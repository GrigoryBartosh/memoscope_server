#include "Controller.h"

using std::string;

Controller::Controller()
{
}

void Controller::run() 
{
    while (true)
    {
        Post post = connectorBuffer.getPost();

        for (const string &path : post.getImgPaths()) {
            string text = textRecognizer.recognize(path);
            post.addText(text);
        }

        connectorDataBase.addPost(post);
    }
}