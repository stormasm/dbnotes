


```bash
alias cre='cargo run --example'

alias gtg='cd ~/j/tmp09/greptimedb'
alias gti='cd ~/j/tmp09/greptimedb-ingester-rust'

alias gtrun='cargo run -- standalone start'
alias gtpsql='psql -h 127.0.0.1 -p 4003 -d public'
```

### Bring up the DB

```bash
gtg
gtrun
```

### Ingest the data

```bash
gti
cre ingest
```

### Bring up the psql client

```bash
gtpsql
```

### Select the data
```sql
select * from weather_demo;
```
