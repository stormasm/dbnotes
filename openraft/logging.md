
To turn on logging in openraft you need to set the loglevel in your terminal.   
If nothing is set then it won't show anything.

```rust
cat .rust | grep RUST_LOG
logt
loglevel
```

once you set the loglevel to debug which is the level for the test

[t10_initialization](https://github.com/datafuselabs/openraft/blob/main/tests/tests/life_cycle/t10_initialization.rs)

then the log file gets written to the

```rust
tests/_log directory
```
