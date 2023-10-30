
### How to call async method from method that is not async

Mr Travolta:: I have an async method that returns a *SendableRecordBatchStream*     
How can I call it from the *ExecutionPlan::execute()*  method since its not async?

Raphael Taylor-Davies::
*futures::stream::once* is your friend

Raphael Taylor-Davies::
Followed by *flatten* or *try_flatten*

Mr Travolta::
Thanks. That worked.

Andrew Lamb::
Anyone want to volunteer to add that hint to the documentation of *ExecutionPlan::execute()* ?   
it seems to have come up a few times.


### Some notes on tpch testing

* [Andrew Lamb Issue 7949](https://github.com/apache/arrow-datafusion/issues/7949)
* [Slack Ref](https://the-asf.slack.com/archives/C01QUFS30TD/p1698418490366659)

[Read this first continually...](https://docs.rs/datafusion/latest/datafusion/index.html)

### Recent Activiy

[context](./context.md)

### Code Flows

* [LogicalPlan](https://github.com/stormasm/dbnotes/blob/main/datafusion/codeflows.md#logical-plan)
* [PhysicalPlan](https://github.com/stormasm/dbnotes/blob/main/datafusion/codeflows.md#physical-plan)

### Setup of Datafusion including testing

* [setup.md](./setup.md)

### Performance of Polars versus Datafusion

[Dataframe Showdown: Polars versus Datafusion](https://www.confessionsofadataguy.com/dataframe-showdown-polars-vs-spark-vs-pandas-vs-datafusion-guess-who-wins/)

### Slack: Arrow-Rust

* channel started on March 10, 2021
* [First Message](https://the-asf.slack.com/archives/C01QUFS30TD/p1615401105000200)
