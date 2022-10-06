## ProgramTransformer

This folder contains the following updated program transformation tools:

- [JavaMethodTransformer](https://github.com/mdrafiqulrabin/JavaTransformer)
- [CSharpMethodTransformer](https://github.com/mdrafiqulrabin/CSharpTransformer)

---

## How to Run:

Execute **(\*.sh)** files to run the corresponding program transformation tool. It takes a directory of original methods as input (i.e., sample methods from the data folder), applies a set of method-level transformations to all methods, and saves all transformed methods to an output directory (i.e., transformed methods to the results folder).

```
$ source JavaTransformer.sh
$ source CSharpTransformer.sh
```

For more details, check the reproducible capsule from [CodeOcean](https://codeocean.com/capsule/2958348/tree/v1).

---

## Transformation Operators: ([Examples](https://github.com/mdrafiqulrabin/tnpa-framework#type-of-transformations))

- Variable Renaming - renames the name of a variable as `var`.
- Log Statement - add a log statement at a random place.
- Loop Exchange - replaces `for` loops with `while` loops or vice versa.
- Permute Statement - swaps two independent statements in a basic block.
- Reorder Condition - reorder a binary condition.
- Switch to If - replaces a `switch` statement with an equivalent `if` statements.
- Try Catch - add a try-catch block at a random statement.
- Unused Statement - inserts a dead code at a random place.
- Boolean Exchange - switches the value of a `boolean` variable and propagates this change.

