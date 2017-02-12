# RecommendationGenSBT

Instructions
------------
The code is written in Scala to be ran on Apache Spark ove the json input data.
The submission can be done by passing relevant arguments. Use the built jar  to execute the recommendation engine.

Usage sample:
------------
spark-submit --master local --class RecommendationGenerator recommendationgensbt_2.10-1.0.jar sku-2000 /media/batch1/home24-test-data.json /media/new1

Usage format:
-------------
RecommendationGenerator <articleKey> <input location> <output location>

arg(0) - article id for which the recommendation is requested
arg(1) - input file location only local files allowed
arg(2) - output file location only local files allowed

Logic Used
----------
Matrix Multiplication & ASCII value utilization for all tie breakers
