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

#### raft_state/mod.rs

```rust
/// A struct used to represent the raft state which a Raft node needs.
#[derive(Clone, Debug)]
#[derive(PartialEq, Eq)]
pub struct RaftState<NID, N, I>
where
    NID: NodeId,
    N: Node,
    I: Instant,
{
    /// The vote state of this node.
    pub(crate) vote: UTime<Vote<NID>, I>,

    /// The LogId of the last log committed(AKA applied) to the state machine.
    ///
    /// - Committed means: a log that is replicated to a quorum of the cluster and it is of the term
    ///   of the leader.
    ///
    /// - A quorum could be a uniform quorum or joint quorum.
    pub committed: Option<LogId<NID>>,

    pub(crate) purged_next: u64,

    /// All log ids this node has.
    pub log_ids: LogIdList<NID>,

    /// The latest cluster membership configuration found, in log or in state machine.
    pub membership_state: MembershipState<NID, N>,

    /// The metadata of the last snapshot.
    pub snapshot_meta: SnapshotMeta<NID, N>,

    // --
    // -- volatile fields: they are not persisted.
    // --
    /// The state of a Raft node, such as Leader or Follower.
    pub server_state: ServerState,

    pub(crate) accepted: Accepted<NID>,

    pub(crate) io_state: IOState<NID>,

    pub(crate) snapshot_streaming: Option<StreamingState>,

    /// The log id upto which the next time it purges.
    ///
    /// If a log is in use by a replication task, the purge is postponed and is stored in this
    /// field.
    pub(crate) purge_upto: Option<LogId<NID>>,
}
```
