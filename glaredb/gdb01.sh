#!/bin/bash
/Users/ma/j/tmp08/glaredb/target/debug/glaredb --query "create table person (id int, name string, primary key(id));  insert into person (id, name) values (1, 'Hopper'), (2, 'Kay'); select * from person;"
