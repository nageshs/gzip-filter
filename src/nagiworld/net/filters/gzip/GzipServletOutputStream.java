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
import java.io.OutputStream;

import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

class GzipServletOutputStream extends ServletOutputStream {
  
  private final static boolean debug = false;

  private GZIPOutputStream os;

  private ServletOutputStream sos;
  
  private HttpServletResponse response;


  public GzipServletOutputStream(HttpServletResponse response) 
    throws IOException {
    this.response = response;
  }

  public void write(int i) throws IOException {
    //    p("write(int i): " + (char)i);
    init();
    os.write(i);
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    //    p("write(byte[] b, int off, int len) : " + new String(b, off, len));
    init();
    os.write(b, off, len);
  }
  
  public void finish() throws IOException {
    //    p("finish");
    if (os != null) {
      os.flush();
      os.finish();
    }
  }

  public void flush() throws IOException {
    //p("flush");
    if (os != null) {
      os.flush();
    }
    if (sos != null) {
      sos.flush();
    }
  }

  public void close() throws IOException {
    //    p("close");
    // no-op
  }

  public void reset() {
    //??
    if (debug) p("reset");
    os = null;
  }

  private void init() throws IOException {
    if (os != null) return;

    response.addHeader("Content-Encoding", "gzip");
    sos = response.getOutputStream();

    if (sos instanceof weblogic.servlet.internal.ServletOutputStreamImpl) {

      os = new GZIPOutputStream(new OutputStream() {

          public void write(int i) throws IOException {  sos.write(i);  }

          public void write(byte[] b, int off, int len) throws IOException {
            sos.write(b, off, len); 
          }

          public void flush() throws IOException {
            /* Intentional no-op to handle the http 1.0 gzip response */
          }

          public void close() throws IOException { /* Intentional no - op */ }

        });

    } else {
      os = new GZIPOutputStream(sos);
    }

    if (debug) System.out.println("init called ");

  }

  private static void p(String out) {
    System.out.println("[GzipServletOutputStream]" + out);
  }

}
