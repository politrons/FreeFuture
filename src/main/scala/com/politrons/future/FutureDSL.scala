package com.politrons.future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Free.liftF
import scalaz.~>

trait FutureDSL extends Actions {

  var result: Any = _

  def FutureFunction(function: () => Any): ActionMonad[Any] = {
    liftF[Action, Any](_Action(function))
  }

  def ParallelFunctions[T](f: () => Any, f1: () => Any, zip: (T, T) => Any): ActionMonad[Any] = {
    liftF[Action, Any](_Zip(f, f1, zip))
  }

  type Transformer = ~>[Action, FutureM]

  override def interpreter: Transformer = new Transformer {

    def apply[A](a: Action[A]): FutureM[A] = a match {
      case _Action(function) => runInFuture(function).asFutureM
      case _Zip(f1, f2, zip) => zipFunctions(f1, f2, zip).asFutureM
      case _OnNext(future, f) => transformFuture(future, f).asFutureM
      case _DoNewFuture(future, f) => runInNewFuture(future, f).asFutureM
      case _WhenFinish(future) => appendFutureValue(future)
    }
  }

  def zipFunctions[A](f1: () => Any, f2: () => Any, zip: (Nothing, Nothing) => Any): Future[Any] = {
    val zipFunction = zip.asInstanceOf[(Any, Any) => Any]
    runInFuture(f1)
      .zip(runInFuture(f2))
      .map(tuple => zipFunction(processEither(tuple._1), processEither(tuple._2)))
  }

  private def processEither(value: Any) = {
    value match {
      case right: Right[Any, Any] => right.right.get
      case _ => " Error function"
    }
  }

  def runInFuture(function: () => Any): Future[Any] = {
    Future {
      function()
    }
  }

  private def runInNewFuture[A](future: Future[Any], f: Nothing => Any): Future[Any] = {
    val function = f.asInstanceOf[Any => Any]
    future.flatMap(value => Future {
      value match {
        case Left(any) => function(any)
        case Right(any) => function(any)
        case Some(any) => function(any)
        case _ => function(value)
      }
    })
  }

  def transformFuture(future: Future[Any], f: Nothing => Any): Future[Any] = {
    val function = f.asInstanceOf[Any => Any]
    future.map {
      case Left(any) => function(any)
      case Right(any) => function(any)
      case Some(any) => function(any)
      case value => function(value)
    }
  }

  def appendFutureValue[A](future: Future[Any]): FutureM[A] = {
    future.onComplete(value => result = value.get).asInstanceOf[FutureM[A]]
  }

  implicit class customFuture(future: Future[Any]) {
    def asFutureM[A] = future.asInstanceOf[FutureM[A]]
  }

}
