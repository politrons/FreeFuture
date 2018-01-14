# FreeFuture
![My image](img/future.png)
A Free monad of scalaz to wrap scala futures to add extra features.

In case you want to run some function asynchroniously and chain with other functions in a pipeline.

```
  FutureAction(() => getSentence)
      .doNext(upperCase)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .doNewFuture(replace("AWESOME", "cool"))
      .doNext(upperCase)
      .appendResult()
      .~>
```

The DSL will manage all monads response to extract values as Option or Either.
