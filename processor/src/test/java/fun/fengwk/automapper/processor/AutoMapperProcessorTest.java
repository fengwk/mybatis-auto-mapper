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
    public void testDemo() {
        Compilation compilation = Compiler
                .javac()
                .withProcessors(new AutoMapperProcessor())
                .compile(
                        JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoDO.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoMapper.java")
                );
        assertThat(compilation).generatedFile(StandardLocation.CLASS_OUTPUT, "fun/fengwk/automapper/processor/demo/DemoMapper.xml");
    }

    @Test
    public void testUser() {
        Compilation compilation = Compiler
            .javac()
            .withProcessors(new AutoMapperProcessor())
            .compile(
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/user/CacheRepository.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/user/GsonCacheRepository.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/user/SimpleIdCacheRepository.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/user/GsonLongIdCacheRepository.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/user/UserDO.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/user/UserMapper.java")
            );
        assertThat(compilation).generatedFile(StandardLocation.CLASS_OUTPUT, "fun/fengwk/automapper/processor/user/UserMapper.xml");
    }

    @Test
    public void testExample() {
        Compilation compilation = Compiler
                .javac()
                .withProcessors(new AutoMapperProcessor())
                .compile(
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/mapper/BaseMapper.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/mapper/EmptyMapper.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/mapper/ExampleMapper.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/mapper/SimpleExampleMapper.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/model/BaseDO.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/model/EmptyDO.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/model/ExampleDO.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/example/model/SimpleExampleDO.java")
                );
    }

}
