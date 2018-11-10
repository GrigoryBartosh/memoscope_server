#include "Controller.h"

using std::string;
using std::cout;
using std::endl;

Controller::Controller()
{
}

void Controller::run() 
{
    while (true)
    {
        cout << "1.request post" << endl;
        PostData post = _connectorBuffer.getPost();
        cout << "1.post received" << endl;

        for (const string &path : post.getImgPaths()) {
            cout << "2.processing img: " << path << endl;

            string fullPath = "../../photos/" + path;
            string text = _textRecognizer.recognize(path);
            post.addText(text);

            cout << "2.processed img: " << path << endl;
        }

        cout << "3.sending post" << endl;
        _connectorDataBase.addPost(post);
        cout << "3.post sended" << endl;
    }
}