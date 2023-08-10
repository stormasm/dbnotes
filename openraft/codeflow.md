
This is the codeflow from the top level of Raft.

Everything starts out with

```rust
Raft::new
```

which is located in *openraft/src/raft/mod.rs*

From here you can dive down further to see what is going on...

* src/core/raft_core.rs

### append_to_log

append_to_log is called in the
[storage/adapter](https://github.com/datafuselabs/openraft/blob/main/openraft/src/storage/adapter.rs)
in the append method.
