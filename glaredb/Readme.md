
Note that RUST_LOG must be set

```rust
env | grep RUST_LOG
RUST_LOG=trace
```
---
```rust
glaredb --verbose 2 --log-mode full local
glaredb --verbose 2 --log-mode full server
```

### Notes on how to run the scripts in this repo

* the glaredb binary must be located where your other local rust binaries
are located

* or you can hard code the exact location of the glaredb binary
