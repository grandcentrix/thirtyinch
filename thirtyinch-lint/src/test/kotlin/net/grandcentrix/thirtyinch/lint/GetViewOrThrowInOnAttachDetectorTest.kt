package net.grandcentrix.thirtyinch.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import net.grandcentrix.thirtyinch.lint.detector.GetViewOrThrowInOnAttachDetector
import org.assertj.core.api.Assertions

private val view = LintDetectorTest.java(
        "package foo;\n" +
                "import net.grandcentrix.thirtyinch.*;\n" +
                "interface MyView extends TiView {\n" +
                "  void invoke();\n" +
                "}"
)

class GetViewOrThrowInOnAttachDetectorTest : LintDetectorTest() {

    override fun getIssues(): MutableList<Issue> = mutableListOf(GetViewOrThrowInOnAttachDetector.ISSUE)

    override fun getDetector(): Detector {
        return GetViewOrThrowInOnAttachDetector()
    }

    fun testJava_noDirectUsage_noWarning() {
        val presenter = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.*;\n" +
                        "public class MyPresenter extends TiPresenter<MyView> {\n" +
                        "  protected void onAttachView(MyView view) {\n" +
                        "    final int test = 42;\n" +
                        "    final int test2 = test();\n" +
                        "    test();\n" +
                        "  }\n" +
                        "  private int test() {" +
                        "    return 42;\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).isEqualTo(NO_WARNINGS)
    }

    fun testKotlin_noDirectUsage_noWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    val test = 42\n" +
                        "    val test2 = test()\n" +
                        "    test()\n" +
                        "  }" +
                        "  private fun test(): Int {" +
                        "    return 42\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).isEqualTo(NO_WARNINGS)
    }

    fun testJava_asMethod_hasWarning() {
        val presenter = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.*;\n" +
                        "public class MyPresenter extends TiPresenter<MyView> {\n" +
                        "  protected void onAttachView(MyView view) {" +
                        "    getViewOrThrow();" +
                        "  }" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_asReference_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    viewOrThrow" +
                        "  }" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_asMethod_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    getViewOrThrow()" +
                        "  }" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testJava_asMethod_chained_hasWarning() {
        val presenter = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.*;\n" +
                        "public class MyPresenter extends TiPresenter<MyView> {\n" +
                        "  protected void onAttachView(MyView view) {" +
                        "    getViewOrThrow().invoke();" +
                        "  }" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_asReference_chained_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    viewOrThrow.invoke()" +
                        "  }" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_asMethod_chained_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    getViewOrThrow().invoke()" +
                        "  }" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testJava_asAssignment_hasWarning() {
        val presenter = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.*;\n" +
                        "public class MyPresenter extends TiPresenter<MyView> {\n" +
                        "  protected void onAttachView(MyView view) {\n" +
                        "    MyView view = getViewOrThrow();\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testJava_asReturn_hasWarning() {
        val presenter = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.*;\n" +
                        "public class MyPresenter extends TiPresenter<MyView> {\n" +
                        "  protected void onAttachView(MyView view) {\n" +
                        "    MyView view = test();\n" +
                        "  }\n" +
                        "  private MyView test() {" +
                        "    return getViewOrThrow();\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_asReturn_reference_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    val view = test()\n" +
                        "  }" +
                        "  private fun test(): MyView {" +
                        "    return viewOrThrow\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_asReturn_method_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    val view = test()\n" +
                        "  }" +
                        "  private fun test(): MyView {" +
                        "    return getViewOrThrow()\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testJava_transitiveUsage_hasWarning() {
        val presenter = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.*;\n" +
                        "public class MyPresenter extends TiPresenter<MyView> {\n" +
                        "  protected void onAttachView(MyView view) {\n" +
                        "    test();\n" +
                        "  }\n" +
                        "  private void test() {" +
                        "    test2();\n" +
                        "  }\n" +
                        "  private void test2() {" +
                        "    test3();\n" +
                        "  }\n" +
                        "  private void test3() {" +
                        "    test4();\n" +
                        "  }\n" +
                        "  private void test4() {" +
                        "    getViewOrThrow();\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_transitiveUsage_reference_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    test()\n" +
                        "  }" +
                        "  private fun test() {" +
                        "    test2()\n" +
                        "  }\n" +
                        "  private fun test2() {" +
                        "    test3()\n" +
                        "  }\n" +
                        "  private fun test3() {" +
                        "    test4()\n" +
                        "  }\n" +
                        "  private fun test4() {" +
                        "    viewOrThrow\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testKotlin_transitiveUsage_method_hasWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    test()\n" +
                        "  }" +
                        "  private fun test() {" +
                        "    test2()\n" +
                        "  }\n" +
                        "  private fun test2() {" +
                        "    test3()\n" +
                        "  }\n" +
                        "  private fun test3() {" +
                        "    test4()\n" +
                        "  }\n" +
                        "  private fun test4() {" +
                        "    getViewOrThrow()\n" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).containsOnlyOnce(TiIssue.GetViewOrThrowInOnAttach.id)
    }

    fun testJava_noTransitiveUsage_noWarning() {
        val presenter = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.*;\n" +
                        "public class MyPresenter extends TiPresenter<MyView> {\n" +
                        "  protected void onAttachView(MyView view) {\n" +
                        "    test();\n" +
                        "  }\n" +
                        "  private void test() {" +
                        "    test2();\n" +
                        "  }\n" +
                        "  private void test2() {" +
                        "    test3();\n" +
                        "  }\n" +
                        "  private void test3() {" +
                        "    test4();\n" +
                        "  }\n" +
                        "  private void test4() {" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).isEqualTo(NO_WARNINGS)
    }

    fun testKotlin_noTransitiveUsage_noWarning() {
        val presenter = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.*\n" +
                        "class MyPresenter : TiPresenter<MyView>() {\n" +
                        "  override fun onAttachView(view: MyView) {" +
                        "    test()\n" +
                        "  }" +
                        "  private fun test() {" +
                        "    test2()\n" +
                        "  }\n" +
                        "  private fun test2() {" +
                        "    test3()\n" +
                        "  }\n" +
                        "  private fun test3() {" +
                        "    test4()\n" +
                        "  }\n" +
                        "  private fun test4() {" +
                        "  }\n" +
                        "}"
        )

        Assertions.assertThat(
                lintProject(
                        tiPresenterStub,
                        tiViewStub,
                        view,
                        presenter
                )
        ).isEqualTo(NO_WARNINGS)
    }
}