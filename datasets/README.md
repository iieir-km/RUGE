-------------------------------------------------------------------------------------
-- DATA USED FOR KNOWLEDGE GRAPH EMBEDDING WITH ITERATIVE GUIDANCE FROM SOFT RULES--
-------------------------------------------------------------------------------------

------------------
OUTLINE:
1. Introduction
2. Content
3. Data Format
4. Data Statistics
5. How to Cite
6. Contact
------------------


------------------
1. INTRODUCTION:
------------------

There are two datasets FB15K and YAGO37 used for Rule-Guided Embedding (RUGE). 
FB15K is a subgraph of Freebase containing 1,345 relations and 14,951 entities. YAGO37 is extracted from the core facts of YAGO3, contaning 37 relations and 123,189 entities.


------------------
2. CONTENT:
------------------

The data archive contains 1 README file + 2 folders:
  - README: the specification document
  - Folder fb15k: the FB15K data set
  - Folder yago37: the YAGO37 data set

Each folder contains 4 files:
  - {dataset}_triples.train
  - {dataset}_triples.valid
  - {dataset}_triples.test
  - {dataset}_rule
  
The 3 files {dataset}_triples.train/valid/test contain the observed triples
(training/validation/test sets). They are used in the task link prediction.

The file {dataset}_rule contains soft rules with various confidence levels. The rules are automatically extracted from each dataset by the tool AMIE+(https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/yago-naga/amie/), which can be used for gounding to obtain grounded rule sets(i.e. propositional statements).


------------------
3. DATA FORMAT
------------------

The {dataset}_triples.* files contain one triple per line, stored in a tab ('\t')
separated format. The first element is the head entity, the second the relation,
and the third the tail entity.

The {dataset}_rule file contains one rule together with its confidence level per line, separated by a tab ('\t') format. 
Rules stored in this file are horn clauses with length not longer than 2 and confidence levels not less than 0.8. 
The length of a horn clause rule is the number of atoms appearing in its premise. 

For example, a soft rule in the form of

?b  /film/director/film  ?a   => ?a  /film/film/directed_by  ?b	0.9

has the lenght of 1, stating that two entities linked by relation /film/director/film might also be linked by relation /film/film/directed_by with confidence level of 0.9. ?a/?b denotes entity variable that can be instantiated with the concrete entities to get ground rules, and => represents logical implication. 
For the above-mentioned example, we can propositionalize it with the
concrete entities of Avatar and Cameron, resulting in a ground rule as 

/film/director/film(Cameron,Avatar)=>/film/film/directed_by (Cameron,Avatar).


------------------
4. DATA STATISTICS
------------------

The FB15K data set consists of 1,345 relations and 14,951 entities among them.
The training set contains 483,142 triples, the validation set 50,000 triples,
and the test set 59,071 triples. 454 rules are created for FB15K.

The YAGO37 data set consists of 37 relations and 123,189 entities among them.
The training set contains 989,132 triples, the validation set 50,000 triples,
and the test set 50,000 triples. 16 rules are created for YAGO37.

All triples are unique and we made sure that all entities/relations appearing in
the validation or test sets were occurring in the training set.


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


