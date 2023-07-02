
To turn on logging in openraft you need to set the loglevel in your terminal.   
If nothing is set then it won't show anything.

```rust
cat .rust | grep RUST_LOG
logt
loglevel
```

you can either set the loglevel to *info* or *debug* and you will see different stuff   

[t10_initialization](https://github.com/datafuselabs/openraft/blob/main/tests/tests/life_cycle/t10_initialization.rs)

then the log file gets written to the

```rust
tests/_log directory
```

to run a particular test

```rust
ctno t10_initialization
```

For more details on how the tracing test code works in openraft.   
[fixtures/mod.rs](https://github.com/datafuselabs/openraft/blob/main/tests/tests/fixtures/mod.rs)
