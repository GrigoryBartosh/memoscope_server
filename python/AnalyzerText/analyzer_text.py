from concurrent import futures
import time
import grpc
import analyzerText_pb2
import analyzerText_pb2_grpc
import jamspell
import pymorphy2

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

class AnalyzerText(analyzerText_pb2_grpc.AnalyzerTextServicer):
    simple_charectrs = None
    rus_charectrs = None
    corrector = None
    morph = None

    def __init__(self):
        self.simple_charectrs = set(list(u"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя"))
        self.rus_charectrs = set(list(u"АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя"))

        self.corrector = jamspell.TSpellCorrector()
        self.corrector.LoadLangModel("ru_small.bin")

        self.morph = pymorphy2.MorphAnalyzer()

        print("kek")

    def get_characters(self, text, charectrs):
        text_res = ""
        for c in text:
            if c in charectrs:
                text_res += c
                continue
            if c.isspace:
                text_res += " "
                continue

        text_res = " ".join(text_res.split())
        text_res = text_res.lower()
        return text_res

    def correct(self, text):
        return self.corrector.FixFragment(text)

    def to_infinitive(self, text):
        words = text.split(" ")
        words_infinitive = list(map(lambda w: self.morph.parse(w)[0].normal_form, words))
        text_res = " ".join(words_infinitive)
        return text_res

    def analyze(self, text):
        text = self.get_characters(text, self.simple_charectrs)
        text_rus = self.get_characters(text, self.rus_charectrs)

        text_corrected = self.correct(text_rus)
        text_infinitive = self.to_infinitive(text_corrected)

        text_res = text + " " + text_infinitive

        return text_res

    def AnalyzeText(self, request, context):
        text = request.text
        text = self.analyze(text)
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