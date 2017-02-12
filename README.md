# RecommendationGenSBT
About
-----
This is recommendation generation enables the user to generate recommendation based on the attributes provided for articles.

Calculates similarity of articles identified by article based on their attributes values.
The number of matching attributes is the most important metric for defining similarity.
In case of a draw, attributes with name higher in alphabet (a is higher than z) is weighted with heavier weight.

e.g)
Example 1:
----------
{"article-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"}} is more similar to
{"article-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"}} than to
{"article-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}}

Example 2:
----------
{"article-1": {"att-a": "a1", "att-b": "b1"}} is more similar to
{"article-2": {"att-a": "a1", "att-b": "b2"}} than to
{"article-3": {"att-a": "a2", "att-b": "b1"}}

sample data available under resources



Instructions
------------
The code is written in Scala to be ran on Apache Spark ove the json input data.
The submission can be done by passing relevant arguments. Use the built jar  to execute the recommendation engine.

Usage sample:
------------
spark-submit --master local --class RecommendationGenerator recommendationgensbt_2.10-1.0.jar article-2000 /media/batch1/home24-test-data.json /media/new1

In windows
----------
Download and save the util at ,
"C://winutils/bin" from 
http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe

On execution set VM argument as -Dhadoop.home.dir=C:\\winutil\\


Usage format:
-------------
RecommendationGenerator <articleKey> <input location> <output location>

arg(0) - article id for which the recommendation is requested
arg(1) - input file location only local files allowed
arg(2) - output file location only local files allowed

Logic Used
----------
Matrix Multiplication & ASCII value utilization for all tie breakers
