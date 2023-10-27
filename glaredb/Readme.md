
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
---
```sh
psql "host=localhost user=glaredb dbname=glaredb port=6543"
```

### Notes on how to run the scripts in this repo

* the glaredb binary must be located where your other local rust binaries
are located

* or you can hard code the exact location of the glaredb binary

### Reference PRs

* [Enable passing sql script as an argument](https://github.com/GlareDB/glaredb/pull/1913)
