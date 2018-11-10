#include "ConnectorBuffer.h"

using std::string;
using std::ifstream;
using grpc::Channel;
using grpc::ClientContext;
using grpc::Status;

ConnectorBuffer::Client::Client(std::shared_ptr<grpc::Channel> channel)
: stub_(Buffer::NewStub(channel))
{
}

PostData ConnectorBuffer::Client::getPost()
{
    GetNewPostRequest request;
    GetNewPostResponse reply;

    ClientContext context;

    Status status = stub_->GetNewPost(&context, request, &reply);

    PostData post;
    post.setText(reply.post().text());
    for (int i = 0; i < reply.post().picturepaths_size(); i++) {
        post.addImgPath(reply.post().picturepaths(i));
    }
    post.setGroupId(reply.post().groupid());
    post.setPostId(reply.post().postid());
    post.setTimestamp(reply.post().timestamp());

    return post;
}

void ConnectorBuffer::readConfig(std::string &ip, std::string &port)
{
    ifstream in("config/ConnectorBuffer.cfg");

    in >> ip;
    in >> port;

    in.close();
}

ConnectorBuffer::ConnectorBuffer() 
{
    string ip, port;
    readConfig(ip, port);
    string addres = ip + ":" + port;
    _client = new Client(grpc::CreateChannel(addres, grpc::InsecureChannelCredentials()));
}

PostData ConnectorBuffer::getPost()
{
    return _client->getPost();
}

ConnectorBuffer::~ConnectorBuffer() 
{
    delete _client;
}