#pragma once

#include <string>
#include <iostream>
#include <fstream>
#include <memory>
#include <grpcpp/grpcpp.h>
#include "../generated/dataBase.grpc.pb.h"
#include "PostData.h"

struct ConnectorDataBase
{
private:
    struct Client {
    private:
        std::unique_ptr<DataBase::Stub> stub_;

    public:
        Client(std::shared_ptr<grpc::Channel> channel);

        void addPost(const PostData &post);
    };

    void readConfig(std::string &ip, std::string &port);

    Client *_client;

public:
    ConnectorDataBase();
    ~ConnectorDataBase();

    void addPost(const PostData &post);
};