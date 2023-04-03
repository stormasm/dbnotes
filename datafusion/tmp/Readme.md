
These are my own personal files from datafusion that I overwrite onto the files in datafusion...

Modify the following line of code in *tpch.rs*

benchmarks/src/bin/tpch.rs

```rust
 /// CI checks
 #[cfg(test)]
-#[cfg(feature = "ci")]
 mod ci {
     use super::*;
     use arrow::datatypes::{DataType, Field};
```