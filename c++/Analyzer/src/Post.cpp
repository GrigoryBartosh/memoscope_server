#include "Post.h"

using std::string;
using std::vector;

Post::Post() 
{
}

void Post::addText(const string& text)
{
    this->text += "\n";
    this->text += text;
}

vector<string>& Post::getImgPaths()
{
    return imgPaths;
}