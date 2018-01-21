# FreeFuture
![My image](img/future.png)
A Free monad of scalaz to wrap scala futures to help the use of futures.

In case you want to run some function asynchroniously and chain with other functions in a pipeline.

```
    FutureFunction(getSentence)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .subscribe(result => println(s"OnNext:$result"),
        t => println(s"OnError:$t"),
          () => println("We complete the pipeline"))

```

In case we want to run in parallel multiple functions in futures and then zip the results.

```
   ParallelFunctions(getSentence, getSecondSentence, concat --> Function to apply once the paralle functions finish)
      .doNext(upperCase)
      .appendResult()
      .subscribe()
```

The DSL will manage all monads response to extract values as Option or Either.
