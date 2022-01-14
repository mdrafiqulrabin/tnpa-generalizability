# JavaMethodExtractor
Convert java files into single method-only files.

---

### Create Jar file with Maven:
```
$ mvn clean compile assembly:single
# Output: target/jar/JavaMethodExtractor.jar
```

### Given input and output path, execute jar:
  ```
  # input_path  = Input directory to the original files.
  # output_path = Output directory to the extracted methods.
  $ java -jar JavaMethodExtractor.jar "input_path" "output_path"
  ```
