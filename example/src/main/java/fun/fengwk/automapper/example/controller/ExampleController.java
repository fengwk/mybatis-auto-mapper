package fun.fengwk.automapper.example.controller;

import fun.fengwk.automapper.example.mapper.ExampleMapper;
import fun.fengwk.automapper.example.mapper.SimpleExampleMapper;
import fun.fengwk.automapper.example.model.ExampleDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    // curl http://localhost:8080/insertSelective?sort=10
    @GetMapping("/insertSelective")
    public String insertSelective(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "sort", required = false) Integer sort) {
        ExampleDO exampleDO = new ExampleDO();
        exampleDO.setName(name);
        exampleDO.setSort(sort);
        exampleMapper.insertSelective(exampleDO);
        return exampleDO.toString();
    }

    // curl http://localhost:8080/insertAllSelective?sort=10
    @GetMapping("/insertAll")
    public String insertAll(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "sort", required = false) Integer sort) {
        List<ExampleDO> exampleDOList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ExampleDO exampleDO = new ExampleDO();
            exampleDO.setName(name);
            exampleDO.setSort(sort);
            exampleDOList.add(exampleDO);
        }
        exampleMapper.insertAll(exampleDOList);
        return exampleDOList.toString();
    }

    // curl http://localhost:8080/insertAllSelective?sort=10
    @GetMapping("/insertAllSelective")
    public String insertAllSelective(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "sort", required = false) Integer sort) {
        List<ExampleDO> exampleDOList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ExampleDO exampleDO = new ExampleDO();
            exampleDO.setName(name);
            exampleDO.setSort(sort);
            exampleDOList.add(exampleDO);
        }
        exampleMapper.insertAllSelective(exampleDOList);
        return exampleDOList.toString();
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

    // curl http://localhost:8080/updateById?id=1&sort=1
    @GetMapping("/updateByIdSelective")
    public String updateByIdSelective(@RequestParam("id") Long id,
                                      @RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "sort", required = false) Integer sort) {
        ExampleDO exampleDO = new ExampleDO();
        exampleDO.setId(id);
        exampleDO.setName(name);
        exampleDO.setSort(sort);
        int result = exampleMapper.updateByIdSelective(exampleDO);
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
        return Arrays.toString(exampleMapper.findByNameStartingWith(name, "name asc").toArray());
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
