On an unrelated note the UML Diagram is really messy and ugly from a basic understanding point of view...

After do action create prepared statement at the top of the diagram is the choice of prepared yes/no

Besides prepared what are the other choices / on first glance it looks like the only other choice is query

Is this correct ?

>> the diagram is correct

>> I made it so I could keep track of the underlying (messy) state of the world
the JDBC driver always turns statements into prepared statements

Me
what are the other choices if its not prepared at the top ?
what could it be besides prepared ?

Other person
in the JDBC driver code, there are two paths for PreparedStatement vs Statement
but the calls to the server it makes are always as if it was trying to take the PreparedStatement path... I should rename it from flightsql.puml to jdbc.puml


Me
So at the top when it asks the question Prepared? Yes / No
The other choices could be Statement or Query ?

Not me
  12:38 PM
### it can be prepared or not, and it can be an update or a query
12:38
### and if it's prepared it can have params or not (edited)

* Michael Angerman
and and update is defined to be a Statement ?

* Not me
no


Michael Angerman
  12:39 PM
and not a prepared statement ?


Brent Gardner
  12:39 PM
both Statement and PreparedStatement have both executeQuery and executeUpdate
12:40
https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html#executeQuery(java.lang.String)
https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html#executeUpdate(java.lang.String)


Michael Angerman
  12:41 PM
Ok I am being really dumb here but let me make sure I understand this...
In the UML diagram there are 3 choice boxes / Prepared / Query / params correct ?


22 replies
Last reply 22 hours agoView thread


Brent Gardner
  12:41 PM
https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html#executeQuery()
https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html#executeUpdate()
