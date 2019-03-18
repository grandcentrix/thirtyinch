package net.grandcentrix.thirtyinch.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import net.grandcentrix.thirtyinch.lint.detector.CallOnMainThreadUsageDetector
import org.assertj.core.api.Assertions

private const val NO_WARNINGS = "No warnings."

private val CLASS_CALLONMAINTHREAD = java(
        "package net.grandcentrix.thirtyinch.callonmainthread;\n" +
                "\n" +
                "import java.lang.annotation.Documented;\n" +
                "import java.lang.annotation.ElementType;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.RetentionPolicy;\n" +
                "import java.lang.annotation.Target;\n" +
                "\n" +
                "@Documented\n" +
                "@Target(ElementType.METHOD)\n" +
                "@Retention(RetentionPolicy.RUNTIME)\n" +
                "public @interface CallOnMainThread {\n" +
                "}"
)

private val CLASS_TIVIEW = java(
        "package net.grandcentrix.thirtyinch;\n" +
                "public interface TiView {}"
)

class CallOnMainThreadUsageDetectorTest : LintDetectorTest() {

    override fun getDetector(): Detector = CallOnMainThreadUsageDetector()

    override fun getIssues(): List<Issue> = listOf(
            CallOnMainThreadUsageDetector.ISSUE_NON_VOID_RETURN_TYPE,
            CallOnMainThreadUsageDetector.ISSUE_NO_TIVIEW_CHILD
    )

    fun testJava_annotation_used_on_method_with_non_void_return_type_should_have_warning() {
        val testInterface = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;\n" +
                        "import net.grandcentrix.thirtyinch.TiView;\n" +
                        "public interface MyInterface extends TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   public int test(String id);\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonVoidMethod.id)
    }

    fun testKotlin_annotation_used_on_method_with_non_void_return_type_should_have_warning() {
        val testInterface = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread\n" +
                        "import net.grandcentrix.thirtyinch.TiView\n" +
                        "interface MyInterface : TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   fun test(id: String): Int\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonVoidMethod.id)
    }

    fun testJava_annotation_used_on_method_with_void_return_type_should_have_no_warning() {
        val testInterface = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;\n" +
                        "import net.grandcentrix.thirtyinch.TiView;\n" +
                        "public interface MyInterface extends TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   public void test(String id);\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .isEqualTo(NO_WARNINGS)
    }

    fun testKotlin_annotation_used_on_method_with_void_return_type_should_have_no_warning() {
        val testInterface = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread\n" +
                        "import net.grandcentrix.thirtyinch.TiView\n" +
                        "interface MyInterface : TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   fun test(id: String)\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .isEqualTo(NO_WARNINGS)
    }

    fun testJava_annotation_used_on_method_in_TiView_child_interface_should_have_no_warning() {
        val testInterface = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;\n" +
                        "import net.grandcentrix.thirtyinch.TiView;\n" +
                        "public interface MyInterface extends TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   public void test(String id);\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .isEqualTo(NO_WARNINGS)
    }

    fun testKotlin_annotation_used_on_method_in_TiView_child_interface_should_have_no_warning() {
        val testInterface = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread\n" +
                        "import net.grandcentrix.thirtyinch.TiView\n" +
                        "interface MyInterface : TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   fun test(id: String)\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .isEqualTo(NO_WARNINGS)
    }

    fun testJava_annotation_used_on_method_in_TiView_child_class_should_have_warning() {
        val testInterface = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;\n" +
                        "import net.grandcentrix.thirtyinch.TiView;\n" +
                        "public class MyClass extends TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   public void test(String id);\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonTiView.id)
    }

    fun testKotlin_annotation_used_on_method_in_TiView_child_class_should_have_warning() {
        val testInterface = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread\n" +
                        "import net.grandcentrix.thirtyinch.TiView\n" +
                        "class MyClass : TiView {\n" +
                        "   @CallOnMainThread\n" +
                        "   fun test(id: String)\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_TIVIEW, CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonTiView.id)
    }

    fun testJava_annotation_used_on_method_in_non_TiView_child_class_should_have_warning() {
        val testInterface = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;\n" +
                        "import net.grandcentrix.thirtyinch.TiView;\n" +
                        "public class MyClass {\n" +
                        "   @CallOnMainThread\n" +
                        "   public void test(String id);\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonTiView.id)
    }

    fun testKotlin_annotation_used_on_method_in_non_TiView_child_class_should_have_warning() {
        val testInterface = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread\n" +
                        "import net.grandcentrix.thirtyinch.TiView\n" +
                        "class MyClass {\n" +
                        "   @CallOnMainThread\n" +
                        "   fun test(id: String)\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonTiView.id)
    }

    fun testJava_annotation_used_on_method_in_non_TiView_interface_class_should_have_warning() {
        val testInterface = java(
                "package foo;\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;\n" +
                        "import net.grandcentrix.thirtyinch.TiView;\n" +
                        "public interface MyView {\n" +
                        "   @CallOnMainThread\n" +
                        "   public void test(String id);\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonTiView.id)
    }

    fun testKotlin_annotation_used_on_method_in_non_TiView_interface_class_should_have_warning() {
        val testInterface = kotlin(
                "package foo\n" +
                        "import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread\n" +
                        "import net.grandcentrix.thirtyinch.TiView\n" +
                        "interface MyClass {\n" +
                        "   @CallOnMainThread\n" +
                        "   fun test(id: String)\n" +
                        "}"
        )

        Assertions.assertThat(lintProject(CLASS_CALLONMAINTHREAD, testInterface))
                .containsOnlyOnce(TiIssue.AnnotationOnNonTiView.id)
    }
}