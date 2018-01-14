package com.politrons.future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Free.liftF
import scalaz.~>

trait FutureDSL extends Actions {

  var result: Any = _

  def FutureAction(function: () => Any): ActionMonad[Any] = {
    val action = _Action(function)
    liftF[Action, Any](action)
  }

  override def interpreter: Action ~> Id = new (Action ~> Id) {

    def apply[A](a: Action[A]): Id[A] = a match {
      case _Action(function) => runInFuture(function);
      case _OnNext(future, f) => transformFuture(future, f)
      case _DoNewFuture(future, f) => runInNewFuture(future, f)
      case _WhenFinish(future) => appendFutureValue(future)
    }
  }

  def runInFuture(function: () => Any): Future[Any] = {
    Future {
      function.apply()
    }
  }

  private def runInNewFuture[A](future: Future[Any], f: Nothing => Any) = {
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

  def transformFuture(future: Future[Any], f: Nothing => Any): Any = {
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
