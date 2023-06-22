

* [storage/mod.rs](https://github.com/datafuselabs/openraft/blob/main/openraft/src/storage/mod.rs)

```rust
#[async_trait]
pub trait RaftLogReader<C>: Send + Sync + 'static
where C: RaftTypeConfig

#[async_trait]
pub trait RaftSnapshotBuilder<C>: Send + Sync + 'static
where C: RaftTypeConfig

#[async_trait]
pub trait RaftStorage<C>: RaftLogReader<C> + Send + Sync + 'static
where C: RaftTypeConfig
```
