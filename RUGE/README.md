-----------------------------------------------------------------------
-- CODE USED FOR KNOWLEDGE GRAPH EMBEDDING WITH ITERATIVE GUIDANCE FROM SOFT RULES--
-----------------------------------------------------------------------

------------------
OUTLINE:
1. Introduction
2. Preprocessing
3. Training
4. Testing
5. How to cite
6. Contact
------------------


------------------
1. INTRODUCTION:
------------------

The codes in the folder RUGE/ are used for Rule-Guided Embedding (RUGE). 
We peovide how to run the experiments of Link Prediction task in the following.


------------------
2. PREPROCESSING
------------------

To run the experiments, you need to preprocess datasets in the folder datasets/ following the steps below:

  (1) Change data form: call the program ConvertDataForm.java in the folder src/basic/dataProcess to convert the string form of original data into digital form, and get resultant files train/valid/test.txt in the folder datasets/

  (2) Propositionalize soft rules: call the program GroundAllRules.java in folder src\basic\dataProcess to propositionalize soft rules in the folder datasets/, and get a resultant file groundings.txt


------------------
3. TRAINING
------------------
To train a model, you need to follow the steps below:

  (1) Export Program_RUGE.java in the folder src/ruge/train as runnable JAR file, for example, termed as RUGE.jar

  (2) Call the program RUGE.jar with parameters, for example, as follows:

java -jar RUGE.jar -train datasets\\fb15k\\train.txt -valid datasets\\fb15k\\valid.txt -test datasets\\fb15k\\test.txt -rule datasets\\fb15k\\groundings.txt -m 1345 -n 14951 -k 200 -d 0.01 -c 0.01 -ne 10 -ge 0.5 -gr 0.5 -# 1000 -skip 50

or

java -jar RUGE.jar -train datasets\\yago37\\train.txt -valid datasets\\yago37\\valid.txt -test datasets\\yago37\\test.txt -rule datasets\\yago37\\groundings.txt -m 37 -n 123189 -k 150 -d 0.003 -c 0.01 -ne 10 -ge 1.0 -gr 0.5 -# 1000 -skip 50

You can also change the parameters when running RUGE.jar:
  - train: the path of training triples 
  - valid: the path of validate triples 
  - test: the path of testing triples 
  - rule: the path of grounded rules
  - m: number of relations 
  - n: number of entities 
  - k: embedding dimensionality 
  - d: L2 regularization coefficient 
  - c: slacking penalty 
  - ne: number of negatives per positive triple (default 2)\n"
  - ge: learning rate of matrix E (default 0.01)\n"
  - gr: learning rate of tensor R (default 0.01)\n"
  - #: number of iterations (default 1000)\n"
  - skip: number of skipped iterations (default 50)\n\n"

The program will train a model with the input parameters, and output 5 files:
  - result.log: the specification document
  - MatrixE.real.best: the real part of entity embeddings
  - MatrixR.real.best: the real part of realtion embeddings
  - MatrixE.imag.best: the imaginary part of entity embeddings
  - MatrixR.imag.best: the imaginary part of realtion embeddings

To prevent results overwriting with different parameters, please kindly set corresponding file paths in the source code (RUGEModel.java). 
 
  
------------------
4. TESTING
------------------
To evaluate on the test datasets, you need call the program Eval_LinkPrediction.java in the folder src/test

You can also change the input parameters when running:
  - iEntities: number of entities
  - iRelations: number of relations
  - iFactors: embedding dimensionality
  - fnRealMatrixE: the file path of the real part of entity embeddings
  - fnRealMatrixR: the file path of the real part of realtion embeddings
  - fnImagMatrixE: the file path of the imaginary part of entity embeddings
  - fnImagMatrixR: the file path of the imaginary part of realtion embeddings
  - fnTrainTriples: the file path of training data (digital form)
  - fnValidTriples: the file path of validation data (digital form)
  - fnTestTriples: the file path of testing data (digital form)

The program will evaluate on testing data, and report the metrics of MRR, MED, and Hits@N.


------------------
5. HOW TO CITE
------------------

When using this code and dataset, one should cite the original paper:  
@inproceedings{guo2018:RUGE,  
 title     = {Knowledge Graph Embedding with Iterative Guidance from Soft Rules},  
 author    = {Shu Guo and Quan Wang and Lihong Wang and Bin Wang and Li Guo},  
 booktitle = {Proceedings of the Thirty-Second AAAI Conference on Artificial Intelligence},  
 year      = {2018}<br> 
}


------------------  
6. CONTACT
------------------

For all remarks or questions please contact Quan Wang:
wangquan (at) iie (dot) ac (dot) cn .


