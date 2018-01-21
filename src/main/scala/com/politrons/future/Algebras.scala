package com.politrons.future

import scala.concurrent.Future
import scalaz.Free


trait Algebras {

  type FutureM[+A] = A

  sealed trait Action[A]

  type ActionMonad[A] = Free[Action, A]

  case class _Action(f: () => Any) extends Action[Any]

  case class _Zip[T](f: () => Any, f1: () => Any, zip: (T, T) => Any) extends Action[Any]

  case class _OnNext[T](future: Future[Any], f: T => Any) extends Action[Any]

  case class _DoNewFuture[T](future: Future[Any], f: T => Any) extends Action[Any]

  case class _WhenFinish[T](future: Future[Any]) extends Action[Any]

  case class _Subscribe[T](future: Future[Any], onNext: Any => Unit ,
                           onError: Throwable => Any, onComplete: () => Unit) extends Action[Any]


}
