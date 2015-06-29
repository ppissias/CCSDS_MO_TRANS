/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO SPP Transport Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.mal.encoder.spp;

import java.math.BigInteger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Implements the MALDecoder interface for a SPP binary encoding.
 */
public class SPPBinaryDecoder extends esa.mo.mal.encoder.binary.fixed.FixedBinaryDecoder
{
  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   */
  public SPPBinaryDecoder(final byte[] src)
  {
    super(new SPPBufferHolder(null, src, 0, src.length));
  }

  /**
   * Constructor.
   *
   * @param is Input stream to read from.
   */
  public SPPBinaryDecoder(final java.io.InputStream is)
  {
    super(new SPPBufferHolder(is, null, 0, 0));
  }

  /**
   * Constructor.
   *
   * @param src Byte array to read from.
   * @param offset index in array to start reading from.
   */
  public SPPBinaryDecoder(final byte[] src, final int offset)
  {
    super(new SPPBufferHolder(null, src, offset, src.length));
  }

  /**
   * Constructor.
   *
   * @param src Source buffer holder to use.
   */
  protected SPPBinaryDecoder(final BufferHolder src)
  {
    super(src);
  }

  @Override
  public org.ccsds.moims.mo.mal.MALListDecoder createListDecoder(final java.util.List list) throws MALException
  {
    return new SPPBinaryListDecoder(list, sourceBuffer);
  }

  @Override
  public Blob decodeBlob() throws MALException
  {
    return new Blob(sourceBuffer.get(sourceBuffer.getSignedShort()));
  }

  @Override
  public Blob decodeNullableBlob() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeBlob();
    }

    return null;
  }

  @Override
  public ULong decodeULong() throws MALException
  {
    byte[] buf =
    {
      0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    System.arraycopy(sourceBuffer.get(8), 0, buf, 1, 8);

    return new ULong(new BigInteger(buf));
  }

  @Override
  public ULong decodeNullableULong() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeULong();
    }

    return null;
  }

  @Override
  public Time decodeTime() throws MALException
  {
    long s = sourceBuffer.getUnsignedLong32() * 1000;
    sourceBuffer.checkBuffer(3);
    final int i = sourceBuffer.shiftOffsetAndReturnPrevious(3);

    byte[] b = new byte[4];
    b[0] = 0;
    b[1] = sourceBuffer.getBuf()[i];
    b[2] = sourceBuffer.getBuf()[i + 1];
    b[3] = sourceBuffer.getBuf()[i + 2];
    int ms = java.nio.ByteBuffer.wrap(b).getInt();

    s += ms;
    return new Time(s);
  }

  @Override
  public Time decodeNullableTime() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeTime();
    }

    return null;
  }

  @Override
  public FineTime decodeFineTime() throws MALException
  {
    long s = sourceBuffer.getUnsignedLong32() * 1000;
    sourceBuffer.checkBuffer(3);
    final int i = sourceBuffer.shiftOffsetAndReturnPrevious(3);
    int ms = java.nio.ByteBuffer.wrap(sourceBuffer.getBuf(), i, 3).getInt();

    s += ms;
    return new FineTime(s);
  }

  @Override
  public FineTime decodeNullableFineTime() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeFineTime();
    }

    return null;
  }

  @Override
  public Duration decodeDuration() throws MALException
  {
    long s = sourceBuffer.getUnsignedLong32() * 1000;
    sourceBuffer.checkBuffer(3);
    final int i = sourceBuffer.shiftOffsetAndReturnPrevious(3);
    int ms = java.nio.ByteBuffer.wrap(sourceBuffer.getBuf(), i, 3).getInt();

    s += ms;
    return new Duration((int) s);
  }

  @Override
  public Duration decodeNullableDuration() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeDuration();
    }

    return null;
  }

  @Override
  public String decodeNullableString() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeString();
    }

    return null;
  }

  @Override
  public URI decodeNullableURI() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeURI();
    }

    return null;
  }

  @Override
  public Identifier decodeNullableIdentifier() throws MALException
  {
    if (sourceBuffer.getBool())
    {
      return decodeIdentifier();
    }

    return null;
  }

  /**
   * Extends the fixed length internal buffer holder to cope with the smaller size of the size field for Strings in SPP
   * packets.
   */
  protected static class SPPBufferHolder extends FixedBufferHolder
  {
    /**
     * Constructor.
     *
     * @param is Input stream to read from.
     * @param buf Source buffer to use.
     * @param offset Buffer offset to read from next.
     * @param length Length of readable data held in the array, which may be larger.
     */
    public SPPBufferHolder(final java.io.InputStream is, final byte[] buf, final int offset, final int length)
    {
      super(is, buf, offset, length);
    }

    @Override
    public String getString() throws MALException
    {
      final int len = getSignedShort();
      if (len >= 0)
      {
        checkBuffer(len);

        final String s = new String(buf, offset, len, UTF8_CHARSET);
        offset += len;
        return s;
      }
      return null;
    }
  }
}
