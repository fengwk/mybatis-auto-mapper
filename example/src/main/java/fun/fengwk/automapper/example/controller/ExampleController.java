package fun.fengwk.automapper.example.controller;

import fun.fengwk.automapper.example.mapper.ExampleMapper;
import fun.fengwk.automapper.example.mapper.SimpleExampleMapper;
import fun.fengwk.automapper.example.model.ExampleDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author fengwk
 */
@RestController
public class ExampleController {

    @Autowired
    private ExampleMapper exampleMapper;

    @Autowired
    private SimpleExampleMapper simpleExampleMapper;

    // curl http://localhost:8080/insert?name=NewExample&sort=10
    @GetMapping("/insert")
    public String insert(@RequestParam("name") String name,
                         @RequestParam("sort") Integer sort) {
        ExampleDO exampleDO = new ExampleDO();
        exampleDO.setName(name);
        exampleDO.setSort(sort);
        exampleMapper.insert(exampleDO);
        return exampleDO.toString();
    }

    // curl http://localhost:8080/deleteById?id=1
    @GetMapping("/deleteById")
    public String deleteById(@RequestParam("id") Long id) {
        int result = exampleMapper.deleteById(1);
        return result == 1 ? "success" : "failure";
    }

    // curl http://localhost:8080/updateById?id=1&name=Updated&sort=1
    @GetMapping("/updateById")
    public String updateById(@RequestParam("id") Long id,
                             @RequestParam("name") String name,
                             @RequestParam("sort") Integer sort) {
        ExampleDO exampleDO = new ExampleDO();
        exampleDO.setId(id);
        exampleDO.setName(name);
        exampleDO.setSort(sort);
        int result = exampleMapper.updateById(exampleDO);
        return result == 1 ? "success" : "failure";
    }

    // curl http://localhost:8080/findById?id=1
    @GetMapping("/findById")
    public String findById(@RequestParam("id") Long id) {
        return String.valueOf(exampleMapper.findById(id));
    }

    // curl http://localhost:8080/findByNameStaringWith?name=New
    @GetMapping("/findByNameStaringWith")
    public String findByNameStaringWith(@RequestParam("name") String name) {
        return Arrays.toString(exampleMapper.findByNameStartingWith(name).toArray());
    }

    // curl http://localhost:8080/findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc?name=New&sort=10
    @GetMapping("/findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc")
    public String findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc(@RequestParam("name") String name,
                                                                                @RequestParam("sort") Integer sort) {
        return Arrays.toString(exampleMapper.findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc(name, sort).toArray());
    }

    // curl http://localhost:8080/pageAll?offset=0&limit=3
    @GetMapping("/pageAll")
    public String pageAll(@RequestParam("offset") int offset,
                          @RequestParam("limit") int limit) {
        return Arrays.toString(exampleMapper.pageAll(offset, limit).toArray());
    }

    // curl http://localhost:8080/simpleExample/findAllOrderByNameDesc
    @GetMapping("/simpleExample/findAllOrderByNameDesc")
    public String pageAll() {
        return Arrays.toString(simpleExampleMapper.findAllOrderByNameDesc().toArray());
    }

}
