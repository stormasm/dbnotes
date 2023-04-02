
The DML (Data Manipulation Language) statements INSERT , UPDATE and DELETE are used to insert, update or delete data on the database respectively. The INSERT statement is used to add rows to a table.

1) create the statement, get the schema and a handle

do_action_create_prepared_statement <->
ActionCreatePreparedStatementRequest

2) for DDL/DML - run the statement

do_put_prepared_statement_update <->
CommandPreparedStatementUpdate

2) for DQL - run the statement

get_flight_info_statement <->
CommandStatementQuery

3) query has been run, ask where the results are stored

get_flight_info_prepared_statement <->
CommandPreparedStatementQuery

4) download the results

do_get_fallback <->
Request<Ticket>

5) close the prepared statement

do_action_close_prepared_statement <->
ActionClosePreparedStatementRequest

6) And finally (for now)

do_put_prepared_statement_query <->
CommandPreparedStatementQuery

### References

[server.rs](https://github.com/apache/arrow-rs/blob/master/arrow-flight/src/sql/server.rs)

[arrow.flight.protocol.rs](https://github.com/apache/arrow-rs/blob/master/arrow-flight/src/sql/arrow.flight.protocol.sql.rs)

[arrow flight rpc](https://arrow.apache.org/docs/format/Flight.html)
