
datafusion_ctx creates a new SessionContext

prepare_stmt creates a new SessionContext via its call to datafusion_ctx

get_flight_info_statement calls prepare_stmt and then later on calls datafusion_ctx

does this mean we are instantiating two (2) new SessionContext's in the same method ?
