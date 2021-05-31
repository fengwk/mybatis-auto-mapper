package fun.fengwk.automapper.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.StandardLocation;

import static com.google.testing.compile.CompilationSubject.assertThat;

/**
 * @author fengwk
 */
public class AutoMapperProcessorTest {

    @Test
    public void test() {
        Compilation compilation = Compiler
                .javac()
                .withProcessors(new AutoMapperProcessor())
                .compile(
                        JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoDO.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoMapper.java")
                );
        assertThat(compilation).generatedFile(StandardLocation.CLASS_OUTPUT, "fun/fengwk/automapper/processor/demo/DemoMapper.xml");
    }

}
