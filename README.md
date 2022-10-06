## Semantic-Preserving Program Transformations

This project contains the program transformation tool and the datasets of transformed programs for the paper 'On the Generalizability of Neural Program Models with respect to Semantic-Preserving Program Transformations' ([arXiv](https://arxiv.org/abs/2008.01566), [ScienceDirect](https://doi.org/10.1016/j.infsof.2021.106552)) accepted at the [IST Journal, Elsevier 2021](https://www.journals.elsevier.com/information-and-software-technology) and presented at the [SANER-JFT 2022](https://saner2022.uom.gr/journalFirstTrack).

Reproducible Capsule of ProgramTransformer:
<ul>
  <li> CodeOcean: https://codeocean.com/capsule/2958348/tree/v1 </li>
</ul>

---

## Structure

```
├── NPM-IST21                 # Artifact of our paper.
  ├── JavaMethodExtractor       # code for extracting single java methods.
  ├── JavaMethodTransformer     # code for semantic program transformations.
  ├── images                    # some figures used in the paper.
  ├── results                   # data and plots used in the paper.
├── ProgramTransformer        # Updated program transformation tools.
  ├── code                      # run Java and C# tools.
  ├── data                      # sample original methods.
  ├── results                   # generated transformed methods.
``` 

---

## Approach

|<img src="./NPM-IST21/images/workflow.png" width="600" alt="Evaluation of Generalizability"/>|
:-------------------------:
|The workflow of our generalizability evaluation.|

---

# Citation:

[On the Generalizability of Neural Program Models with respect to Semantic-Preserving Program Transformations](https://doi.org/10.1016/j.infsof.2021.106552)

```
@article{rabin2021generalizability,
  title = {On the generalizability of Neural Program Models with respect to semantic-preserving program transformations},
  author = {Md Rafiqul Islam Rabin and Nghi D.Q. Bui and Ke Wang and Yijun Yu and Lingxiao Jiang and Mohammad Amin Alipour},
  journal = {Information and Software Technology (IST)},
  volume = {135},
  pages = {106552},
  year = {2021},
  issn = {0950-5849},
  doi = {https://doi.org/10.1016/j.infsof.2021.106552},
  url = {https://www.sciencedirect.com/science/article/pii/S0950584921000379}
}
```

---

## Updated Program Transformation Tools:
- [JavaMethodTransformer](https://github.com/mdrafiqulrabin/JavaTransformer)
- [CSharpMethodTransformer](https://github.com/mdrafiqulrabin/CSharpTransformer)

---

## Related Articles:

- Testing Neural Program Analyzers [[arXiv](https://arxiv.org/abs/1908.10711), [GitHub](https://github.com/mdrafiqulrabin/tnpa-framework)]
- Evaluation of Generalizability of Neural Program Analyzers [[arXiv](https://arxiv.org/abs/2004.07313), [GitHub](https://github.com/mdrafiqulrabin/tnpa-evaluation)]
