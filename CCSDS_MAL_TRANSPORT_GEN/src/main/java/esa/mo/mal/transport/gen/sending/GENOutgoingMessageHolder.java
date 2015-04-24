/* ----------------------------------------------------------------------------
 * Copyright (C) 2014      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Generic Transport Framework
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
package esa.mo.mal.transport.gen.sending;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import static esa.mo.mal.transport.gen.GENTransport.LOGGER;

/**
 * This class holds the message to be sent in encoded format and a reply queue that the internal sender of the data can
 * listen to in order to be informed if the message was successfully sent or not.
 *
 */
public class GENOutgoingMessageHolder
{
  //reply queue
  private final BlockingQueue<Boolean> replyQueue;

  //the raw data
  private final byte[] data;

  /**
   * Will construct a new object and create a new internal reply queue
   *
   * @param data the data to be sent
   */
  public GENOutgoingMessageHolder(byte[] data)
  {
    this.data = data;
    replyQueue = new LinkedBlockingQueue<Boolean>();
  }

  /**
   * This method blocks until there is an attempt to send the data.
   *
   * @return TRUE if the data was successfully sent and FALSE if there was a communication or internal problem.
   * @throws InterruptedException in case of shutting down or internal error
   */
  public Boolean getResult() throws InterruptedException
  {
    return replyQueue.take();
  }

  /**
   * Sets the result indicating if the data was sent successfully.
   *
   * @param result TRUE if the data was successfully sent and FALSE if there was a communication or internal problem.
   */
  public void setResult(Boolean result)
  {
    boolean inserted = replyQueue.add(result);
    if (!inserted)
    {
      // log error. According to the specification (see *add* call
      // documentation) this will always return true, or throw an
      // exception
      LOGGER.log(Level.SEVERE, "Could not insert result to processing queue", new Throwable());
    }
  }

  /**
   * Getter for the data to be sent
   *
   * @return
   */
  public byte[] getData()
  {
    return data;
  }
}