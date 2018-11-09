CXX = g++
CXXFLAGS = -std=c++11 -O2 -Wall -Wextra -g -Iinclude `pkg-config --cflags opencv` `pkg-config --cflags lept` `pkg-config --cflags tesseract` `pkg-config --cflags protobuf grpc`
LDFLAGS = `pkg-config --libs opencv` `pkg-config --libs lept` `pkg-config --libs tesseract` -lpthread
LDFLAGS += -L/usr/local/lib `pkg-config --libs protobuf grpc++ grpc`\
           -Wl,--no-as-needed -lgrpc++_reflection -Wl,--as-needed\
           -ldl

EXE = main
SRCDIR = src
PROTOSDIR = ../../protos
GENDIR = generated
BINDIR = bin

PROTOC = protoc
GRPC_CPP_PLUGIN = grpc_cpp_plugin
GRPC_CPP_PLUGIN_PATH ?= `which $(GRPC_CPP_PLUGIN)`

GENERATE_СС = $(patsubst $(PROTOSDIR)/%.proto,$(GENDIR)/%.pb.cc,$(wildcard $(PROTOSDIR)/*.proto))
GENERATE_СС += $(patsubst $(PROTOSDIR)/%.proto,$(GENDIR)/%.grpc.pb.cc,$(wildcard $(PROTOSDIR)/*.proto))
GENERATE_H = $(patsubst $(GENDIR)/%.cc,$(GENDIR)/%.h,$(GENERATE_СС))
GENERATE_O = $(patsubst $(GENDIR)/%.cc,$(BINDIR)/%.o,$(GENERATE_СС))
OBJECTS = $(patsubst $(SRCDIR)/%.cpp,$(BINDIR)/%.o,$(wildcard $(SRCDIR)/*.cpp))
OBJECTS_ALL = $(GENERATE_O)
OBJECTS_ALL += $(OBJECTS)

all: $(EXE)

$(EXE): $(GENDIR) $(BINDIR) $(GENERATE_СС) $(OBJECTS_ALL)
	$(CXX) $(OBJECTS_ALL) -o $(EXE) $(LDFLAGS)

$(BINDIR)/%.pb.o: $(GENDIR)/%.pb.cc
	$(CXX) $(CXXFLAGS) -c -MMD -o $@ $<

$(BINDIR)/%.o: $(SRCDIR)/%.cpp
	$(CXX) $(CXXFLAGS) -c -MMD -o $@ $<

$(GENDIR):
	mkdir -p $(GENDIR)

$(BINDIR):
	mkdir -p $(BINDIR)

.PRECIOUS: $(GENDIR)/%.grpc.pb.cc
$(GENDIR)/%.grpc.pb.cc: $(PROTOSDIR)/%.proto
	$(PROTOC) -I $(PROTOSDIR) --grpc_out=$(GENDIR) --plugin=protoc-gen-grpc=$(GRPC_CPP_PLUGIN_PATH) $<

.PRECIOUS: $(GENDIR)/%.pb.cc
$(GENDIR)/%.pb.cc: $(PROTOSDIR)/%.proto
	$(PROTOC) -I $(PROTOSDIR) --cpp_out=$(GENDIR) $<

include $(wildcard $(BINDIR)/*.d)

run:
	./$(EXE)

me_happy: all run

clean:
	rm -rf $(GENDIR) $(BINDIR) $(EXE)

.PHONY: clean all
