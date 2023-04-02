

[Arrow Flight](https://github.com/apache/arrow-rs/tree/master/arrow-flight)

#### Bring up the server

```rust
alias arfs='cre flight_sql_server --features="flight-sql-experimental tls"'
```

#### Bring up the client

```rust
cr --features="cli flight-sql-experimental tls" -- --host 0.0.0.0 --port 50051 "select *"
```

#### Bring up the sl client

```rust
cr --features="cli flight-sql-experimental tls" -- --host 0.0.0.0 --port 3033 "select 1;"
```

```rust
cr --features="cli flight-sql-experimental tls" -- --host 0.0.0.0 --port 3033 "select 1;"
```


### Add this line of code to [flight_sql_client.rs](https://github.com/apache/arrow-rs/blob/master/arrow-flight/src/bin/flight_sql_client.rs)

```rust
async fn main() {
    let args = Args::parse();
    setup_logging();
    let mut client = setup_client(args.client_args).await.expect("setup client");

    let _ = client
        .handshake("admin", "password")
        .await
        .expect("handshake broke");

    let info = client.execute(args.query).await.expect("prepare statement");
    info!("got flight info");
```
