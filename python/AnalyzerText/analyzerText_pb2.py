# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: analyzerText.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='analyzerText.proto',
  package='',
  syntax='proto3',
  serialized_options=_b('\n\014ru.memoscopeB\021AnalyzerTextProto'),
  serialized_pb=_b('\n\x12\x61nalyzerText.proto\"\"\n\x12\x41nalyzeTextRequest\x12\x0c\n\x04text\x18\x01 \x01(\t\"#\n\x13\x41nalyzeTextResponse\x12\x0c\n\x04text\x18\x01 \x01(\t2J\n\x0c\x41nalyzerText\x12:\n\x0b\x41nalyzeText\x12\x13.AnalyzeTextRequest\x1a\x14.AnalyzeTextResponse\"\x00\x42!\n\x0cru.memoscopeB\x11\x41nalyzerTextProtob\x06proto3')
)




_ANALYZETEXTREQUEST = _descriptor.Descriptor(
  name='AnalyzeTextRequest',
  full_name='AnalyzeTextRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='text', full_name='AnalyzeTextRequest.text', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=22,
  serialized_end=56,
)


_ANALYZETEXTRESPONSE = _descriptor.Descriptor(
  name='AnalyzeTextResponse',
  full_name='AnalyzeTextResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='text', full_name='AnalyzeTextResponse.text', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=58,
  serialized_end=93,
)

DESCRIPTOR.message_types_by_name['AnalyzeTextRequest'] = _ANALYZETEXTREQUEST
DESCRIPTOR.message_types_by_name['AnalyzeTextResponse'] = _ANALYZETEXTRESPONSE
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

AnalyzeTextRequest = _reflection.GeneratedProtocolMessageType('AnalyzeTextRequest', (_message.Message,), dict(
  DESCRIPTOR = _ANALYZETEXTREQUEST,
  __module__ = 'analyzerText_pb2'
  # @@protoc_insertion_point(class_scope:AnalyzeTextRequest)
  ))
_sym_db.RegisterMessage(AnalyzeTextRequest)

AnalyzeTextResponse = _reflection.GeneratedProtocolMessageType('AnalyzeTextResponse', (_message.Message,), dict(
  DESCRIPTOR = _ANALYZETEXTRESPONSE,
  __module__ = 'analyzerText_pb2'
  # @@protoc_insertion_point(class_scope:AnalyzeTextResponse)
  ))
_sym_db.RegisterMessage(AnalyzeTextResponse)


DESCRIPTOR._options = None

_ANALYZERTEXT = _descriptor.ServiceDescriptor(
  name='AnalyzerText',
  full_name='AnalyzerText',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=95,
  serialized_end=169,
  methods=[
  _descriptor.MethodDescriptor(
    name='AnalyzeText',
    full_name='AnalyzerText.AnalyzeText',
    index=0,
    containing_service=None,
    input_type=_ANALYZETEXTREQUEST,
    output_type=_ANALYZETEXTRESPONSE,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_ANALYZERTEXT)

DESCRIPTOR.services_by_name['AnalyzerText'] = _ANALYZERTEXT

# @@protoc_insertion_point(module_scope)
