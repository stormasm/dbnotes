
Send some data to SL and then do nothing after that

5 or 6 minutes later

```rust
[2023-05-25T01:04:07Z INFO  schnapslatte::raft::sxt_snapshot_builder] Saving snapshot to file:///Users/ma/Library/Application%20Support/io.spaceandtime.schnapslatte
[2023-05-25T01:04:07Z DEBUG schnapslatte::arrow::arrow_block] Wrote 1486 bytes in 8.861ms
[2023-05-25T01:04:08Z INFO  schnapslatte::raft::sxt_snapshot_builder] Creating new delta table file:///Users/ma/Library/Application%20Support/io.spaceandtime.schnapslatte/tables/person
[2023-05-25T01:04:08Z INFO  schnapslatte::raft::sxt_snapshot_builder] Saved delta snapshot v1
[2023-05-25T01:04:08Z INFO  schnapslatte::raft::sxt_snapshot_builder] Took 232.872ms to save snapshot of 31.34 KiB bytes at timestamp 4999
```

5 or 6 minutes later

```rust
[2023-05-25T01:10:34Z INFO  schnapslatte::raft::sxt_snapshot_builder] Saving snapshot to file:///Users/ma/Library/Application%20Support/io.spaceandtime.schnapslatte
[2023-05-25T01:10:34Z INFO  schnapslatte::raft::sxt_snapshot_builder] Creating new delta table file:///Users/ma/Library/Application%20Support/io.spaceandtime.schnapslatte/tables/person
[2023-05-25T01:10:34Z ERROR schnapslatte::raft::sxt_snapshot_builder] Error creating delta table VersionAlreadyExists(0)
[2023-05-25T01:10:34Z ERROR schnapslatte::raft::sxt_snapshot_builder] Error taking snapshot: delta caused by 'VersionAlreadyExists(0)' at src/raft/sxt_snapshot_builder.rs:111
```
