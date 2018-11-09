#pragma once

#include <string>
#include <iostream>
#include <fstream>
#include <memory>
#include <grpcpp/grpcpp.h>
#include "../generated/buffer.grpc.pb.h"
#include "PostData.h"

struct ConnectorBuffer
{
private:
    struct Client {
    private:
        std::unique_ptr<Buffer::Stub> stub_;

    public:
        Client(std::shared_ptr<grpc::Channel> channel);

        PostData getPost();
    };

    void readConfig(std::string &ip, std::string &port);

    Client *_client;

public:
    ConnectorBuffer();
    ~ConnectorBuffer();

    PostData getPost();
};