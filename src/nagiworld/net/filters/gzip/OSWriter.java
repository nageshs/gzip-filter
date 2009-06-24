/**
 * Copyright 2006-2007 Nagesh Susarla.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package nagiworld.net.filters.gzip;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.StreamEncoder;


class OSWriter extends Writer {

  private final StreamEncoder se;

  public OSWriter(OutputStream out, String charsetName)
    throws UnsupportedEncodingException
  {
    super(out);
    if (charsetName == null) throw new NullPointerException("charsetName");
    se = StreamEncoder.forOutputStreamWriter(out, this, charsetName);
  }

  public String getEncoding() {
    return se.getEncoding();
  }

  private void flushBuffer() throws IOException {
    se.flushBuffer();
  }


  public void write(char cbuf[], int off, int len) throws IOException {
    se.write(cbuf, off, len);
    flushBuffer();
  }

  public void write(int c) throws IOException {
    se.write(c);
    flushBuffer();
  }

  public void write(String str, int off, int len) throws IOException {
    se.write(str, off, len);
    flushBuffer();
  }

  public void flush() throws IOException {
    se.flush();
  }

  public void close() throws IOException {
    se.close();
  }

}
