package com.bjsxt.search.service;

import com.bjsxt.utils.Result;
import com.bjsxt.utils.SolrDocument;

import java.util.List;

public interface SolrService {
    //向solr索引库中导入搜索需要用到的索引数据
    Result importSolr();
    //向solr索引库中查询需要搜索的数据
    List<SolrDocument> searchList(String q,Long page,int rows);
}
