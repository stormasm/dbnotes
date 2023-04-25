
### To build the server

```rust
cargo build
```

### To test the server

```rust
cargo test
```

### To bring up the SL server run this command

```rust
cargo run -- --advertise-endpoint=127.0.0.1:50060 --listen-endpoint=127.0.0.1:50060 --leader-endpoint=127.0.0.1:50060
```

### Once the SL server is up and running go here to exercise the server

[flight-testing](https://github.com/stormasm/flight-testing)
