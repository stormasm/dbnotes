
### Testing Legacy Sled

in src/lib.rs at line 97

[remove unused_qualifications](https://github.com/spacejam/sled/blob/main/src/lib.rs#L97)

```rust
ctt test_log --features=testing
```

```rust
cargo test --test test_log --features=testing
```
