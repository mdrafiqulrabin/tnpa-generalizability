# JavaTransformer

### Create Jar file with Ant
$ mvn dependency:copy-dependencies \
$ ant jar \
Output: build/jar/JavaTransformer.jar

### Create Jar file with Maven
$ mvn clean compile assembly:single \
Output: target/jar/JavaTransformer.jar
