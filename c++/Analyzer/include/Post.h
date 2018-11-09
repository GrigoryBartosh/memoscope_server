#pragma once

#include <cstring>
#include <string>
#include <vector>

struct Post
{
private:
    std::string text;
    std::vector<std::string> imgPaths;
    long long group_id;
    long long post_id;
    long long timestamp;

public:
    Post();

    void addText(const std::string& text);
    std::vector<std::string>& getImgPaths();
};