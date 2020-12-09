**Setup Instructions** <br />

This file contains instructions on how to run the program, this information will be repeated on the report.

Import the program from github, folder *final-project* contains all the code for my program, and the collection of
wikiPedia articles. You will still need to download the best index I have created and put it somewhere on your local machine.

To make sure the program can access your index, at the top of the *QueryEngine.java* file (the main code file), you
can assign the *indexPath* variable to wherever you have stored the index on your local machine. 
*indexPath* is the path for how it was stored on my machine, and can serve as an example.

Link for my best index : https://drive.google.com/file/d/18-YjcF_AWUdnFvi5PQ3tdF_7NVzkOnIS/view?usp=sharing
If you have this link you should be able to download, email me at zwiegand@email.arizona.edu immediately if you have problems.

**Running Instructions**

After you have your path for the index set up at the top of the *QueryEngine.java* file you
should be able to run the program.

The console will prompt you whether or not you want to build a new index. If you have the path for my
index setup properly you should select "n", but if for some reason you want to test the index building
function, I would set up a new path for *indexPath*, and let it run. Building the index took over 5 hours on my PC.

You will probably want to select "n" when prompted to build an index.

Next it will as if you want to run a query. You can enter "y".

The program will prompt you for a path. I have included the 100 Jeopardy questions, so entering "questions.txt" will
all you to query all 100 questions. If you want to try additional questions or files, this gives you the option to identify
the different file path here.

After you have entered the path for the txt file containing the Jeopardy questions, the program should run very quickly, 
printing the results. Each question will get an indication whether its "RIGHT" or "WRONG, the output the programs "guess" and the
correct answer. <br />
Example:
```
WRONG
Guess was : Melrose Place
Answer was : Heather Locklear
```
At the very end of the output you will get to see how many questions were guessed correct out of how many questions there were.

```
Answers correct : 23
Total questions : 100
```
