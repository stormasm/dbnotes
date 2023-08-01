
To get the filters up and running in raft-kv-rocksdb...

* in src/bin/main.rs

```rust
#[async_std::main]
async fn main() -> std::io::Result<()> {
    // Setup the logger
    tracing_subscriber::fmt()
        .with_target(true)
        .with_thread_ids(true)
        .with_level(true)
        .with_ansi(false)
        .with_env_filter("openraft::engine::engine_impl=info")
        .init();
```
