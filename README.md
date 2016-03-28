poc-persistent-fsm
===========================

## To test the actor recovery, perform the next steps

### Start the node

```
$ sbt clean package run
```

### In another terminal start the transaction

```
$ curl http://127.0.0.1:8080/api/transactions/123/start
```

### Shutdown and then start the node again so there is no FSM running

```
$ sbt run
```

### Submit transaction data, that depends on the started transaction

```
$ curl http://127.0.0.1:8080/api/transactions/123/data/12345
```

### Conclusion

At the last one step the events have to be replayed from the storage and the fsm state is moved to that one, that is required to submit the transaction data