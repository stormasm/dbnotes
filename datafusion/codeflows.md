
The Execs create the ExecutionPlans see  [List of Execs](https://docs.rs/datafusion/latest/datafusion/index.html#physical-plan)
and then those ExecutionPlans are used to create the RecordBatches via
[collect](https://github.com/apache/arrow-datafusion/blob/master/datafusion/core/src/physical_plan/mod.rs) which takes in the ExecutionPlan and returns a RecordBatch which is the final output of datafusion. 

Datafusion's whole mission in life is to create [RecordBatches](https://docs.rs/arrow/latest/arrow/record_batch/struct.RecordBatch.html).

the impl ExecutionPlan for WhateverExec the **execute** method creates a SendableRecordBatchStream