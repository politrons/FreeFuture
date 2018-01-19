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

  override def interpreter: Action ~> Id = new (Action ~> Id) {

    def apply[A](a: Action[A]): Id[A] = a match {
      case _Action(function) => runInFuture(function);
      case _Zip(f1, f2, zip) => zipFunctions(f1, f2, zip)
      case _OnNext(future, f) => transformFuture(future, f)
      case _DoNewFuture(future, f) => runInNewFuture(future, f)
      case _WhenFinish(future) => appendFutureValue(future)
    }
  }

  def zipFunctions(f1: () => Any, f2: () => Any, zip: (Nothing, Nothing) => Any): Future[Any] = {
    val zipFunction = zip.asInstanceOf[(Any, Any) => Any]
    runInFuture(f1)
      .zip(runInFuture(f2))
      .map(tuple => processZip(zipFunction, tuple))
  }

  private def processZip(zipFunction: (Any, Any) => Any, tuple: (Any, Any)) = {
    val value1 = tuple._1 match {
      case right: Right[Any, Any] => right.right.get
      case _ => " Error in first function"
    }
    val value2 = tuple._2 match {
      case right: Right[Any, Any] => right.right.get
      case _ => " Error in second function"
    }
    zipFunction.apply(value1, value2)
  }

  def runInFuture(function: () => Any): Future[Any] = {
    Future {
      function.apply()
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

  def appendFutureValue(future: Future[Any]): Any = future.onComplete(value => result = value.get)

}
