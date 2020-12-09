**In Short** <br />
Import this code onto your system.

Follow this link to download my best index : https://drive.google.com/file/d/18-YjcF_AWUdnFvi5PQ3tdF_7NVzkOnIS/view?usp=sharing
Know the path to the index on your system.
Enter this path into the top of the code in the *QueryEngine.java* file.

When run the code will prompt you to build an index, type "n", <br />
then prompt you to run queries, type "y", <br \>
then prompt you for question document, type "questions.txt". <br \>

Detailed instructions below.

**Setup Instructions** <br />

Import the program from github, folder *watson-final* contains all the code for my program. In the resources subfolder you will see *questions.txt* and *wikiPages*. If you wish to test the index building capabilities you can insert all of the wiki articles into this folder and follow the instructions to run below. You will still need to download the best index I have created and put it somewhere on your local machine.

I have included the text doc *questions.txt* for when you want to run the queries. Instructions are below on how to use it further.

To make sure the program can access your index, at the top of the *QueryEngine.java* file (the main code file), you
can assign the *indexPath* variable to wherever you have stored the index on your local machine. 
*indexPath* is the path for how it was stored on my machine, and can serve as an example.

Link for my best index : https://drive.google.com/file/d/18-YjcF_AWUdnFvi5PQ3tdF_7NVzkOnIS/view?usp=sharing
If you have this link you should be able to download, email me at zwiegand@email.arizona.edu immediately if you have problems.

When I was testing importing my project from this repository, Eclipse my IDE gave imported two folders, *watson-final* and *watson-final_watson-final*. The later seemed 
to be the folder that held my working project. 

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
