CREATE KEYSPACE poc_persistent_fsm WITH replication = {
    'class': 'SimpleStrategy', 
    'replication_factor': '1'
} AND durable_writes = true;