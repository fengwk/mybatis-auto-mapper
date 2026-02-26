package fun.fengwk.automapper.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Optional;

import static com.google.testing.compile.CompilationSubject.assertThat;

/**
 * @author fengwk
 */
public class AutoMapperProcessorTest {

    @Test
    public void testDemo() throws IOException {
        Compilation compilation = Compiler
                .javac()
                .withProcessors(new AutoMapperProcessor())
                .compile(
                        JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoDO.java"),
                        JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoMapper.java")
                );
        Optional<JavaFileObject> generatedFile = compilation.generatedFile(
            StandardLocation.CLASS_OUTPUT,
            "fun/fengwk/automapper/processor/demo/DemoMapper.xml"
        );
        assert generatedFile.isPresent();

        String xml = generatedFile.get().getCharContent(false).toString();
        assert xml.contains("from `demo`");
        assert xml.contains("limit #{limit}");
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
    public void testExample() throws IOException {
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

        Optional<JavaFileObject> generatedFile = compilation.generatedFile(
            StandardLocation.CLASS_OUTPUT,
            "fun/fengwk/automapper/example/mapper/ExampleMapper.xml"
        );
        assert generatedFile.isPresent();

        String xml = generatedFile.get().getCharContent(false).toString();
        assert xml.contains("<insert id=\"insertSelective\"");
        assert xml.contains("<if test=\"name != null\">`name`,</if>");
        assert xml.contains("<if test=\"name != null\">#{name},</if>");
        assert !xml.contains("<if test=\"sort != null\">`sort`,</if>");
    }

    @Test
    public void testDemoPostgreSql() throws IOException {
        Compilation compilation = Compiler
            .javac()
            .withProcessors(new AutoMapperProcessor())
            .compile(
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/BaseMapper.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/BaseDO.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoDO.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoPgMapper.java")
            );

        Optional<JavaFileObject> generatedFile = compilation.generatedFile(
            StandardLocation.CLASS_OUTPUT,
            "fun/fengwk/automapper/processor/demo/DemoPgMapper.xml"
        );
        assert generatedFile.isPresent();

        String xml = generatedFile.get().getCharContent(false).toString();
        assert xml.contains("from \"demo_pg\"");
        assert xml.contains("like #{name} || '%'");
        assert xml.contains("limit #{limit} offset #{offset}");
        assert xml.contains("<foreach collection=\"collection\" item=\"item\" separator=\",\">");
        assert xml.contains("<otherwise>default</otherwise>");
    }

    @Test
    public void testDemoSql92() throws IOException {
        Compilation compilation = Compiler
            .javac()
            .withProcessors(new AutoMapperProcessor())
            .compile(
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/BaseMapper.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/BaseDO.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoDO.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoSql92Mapper.java")
            );

        Optional<JavaFileObject> generatedFile = compilation.generatedFile(
            StandardLocation.CLASS_OUTPUT,
            "fun/fengwk/automapper/processor/demo/DemoSql92Mapper.xml"
        );
        assert generatedFile.isPresent();

        String xml = generatedFile.get().getCharContent(false).toString();
        assert xml.contains("from \"demo_sql92\"");
        assert xml.contains("limit #{limit} offset #{offset}");
        assert xml.contains("like '${name}%'");
    }

    @Test
    public void testDemoSqlite() throws IOException {
        Compilation compilation = Compiler
            .javac()
            .withProcessors(new AutoMapperProcessor())
            .compile(
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/BaseMapper.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/BaseDO.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoDO.java"),
                JavaFileObjects.forResource("fun/fengwk/automapper/processor/demo/DemoSqliteMapper.java")
            );

        Optional<JavaFileObject> generatedFile = compilation.generatedFile(
            StandardLocation.CLASS_OUTPUT,
            "fun/fengwk/automapper/processor/demo/DemoSqliteMapper.xml"
        );
        assert generatedFile.isPresent();

        String xml = generatedFile.get().getCharContent(false).toString();
        assert xml.contains("from \"demo_sqlite\"");
        assert xml.contains("like #{name} || '%'");
        assert xml.contains("limit #{limit} offset #{offset}");
        assert xml.contains("<foreach collection=\"collection\" item=\"item\" separator=\",\">");
        assert xml.contains("<otherwise>default</otherwise>");
    }

}
