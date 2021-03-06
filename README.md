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
          () => println("Pipeline completed"))

```

In case we want to run in parallel multiple functions in futures and then zip the results.

```
   ParallelFunctions(getSentence, getSecondSentence, concat --> Function to apply once the paralle functions finish)
      .doNext(upperCase)
      .subscribe()
```
Since I´m a huge fan of [Rx](https://github.com/politrons/reactive), here I´m using same syntax for subscription callbacks:

* **onNext** executed per item emitted in the pipeline.
* **onError** executed per throwable emitted in the pipeline.
* **onComplete** executed once the pipeline has finish to emitt items.

    ```
    subscribe(r  => /*onNext callback*/,
              t  => /*onError callback*/,
              () => /*onComplete callback*/)
    ```

The DSL will manage all monads response to extract values as Option or Either.

You can play with the DSL [here](src/test/scala/TestFuture.scala)

