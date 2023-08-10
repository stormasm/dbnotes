
This is the codeflow from the top level of Raft.

Everything starts out with

```rust
rg Raft::new
```

which is located in *openraft/src/raft/mod.rs*

Then next this is where the runtime spawns a thread

```rust
let core_handle = C::AsyncRuntime::spawn(core.main(rx_shutdown).instrument(trace_span!("spawn").or_current()));
```

From this file: *src/core/raft_core.rs* is located

* main
* do_main
* runtime_loop

### append_to_log

append_to_log is called in the
[storage/adapter](https://github.com/datafuselabs/openraft/blob/main/openraft/src/storage/adapter.rs)
in the append method.
