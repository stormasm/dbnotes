
### Setup of Datafusion including testing

* [setup.md](./setup.md)

### Performance of Polars versus Datafusion

[Dataframe Showdown: Polars versus Datafusion](https://www.confessionsofadataguy.com/dataframe-showdown-polars-vs-spark-vs-pandas-vs-datafusion-guess-who-wins/)

### Slack: Arrow-Rust

* channel started on March 10, 2021
* [First Message](https://the-asf.slack.com/archives/C01QUFS30TD/p1615401105000200)


The Execs create the RecordBatches which is the final output of datafusion.   
Datafusion's whole mission in life is to create [RecordBatches](https://docs.rs/arrow/latest/arrow/record_batch/struct.RecordBatch.html).

the impl ExecutionPlan for WhateverExec the **execute** method creates a SendableRecordBatchStream

in other words :)   
the Execs create the RecordBatches see  [List of Execs](https://docs.rs/datafusion/latest/datafusion/index.html#physical-plan)

