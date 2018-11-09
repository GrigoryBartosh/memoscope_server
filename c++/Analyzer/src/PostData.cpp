#include "PostData.h"

using std::string;
using std::vector;

PostData::PostData() 
{
}

void PostData::setText(const std::string& text)
{
    _text = text;
}

void PostData::addText(const std::string& text)
{
    _text += "\n";
    _text += text;
}

std::string PostData::getText() const
{
    return _text;
}

void PostData::addImgPath(const std::string& path)
{
    _imgPaths.push_back(path);
}

std::vector<std::string>& PostData::getImgPaths()
{
    return _imgPaths;
}

void PostData::setGroupId(long long groupId)
{
    _groupId = groupId;
}

long long PostData::getGroupId() const
{
    return _groupId;
}

void PostData::setPostId(long long postId)
{
    _postId = postId;
}

long long PostData::getPostId() const
{
    return _postId;
}

void PostData::setTimestamp(long long timestamp)
{
    _timestamp = timestamp;
}

long long PostData::getTimestamp() const
{
    return _timestamp;
}