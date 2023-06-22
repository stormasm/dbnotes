
All of the main config values for raft including

```rust
/// The heartbeat interval in milliseconds at which leaders will send heartbeats to followers
 #[clap(long, default_value = "50")]
 pub heartbeat_interval: u64,
```

is located in *openraft/src/config/config.rs*
