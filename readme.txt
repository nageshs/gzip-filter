Building the GZIP filter from src
----------------------------------


GZIP Filter Usage :
-------------------
Add the gzip-filter.jar included in this distribution 
into your war's WEB-INF/lib directory. Register the gzip 
filter in your web.xml as shown in the WEB-INF/web.xml 

i.e. 

 <filter>
  <filter-name>GZIPFilter</filter-name>
  <filter-class>nagiworld.net.filters.gzip.GZIPFilter</filter-class>
 </filter>

Mapping the Filter : 
--------------------
        Map all resources which can benefit from compression such as
.txt, .log, .html and .htm. You can also use the filter to compress output
 from jsps and other dynamic content. Typically compressing certain image 
types does not prove to be advantageous since they are already compressed
so make sure they are not mapped to the gzipfilter.

Example : also shown in the accompanying web.xml 

 <filter-mapping>
   <filter-name>GZIPFilter</filter-name>
   <url-pattern>*.html</url-pattern>
 </filter-mapping>

 <filter-mapping>
   <filter-name>GZIPFilter</filter-name>
   <url-pattern>*.jsp</url-pattern>
 </filter-mapping>

Building the gzip-filter.jar
----------------------------
        The build.xml provided in the root directory of the gzipfilter can be used to compile and create the gzip-filter.jar. Run the following command to create a jar file:

ant jar

This creates a jar called gzip-filter.jar in the WEB-INF/lib directory. Please ensure that the servlet-api.jar is present in the $CLASSPATH

Enjoy!
Please post your feedback or comments to www.nagiworld.net. 
