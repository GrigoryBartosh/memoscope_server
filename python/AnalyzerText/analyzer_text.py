from concurrent import futures
import time
import grpc
import analyzerText_pb2
import analyzerText_pb2_grpc

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

class AnalyzerText(analyzerText_pb2_grpc.AnalyzerTextServicer):
    def __init__(self):
        //TODO
        pass

    def analyze(text):
        //TODO
        return text

    def AnalyzeText(self, request, context):
        text = request.text
        text = analyze(text)
        return analyzerText_pb2.AnalyzeTextResponse(text=text)

def read_port():
    port = 0

    with open("analyzer_text.cfg","r") as file:
        port = int(file.read())

    return port

def serve():
    port = read_port()

    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    analyzerText_pb2_grpc.add_AnalyzerTextServicer_to_server(AnalyzerText(), server)
    server.add_insecure_port("[::]:" + str(port))
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()