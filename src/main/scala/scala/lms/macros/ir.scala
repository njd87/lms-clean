package scala.lms.macros

//import language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox.Context

import scala.util.matching.Regex

import scala.collection.mutable


object ir {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    println("YOUPI")
    println(c.prefix)
    val List(a) = annottees
    println(a)
    a.tree match {
      case q"def $name[..$t](..$args): $tpe" =>
        val args1 = args map { case ValDef(_,x,_,_) => q"ref($x)" }
        return c.Expr(q"def $name[..$t](..$args): $tpe = reflect(${name.toString},..$args1)")
      case q"def $name[..$t](..$args): $tpe = $body" =>
        // TODO: strip by-name type
        val args0 = args map { case ValDef(_,x,_,_) => q"$x" }
        val args1 = args map { case ValDef(_,x,_,_) => q"ref($x)" }
        val name_next = TermName(name.toString + "_next")
        // TODO: what if we have type parameters? just disallow?
        return c.Expr(q"""
          lower((..$args) => 
            Rewrite[$tpe]($name(..$args0), $name_next(..$args0)))
          def $name[..$t](..$args): $tpe = reflect(${name.toString},..$args1)
          def $name_next[..$t](..$args): $tpe = $body
        """)
     // TODO class def
      //case t@ClassDef(_,_,_,_) =>
    }
  }
}
