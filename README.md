
* [datafusion notes](./datafusion/Readme.md)

### Issues

* [how do you get loopback addresses other than 127.0.0.1 to work on a mac](https://superuser.com/questions/458875/how-do-you-get-loopback-addresses-other-than-127-0-0-1-to-work-on-os-x)

when I ping 127.0.0.2 on a mac prior to running these commands I get a "Request timeout for icmp_seq 0" therefore you must run this command first.

```rust
sudo ifconfig lo0 alias 127.0.0.2 up
sudo ifconfig lo0 alias 127.0.0.3 up
```

### Legacy Folder

* [ioxnotes](https://github.com/stormasm/ioxnotes)
