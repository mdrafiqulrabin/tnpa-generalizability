#!/bin/bash

input_path="./data/sample_java/"
output_path="./results/sample_java/"

echo "Running JavaTransformer on ${input_path}"
rm -rf ${output_path}
java -jar code/JavaTransformer/target/jar/JavaTransformer.jar ${input_path} ${output_path}
echo "Saved transformed java methods at ${output_path}"
