
### High Level Concepts

[The LogicalPlan is converted to an ExecutionPlan by a PhysicalPlanner](https://docs.rs/datafusion/latest/datafusion/index.html#parse-plan-optimize-execute)

### Logical Plan

a LogicalPlan gets created out of the sql.   
a dataframe is a LogicalPlan with SessionState.

create_physical_plan takes in a LogicalPlan

In this [MemTable example](https://github.com/apache/arrow-datafusion/blob/master/datafusion-examples/examples/memtable.rs) you have your RecordBatch in memory and then a dataframe comes along and is able to talk to it.

### Physical Plan

**create_physical_plan** is the core method defined in the trait PhysicalPlanner
[physical_plan/planner.rs](https://github.com/apache/arrow-datafusion/blob/master/datafusion/core/src/physical_plan/planner.rs) which takes in a LogicalPlan and SessionState and returns an ExecutionPlan

The [Execs](https://docs.rs/datafusion/latest/datafusion/index.html#physical-plan) creates the [ExecutionPlans](https://github.com/apache/arrow-datafusion/blob/master/datafusion/core/src/physical_plan/mod.rs) and then those ExecutionPlans are used to create the RecordBatches via [collect](https://github.com/apache/arrow-datafusion/blob/master/datafusion/core/src/physical_plan/mod.rs) which takes in the ExecutionPlan and returns a RecordBatch which is the final output of datafusion. 

Datafusion's whole mission in life is to create [RecordBatches](https://docs.rs/arrow/latest/arrow/record_batch/struct.RecordBatch.html).

with the 
```rust
impl ExecutionPlan for WhateverExec 
```
the **execute** method creates a SendableRecordBatchStream

#### More tests that massage these Exec concepts

[datasource/file_format/json.rs](https://github.com/apache/arrow-datafusion/blob/master/datafusion/core/src/datasource/file_format/json.rs)