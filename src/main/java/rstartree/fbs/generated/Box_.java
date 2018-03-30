// automatically generated, do not modify

package rstartree.fbs.generated;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Box_ extends Struct {
  public Box_ __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public float prob()   { return bb.getFloat(bb_pos + 0); }
  public float minX() { return bb.getFloat(bb_pos + 4); }
  public float minY() { return bb.getFloat(bb_pos + 8); }
  public float maxX() { return bb.getFloat(bb_pos + 12); }
  public float maxY() { return bb.getFloat(bb_pos + 16); }
  public float id() { return bb.getFloat(bb_pos + 20); }

  public static int createBox_(FlatBufferBuilder builder,float id, float minX, float minY, float maxX, float maxY, float prob) {
    builder.prep(4, 24);
    builder.putFloat(id);
    builder.putFloat(maxY);
    builder.putFloat(maxX);
    builder.putFloat(minY);
    builder.putFloat(minX);
    builder.putFloat(prob);
    return builder.offset();
  }
};

