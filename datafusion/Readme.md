

The Execs create the RecordBatches which is the final output of datafusion.   
Datafusion's whole mission in life is to create [RecordBatches](https://docs.rs/arrow/latest/arrow/record_batch/struct.RecordBatch.html).

the impl ExecutionPlan for WhateverExec the **execute** method creates a SendableRecordBatchStream

in other words :)   
the Execs create the RecordBatches see  [List of Execs](https://docs.rs/datafusion/latest/datafusion/index.html#physical-plan)

