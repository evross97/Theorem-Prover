How to use my Theorem Prover

- Open up a command line
- move to the folder 'reasoning'
- edit the text file "in.txt" with your formulas, they must be one per line, e.g.

A | C
(A -> B) & C
B

- type "javac -s src -d bin src/cnf/*.java"
- save the text file and run the code from the command line by typing the following "java -cp bin cnf.Main2"
- the output will appear in the command line
