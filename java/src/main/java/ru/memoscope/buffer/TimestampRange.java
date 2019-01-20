package ru.memoscope.buffer;

public class TimestampRange {

  public final long minTimestamp;

  public final long maxTimestamp;

  public TimestampRange(long min, long max) {
    minTimestamp = min;
    maxTimestamp = max;
  }
}
