

The SQL INSERT INTO Statement  
The INSERT INTO statement is used to insert new records in a table.

INSERT INTO Syntax  
It is possible to write the INSERT INTO statement in two ways:

1. Specify both the column names and the values to be inserted:

```rust
INSERT INTO table_name (column1, column2, column3, ...)  
VALUES (value1, value2, value3, ...);  
```

2. If you are adding values for all the columns of the table, you do not need to specify the column names in the SQL query. However, make sure the order of the values is in the same order as the columns in the table. Here, the INSERT INTO syntax would be as follows:

```rust
INSERT INTO table_name  
VALUES (value1, value2, value3, ...);
```

### References

[insert statement syntax](https://www.w3schools.com/sql/sql_insert.asp)
