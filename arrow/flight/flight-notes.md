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
>> and if it's prepared it can have params or not

and and update is defined to be a Statement ?
>> no

Me
and not a prepared statement ?
>> both Statement and PreparedStatement have both executeQuery and executeUpdate

Me
Ok I am being really dumb here but let me make sure I understand this...
In the UML diagram there are 3 choice boxes / Prepared / Query / params correct ?

>> yes

what does params mean to you ?  what are examples of the params concept in the diagram ?

>> PTAL at the [tests](./ArrowFlightJdbcDriverTest.java)
>> in the comment in the [puml file](./flight-sql.png)


>> @startuml as observed by the JDBC driver in the real world, derived from tests above

>> that diagram is of the calls to the server resulting from each of those test cases (edited)

Michael Angerman
  24 hours ago
Ok cool thanks...  I will take a look at all of the stuff you referenced and then see how I understand the params concept...
Because I know what a PreparedStatement is and I know what a Query is / that seems pretty obvious to me / the thing I don't get is the params --- once I get that idea then the diagram will / should be more clear to me...
In fact I will try and write that up and you can tell me if what I wrote seems correct

>> String sql = "insert into person values ($1, $2)";
>> $1 and $2 are params

>> https://github.com/apache/arrow-rs/blob/4e7bb45050622d5b43505aa64dacf410cb329941/format/FlightSql.proto#L1440
FlightSql.proto

Thank you !!  Kindly....
Wow.... This makes it much more clear....
OK !  This is great / now I can spend some more time studying this stuff....

Kind of amazing that there is really no spec for Flight / I have reviewed what is out there and basically there only real reference is the FlightSql.proto which doesn't really match up well with the Actions and Commands from a flow diagram point of view Trying to bridge that proto file to the real world is really a stretch for the practical mind

>> its hard to understand because it's built on Flight.proto
>> flight has a gRPC server. flight has endpoints/methods
>> but they are all DoPut and DoGet
>> so flightsql is just a bunch of custom do_put and do_get messages
>> and that makes it harder to read
>> i.e. the same message could mean different things if sent to do_get vs do_put

### References

* [Ref 01](https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html#executeQuery-java.lang.String)
* [Ref02](https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html#executeUpdate-java.lang.String)
* [Ref 03](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html#executeQuery())
* [Ref 04](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html#executeUpdate())
