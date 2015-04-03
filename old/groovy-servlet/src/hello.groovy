import java.util.Date

if (session == null) {
  session = request.getSession(true);
}

if (session.counter == null) {
  session.counter = 1
}

println """
<html>
    <head>
        <title>Groovy Servlet</title>
    </head>
    <body>
Hello, ${request.remoteHost}: ${session.counter}! ${new Date()}
    </body>
</html>
"""
session.counter = session.counter + 1



