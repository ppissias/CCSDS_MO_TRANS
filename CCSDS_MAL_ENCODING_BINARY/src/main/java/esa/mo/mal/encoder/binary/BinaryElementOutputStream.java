/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Binary encoder
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
package esa.mo.mal.encoder.binary;

import esa.mo.mal.encoder.gen.GENElementOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Element;

/**
 * Implements the MALElementInputStream interface for a binary encoding.
 */
public class BinaryElementOutputStream implements GENElementOutputStream
{
  private final OutputStream dos;
  private BinaryEncoder enc = null;

  /**
   * Constructor.
   *
   * @param os Output stream to write to.
   */
  public BinaryElementOutputStream(final OutputStream os)
  {
    this.dos = os;
  }

  @Override
  public void writeElement(final Object element, final MALEncodingContext ctx) throws MALException
  {
    if (null == enc)
    {
      this.enc = createBinaryEncoder(dos);
    }

    if ((null != ctx) && (element == ctx.getHeader()))
    {
      ((Element) element).encode(enc);
    }
    else
    {
      enc.encodeNullableElement((Element) element);
    }
  }

  @Override
  public void flush() throws MALException
  {
    try
    {
      dos.flush();
    }
    catch (IOException ex)
    {
      throw new MALException("IO exception flushing Element stream", ex);
    }
  }

  @Override
  public void close() throws MALException
  {
    try
    {
      dos.close();
      enc.close();
    }
    catch (IOException ex)
    {
      throw new MALException(ex.getLocalizedMessage(), ex);
    }
  }

  /**
   * Over-ridable factory function.
   *
   * @param os Output stream to wrap.
   * @return the new encoder.
   */
  protected BinaryEncoder createBinaryEncoder(OutputStream os)
  {
    return new BinaryEncoder(os);
  }
}
