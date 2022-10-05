# JavaMethodTransformer
Apply semantic transformations on Java methods.

---

### Create Jar file with Maven:
```
$ mvn clean compile assembly:single
# Output: target/jar/JavaMethodTransformer.jar
```

### Given input and output path, execute jar:
  ```
  # input_path  = Input directory to the original programs.
  # output_path = Output directory to the transformed programs.
  $ java -jar JavaMethodTransformer.jar "input_path" "output_path"
  ```
