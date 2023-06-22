
This is the codeflow from the top level of Raft.

Everything starts out with

```rust
Raft::new
```

which is located in *openraft/src/raft.rs*.  

From here you can dive down further to see what is going on...

* src/core/raft_core.rs
