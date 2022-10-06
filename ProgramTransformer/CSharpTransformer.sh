#!/bin/bash

input_path="./data/sample_csharp/"
output_path="./results/sample_csharp/"

echo "Running CSharpTransformer on ${input_path}"
rm -rf ${output_path}
dotnet run --project=code/CSharpTransformer/CSharpTransformer.csproj ${input_path} ${output_path}
echo "Saved transformed c# methods at ${output_path}"
