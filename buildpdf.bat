@rem Set the JAVA_HOME variable
@set JAVA_HOME=c:\program files\java\jdk1.5.0\

@set DOCLET=com.tarsec.javadoc.pdfdoclet.PDFDoclet
@set JARS=javadoc/PDFdoclet/pdfdoclet-0.8.0-all.jar
@md dist\javadoc_pdf
@set PDF=dist/javadoc_pdf/CircuitSandbox.pdf
@set CFG=cs_pdfdoclet.cfg
@set SRC=src

@javadoc -doclet %DOCLET% -docletpath %JARS% -pdf %PDF% -config %CFG% -sourcepath %SRC% @packages

pause