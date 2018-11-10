package ru.memoscope.buffer;

public class TimestampRange {

  public long minTimestamp;

  public long maxTimestamp;

  public TimestampRange(long min, long max) {
    minTimestamp = min;
    maxTimestamp = max;
  }
}
