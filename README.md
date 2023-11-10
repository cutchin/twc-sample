# Example File Upload Servlet

There is not much to this project. Just a simple servlet that accepts a file upload via multipart form content, and a very
primitive HTML page that allows you to choose and upload a file.

It does not use a `web.xml` file, instead allowing the servlet to configure itself via an annotation:

```java
@MultipartConfig(maxFileSize = Integer.MAX_VALUE)
@WebServlet(urlPatterns = "/upload")
public class UploadServlet extends HttpServlet {
```

Note that there are several ways to limit the max upload size of a servlet.

## Global Config via the Undertow subsystem

You can run this via the JBoss CLI to change the system max value. Note that this is the absolute max POST size allowed to the server,
so even if your servlet allows a higher value, it cannot exceed whatever is configured system-wide. 

```
/subsystem=undertow/server=default-server/http-listener=default/:write-attribute(name=max-post-size,value=80000000)
```

## Per-servlet configuration

### Via @MultipartConfig
See above - it's just an annotation on your servlet.

### Via web.xml

Red Hat documents this here:
https://access.redhat.com/solutions/3354401

Oracle does as well:
https://docs.oracle.com/javaee/6/tutorial/doc/gmhal.html

Nose that both of the above documents also cover the annotation method.

### Via Spring

This is documented here:
https://spring.io/guides/gs/uploading-files/

See the "Tuning File Upload Limits" section. Note that Spring does not really implement this itself, it just passes the
limits to the servlet engine when it registers its own servlets.

### Via manual servlet registration

This is much a less likely scenario. If you register a servlet manually via the `ServletContext` you get a `ServletRegistration.Dynamic`
object that you can use to configure multipart limits.

https://docs.oracle.com/javaee/7/api/javax/servlet/ServletContext.html

## Relevant Error Messages

There are two error messages you may see related to this issue.

### UT005023

This error message means you have exceeded the system-wide max POST limit. You need to configure JBoss/Wildfly to accept a
larger POST size:
```
16:51:01,747 ERROR [io.undertow.request] (default task-2) UT005023: Exception handling request to /twc-sample/upload: java.lang.IllegalStateException: io.undertow.server.RequestTooBigException: UT000020: Connection terminated as request was larger than 10485760
```

### UT000054

This error indicates that the servlet itself has placed a limit on its max multipart file upload size. See the numerous methods above
to increase the servlet's max upload size.

```
java.lang.IllegalStateException: io.undertow.server.handlers.form.MultiPartParserDefinition$FileTooLargeException: UT000054: The maximum size 100000 for an individual file in a multipart request was exceeded
```

