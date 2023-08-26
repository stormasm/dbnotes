
* [discord 8/26/23](https://discord.com/channels/509773073294295082/630073320825552966/1144994172336611379)

Hey folks, I've just released a new alpha version, 117, that has some big changes[1] to how certain things related to durability are handled, while implementing support for tree nodes to be merged into their siblings when they become empty, so that the system will be able to scale down resource use when data is removed over time. I haven't done comparative performance checks yet against the last release, and would be grateful if anyone hits problems to talk about them here and ideally include a reproduction test.

A (potentially, if you set these yourself instead of relying on the default) breaking change that has occurred between the last alpha and 117 is that I've removed 2 of the 3 const generic parameters on Db. The one remaining is LEAF_FANOUT which controlls how branchy the system will be - setting this smaller will cause random reads to have lower latency, setting this higher will cause the system to use less disk space overall and achieve better on-disk compression ratios. It's possible that I'll allow the other two to be configured over time but right now it doesn't seem very useful for 99% of workloads to do so.

Now that tree nodes can be merged, one of the next alpha releases will include support for actually reclaiming heap file data as well, but I wanted to keep high-level leaf merging separate from low-level storage compaction to simplify debugging if issues are uncovered.

https://github.com/spacejam/sled/pull/1459/files

* [discord 7/28/23](https://discord.com/channels/509773073294295082/509773073294295084/1134466317567660083)

hey folks 🙂 after a few years of kind of intense experimentation in isolation, the first extremely-rough cut for the storage engine of 1.0 is ready for testing. This first alpha intentionally has very few features, and the documentation is not yet in place, and the api will change before 1.0.0 final, but if you'd like to play around with the basic API, I've just released https://docs.rs/sled/1.0.0-alpha.1 🙂 This weekend I'm going to be adding most of the existing sled features, although transactions and subscription might lag a little more as I think about them more. Windows support should come later today.
