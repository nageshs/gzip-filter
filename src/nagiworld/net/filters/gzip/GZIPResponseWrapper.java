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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.ServletOutputStream;

class GZIPResponseWrapper extends HttpServletResponseWrapper {

  private static final boolean debug = false;

  private ServletOutputStream stream;
  private PrintWriter writer;
  private GzipServletOutputStream gstream;
  private final HttpServletResponse response;

  public GZIPResponseWrapper(HttpServletResponse response) {
    super(response);
    this.response = response;
  }
  
  public void setContentLength(int len) {
    if (debug) p("Tried to set Content-Length to : " + len);
    // no-op
  }

  public ServletOutputStream getOutputStream() throws IOException {
    if (writer != null) 
      throw new IllegalStateException("strict servlet API : cannot call getOutputStream after getWriter() ");
    if (stream == null) 
      stream = createOutputStream();
    return stream;
  }

  public PrintWriter getWriter() throws IOException {
    if (stream != null) 
      throw new IllegalStateException("strict servlet API : cannot call "
                                      + "getWriter after getOutputStream() ");
    if (writer == null) {
      writer = new PrintWriter(new OSWriter(createOutputStream(), response.getCharacterEncoding()));
    }
    return writer;
  }
  
  public void flushBuffer() throws IOException {
    if (debug) p("flushBuffer");
    if (gstream == null) return;
    gstream.flush();
    super.flushBuffer();
  }
  
  public void resetBuffer() {
    if (debug) p("resetBuffer");
    super.resetBuffer();
    if (gstream != null) {
      gstream.reset();
    }
  }
  
  /** impl specific methods **/
  /*package*/ void finish() throws IOException {
    if (gstream == null) return;
    ((GzipServletOutputStream)gstream).finish();
  }

  private ServletOutputStream createOutputStream() throws IOException {
    if (gstream == null) 
      gstream = new GzipServletOutputStream(response);
    return (ServletOutputStream)gstream;
  }

  static void p(String out) {
    System.out.println("[GZIPResponseWrapper]" + out);
  }
}
