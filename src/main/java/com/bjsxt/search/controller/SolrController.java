package com.bjsxt.search.controller;

import com.bjsxt.search.service.SolrService;
import com.bjsxt.utils.Result;
import com.bjsxt.utils.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SolrController {
    @Autowired
    SolrService solrService;

    //向索引库中导入数据
    @RequestMapping("/importSolr")
    public Result importSolr(){
        return solrService.importSolr();
    }

    //向索引库查询符合搜索条件的数据
    @RequestMapping("/list")
    public List<SolrDocument> searchList(String q, @RequestParam(defaultValue = "1") Long page,@RequestParam(defaultValue = "10") int rows){
        try {
            return solrService.searchList(q, page, rows);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
