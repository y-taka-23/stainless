/* Copyright 2009-2016 EPFL, Lausanne */

package stainless

import org.scalatest._

class ExtractionSuite extends FunSpec with inox.ResourceUtils with InputUtils {

  def testAll(dir: String): Unit = {
    val reporter = new inox.TestSilentReporter
    val ctx = inox.Context(reporter, new inox.utils.InterruptManager(reporter))

    val fs = resourceFiles(dir, _.endsWith(".scala")).toList

    describe(s"Program extraction in $dir") {
      val files = fs map { _.getPath }
      val tryProgram = scala.util.Try(loadFiles(ctx, files)._2)
      it("should be successful") { assert(tryProgram.isSuccess) }

      if (tryProgram.isSuccess) {
        val program = tryProgram.get

        it("should typecheck") {
          program.symbols.ensureWellFormed
          for (fd <- program.symbols.functions.values.toSeq) {
            import program.symbols._
            assert(isSubtypeOf(fd.fullBody.getType, fd.returnType))
          }
        }

        val tryExProgram = scala.util.Try(extraction.extract(program, ctx))
        describe("and transformation") {
          it("should be successful") { assert(tryExProgram.isSuccess) }

          if (tryExProgram.isSuccess) {
            val exProgram = tryExProgram.get
            it("should produce no errors") { assert(reporter.lastErrors.isEmpty) }

            it("should typecheck") {
              exProgram.symbols.ensureWellFormed
              for (fd <- exProgram.symbols.functions.values.toSeq) {
                import exProgram.symbols._
                assert(isSubtypeOf(fd.fullBody.getType, fd.returnType))
              }
            }

            it("should typecheck without matches") {
              for (fd <- exProgram.symbols.functions.values.toSeq) {
                import exProgram.symbols._
                assert(isSubtypeOf(matchToIfThenElse(fd.fullBody).getType, fd.returnType))
              }
            }
          }
        }
      }
    }
  }

  testAll("extraction")
}

