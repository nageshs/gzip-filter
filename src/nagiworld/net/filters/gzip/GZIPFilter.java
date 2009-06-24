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
import java.util.Enumeration;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

public class GZIPFilter implements Filter {
  
  private final static boolean debug = false;

  private static final String WL_GZIP_FILTER = "nagiworld.net.filters.gzip";
  private FilterConfig config;
  
  public void init(FilterConfig cfg) {
    config = cfg;
    // Extract any config parameters 
  }

  /** 
   * In 6.1, WebLogic uses the old servlet2.3 pfd2 
   * interfaces which had the following methods.
   * Hence include them to ensure that the filter
   * works with both 6.x as well as 7.x
   */
  private FilterConfig fc;
  public void setFilterConfig(FilterConfig cfg) {
    try {
      fc = cfg;
      init(cfg);
    } catch (Exception ex){}
  }
  public FilterConfig getFilterConfig() {
    return fc;
  }

  
  public void doFilter(ServletRequest request, ServletResponse response, 
                       FilterChain chain) 
                       throws IOException, ServletException {
    if (debug) p("doFilter start");

    long t = System.currentTimeMillis();

    // Case 1: Let non-http request just pass through
    if (!(response instanceof HttpServletResponse) || 
        !(request instanceof HttpServletRequest)) {
      chain.doFilter(request, response);
      return;
    }

    HttpServletResponse res = (HttpServletResponse)response;
    HttpServletRequest req  = (HttpServletRequest)request;
    
    // Case2 : Already filtered ?
    if (req.getAttribute(WL_GZIP_FILTER) != null) {
      chain.doFilter(request, response);
      return;
    }

    req.setAttribute(WL_GZIP_FILTER, "true");

    boolean needsCompression = false;
    for (Enumeration enum_ = req.getHeaders("Accept-Encoding"); 
         enum_.hasMoreElements();) {
      String name = (String) enum_.nextElement();
      if (debug) p("Accept-Encoding: name :" + name);
      if (name.indexOf("gzip") != -1) {
        needsCompression = true;
      }
    }
    
    // Case 3: Request doesnt support gzip 
    if (!needsCompression) {
      chain.doFilter(request, response);
      return;
    }
    
    String requestURI = req.getRequestURI();
    
    GZIPResponseWrapper wrapper = new GZIPResponseWrapper(res);
    try {
      chain.doFilter(request, wrapper);
    } finally {
      wrapper.finish();
    }

    if (debug) p("Compressed : " + requestURI + " in  " + 
                 (System.currentTimeMillis() - t) + "ms");
  }

  
  public void destroy() {
    config = null;
  }

  private static void p(String out) {
    System.out.println("[GZIPFilter]" + out);
  }

}
