/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO String encoder
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
package esa.mo.mal.encoder.string;

import esa.mo.mal.encoder.gen.GENElementInputStream;
import java.io.InputStream;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Element;

/**
 * Implements the MALElementInputStream interface for String encodings.
 */
public class StringElementInputStream implements GENElementInputStream
{
  private final StringDecoder dec;

  /**
   * Constructor.
   *
   * @param is Input stream to read from.
   */
  public StringElementInputStream(final InputStream is)
  {
    dec = new StringDecoder(is);
  }

  @Override
  public Object readElement(final Object element, final MALEncodingContext ctx)
          throws IllegalArgumentException, MALException
  {
    return dec.decodeNullableElement((Element) element);
  }

  @Override
  public byte[] getRemainingEncodedData() throws MALException
  {
    return dec.getRemainingEncodedData();
  }

  @Override
  public void close() throws MALException
  {
  }
}
