#include "ConnectorDataBase.h"

using std::string;
using std::ifstream;
using grpc::Channel;
using grpc::ClientContext;
using grpc::Status;

ConnectorDataBase::Client::Client(std::shared_ptr<grpc::Channel> channel)
: stub_(DataBase::NewStub(channel))
{
}

void ConnectorDataBase::Client::addPost(const PostData &post)
{
    StorePostRequest request;

    request.set_text(post.getText());
    request.set_groupid(post.getGroupId());
    request.set_postid(post.getPostId());
    request.set_timestamp(post.getTimestamp());

    StorePostResponse reply;
    ClientContext context;
    Status status = stub_->StorePost(&context, request, &reply);
}

void ConnectorDataBase::readConfig(std::string &ip, std::string &port)
{
    ifstream in("config/ConnectorDataBase.cfg");

    in >> ip;
    in >> port;

    in.close();
}

ConnectorDataBase::ConnectorDataBase() 
{
    string ip, port;
    readConfig(ip, port);
    string addres = ip + ":" + port;
    _client = new Client(grpc::CreateChannel(addres, grpc::InsecureChannelCredentials()));
}

void ConnectorDataBase::addPost(const PostData &post)
{
    _client->addPost(post);
}

ConnectorDataBase::~ConnectorDataBase() 
{
    delete _client;
}