On an unrelated note the UML Diagram is really messy and ugly from a basic understanding point of view...

After do action create prepared statement at the top of the diagram is the choice of prepared yes/no

Besides prepared what are the other choices / on first glance it looks like the only other choice is query

Is this correct ?

>> the diagram is correct

>> I made it so I could keep track of the underlying (messy) state of the world
the JDBC driver always turns statements into prepared statements

what are the other choices if its not prepared at the top ?
what could it be besides prepared ?

>> in the JDBC driver code, there are two paths for PreparedStatement vs Statement
>> but the calls to the server it makes are always as if it was trying to take the PreparedStatement path... I should rename it from flightsql.puml to jdbc.puml

So at the top when it asks the question Prepared? Yes / No
The other choices could be Statement or Query ?

### The key answer to the question is here

>> it can be prepared or not, and it can be an update or a query
>> and if it's prepared it can have params or not (edited)

and and update is defined to be a Statement ?g
>> no

Me
and not a prepared statement ?
>> both Statement and PreparedStatement have both executeQuery and executeUpdate

Me
Ok I am being really dumb here but let me make sure I understand this...
In the UML diagram there are 3 choice boxes / Prepared / Query / params correct ?


22 replies
Last reply 22 hours agoView thread


### References

* [Ref 01](https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html#executeQuery-java.lang.String)
* [Ref02](https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html#executeUpdate-java.lang.String)
* [Ref 03](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html#executeQuery())
* [Ref 04](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html#executeUpdate())
