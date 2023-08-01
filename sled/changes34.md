
### Shows changes between 34.7 and 1.0

[start here](https://github.com/spacejam/sled/commit/3d4c6a7c400ea8b4e696f16cff836e0c27e8df45)


|name |status |
|-|-|
| atomic_shim | gone |
| backoff | gone |     
| batch | modified |       
| cache_padded | gone |
| concurrency_control | gone |
| config | gone |
| context | gone |
| db | modified |
| debug-delay | gone |
| dll | gone |
| event_log | gone |
| fail | gone |
| fastcmp | gone |
| fastlock | gone |
| flush_epoch | modified |
| flusher | gone |
| fnv | gone |
| histogram | gone |
| iter | gone |
| ivec | gone |
| lazy | gone |
| lib | modified |
| lru | gone |
| meta | gone |
| metrics | gone |
| node | gone |
| oneshot | gone |
| result | gone |
| serialization | gone |
| stack | gone |
| subscriber | gone |
| sys_limits | gone |
| threadpool | gone |
| transaction | gone |
| tree | gone |
| varint | gone |

### Cargo.toml changes

```rust
bincode = "1.3.3"
cache-advisor = "1.0.12"
concurrent-map = { version = "5.0.27", features = ["serde"] }
crc32fast = "1.3.2"
ebr = "0.2.8"
inline-array = { version = "0.1.11", features = ["serde", "concurrent_map_minimum"] }
fs2 = "0.4.3"
log = "0.4.19"
pagetable = "0.4.3"
parking_lot = { version = "0.12.1", features = ["arc_lock"] }
rayon = "1.7.0"
serde = { version = "1.0", features = ["derive"] }
stack-map = { version = "1.0.3", features = ["serde"] }
zstd = "0.12.4"
fnv = "1.0.7"
fault-injection = "1.0.9"
crossbeam-queue = "0.3.8"
crossbeam-channel = "0.5.8"
```