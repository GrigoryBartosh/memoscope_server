#pragma once

#include <cstring>
#include <string>
#include <vector>

struct PostData
{
private:
    std::string _text;
    std::vector<std::string> _imgPaths;
    long long _groupId;
    long long _postId;
    long long _timestamp;

public:
    PostData();

    void setText(const std::string& text);
    void addText(const std::string& text);
    std::string& getText();

    void addImgPath(const std::string& path);
    std::vector<std::string>& getImgPaths();

    void setGropId(long long groupId);
    long long getGropId();

    void setPostId(long long postId);
    long long getPostId();

    void setTimestamp(long long timestamp);
    long long getTimestamp();
};