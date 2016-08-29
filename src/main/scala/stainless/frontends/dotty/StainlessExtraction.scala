/* Copyright 2009-2016 EPFL, Lausanne */

package stainless
package frontends.dotty

import dotty.tools.dotc.core.Phases._
import dotty.tools.dotc.core.Contexts._

class StainlessExtraction(inoxCtx: inox.InoxContext) extends Phase {

  def phaseName: String = "stainless extraction"

  def run(implicit ctx: Context): Unit = {
    val extraction = new CodeExtraction(inoxCtx)

    val unit = ctx.compilationUnit
    val tree = unit.tpdTree

    println(tree.getClass)
  }
}