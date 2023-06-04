
There are 3 data directories

1. data
2. data-reduce
3. data-insert

The data directory is the original directory found in datafusion-benchmarks.

The data-reduce directory is a greatly reduced copy of data with only the first n rows of data.

The data-insert directory is a set of bash scripts of insert statements.

The decoupling of the data allows for variation in the size of the data sets
that get inserted into your database.

The create statements and the insert statements are decoupled to enable
more debugging of the process of inserting data into the db.

There is a parameter that gets set to define how big or how many rows are
contained in the data-reduce directory.

At the moment there is no querying go on yet...

Its so far just about getting data into the system.

How to query a reduced set of data is the next topic we will address...
