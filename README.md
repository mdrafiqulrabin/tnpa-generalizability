# JavaMethodTransformer


### Create Jar file with Maven
$ mvn clean compile assembly:single \
Output: target/jar/JavaMethodTransformer.jar

### Call JavaMethodTransformer
$ java -jar JavaMethodTransformer.jar <.../methods/> <.../transforms/> \
args[0] = Input directory to original methods. \
args[1] = Output directory to transformed methods. \
