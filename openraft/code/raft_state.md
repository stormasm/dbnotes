```rust
{
vote: UTime { data: Vote { leader_id: LeaderId { term: 1, node_id: 0 }, committed: false }, utime: Some(Instant { tv_sec: 106756, tv_nsec: 936314791 }) },
committed: Some(LogId { leader_id: LeaderId { term: 3, node_id: 0 }, index: 1 }),
purged_next: 0,
log_ids: LogIdList { key_log_ids: [LogId { leader_id: LeaderId { term: 0, node_id: 0 }, index: 0 }, LogId { leader_id: LeaderId { term: 1, node_id: 0 }, index: 1 }, LogId { leader_id: LeaderId { term: 3, node_id: 0 }, index: 2 }] },
membership_state: MembershipState { committed: EffectiveMembership { log_id: None, membership: Membership { configs: [], nodes: {} }, voter_ids: {} }, effective: EffectiveMembership { log_id: None, membership: Membership { configs: [], nodes: {} }, voter_ids: {} } },
snapshot_meta: SnapshotMeta { last_log_id: None, last_membership: StoredMembership { log_id: None, membership: Membership { configs: [], nodes: {} } }, snapshot_id: "" },
server_state: Learner,
accepted: Accepted { leader_id: LeaderId { term: 0, node_id: 0 }, log_id: None },
io_state: IOState { building_snapshot: false, vote: Vote { leader_id: LeaderId { term: 1, node_id: 0 }, committed: false }, flushed: LogIOId { leader_id: LeaderId { term: 0, node_id: 0 }, log_id: None }, applied: Some(LogId { leader_id: LeaderId { term: 3, node_id: 0 }, index: 1 }), purged: None },
snapshot_streaming: None,
purge_upto: None
}
```
