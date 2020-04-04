package com.bjsxt.search.service.impl;

import com.bjsxt.mapper.SolrMapper;
import com.bjsxt.pojo.Solr;
import com.bjsxt.search.service.SolrService;
import com.bjsxt.utils.Result;
import com.bjsxt.utils.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolrServiceImpl implements SolrService {
    @Autowired
    SolrMapper solrMapper;
    @Autowired
    SolrTemplate solrTemplate;
    @Value("${spring.data.solr.core}")
    private String collection;
    //第一步，向solr索引库中导入索引数据
    @Override
    public Result importSolr() {
        try {
            System.out.println("开始将数据导入索引库中");
            List<Solr> solrList = solrMapper.getSolrList();
            for(Solr solr:solrList){
                System.out.println("solr："+solr);
                SolrInputDocument document = new SolrInputDocument();
                document.setField("id",solr.getId());
                document.setField("item_title",solr.getTitle());
                document.setField("item_sell_point",solr.getSell_point());
                document.setField("item_price",solr.getPrice());
                document.setField("item_image",solr.getImage());
                document.setField("item_category_name",solr.getName());
                document.setField("item_desc",solr.getItem_desc());
                //向索引库中添加数据
                solrTemplate.saveDocument(collection,document);
            }
            //solr索引库的事务提交
            solrTemplate.commit(collection);
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("内部错误");
    }
    //在索引库中查询需要搜索的索引数据
    @Override
    public List<SolrDocument> searchList(String q, Long page, int rows) {
        System.out.println("我来设置高亮");
        //设置高亮查询条件
        HighlightQuery query = new SimpleHighlightQuery();
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(q);
        query.addCriteria(criteria);
        //设置高亮属性
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
        //分页
        query.setOffset((page-1)*rows);
        query.setRows(rows);
        //在索引库中查询符合搜索条件的索引数据
        HighlightPage<SolrDocument> highlightPage = solrTemplate.queryForHighlightPage(collection,query,SolrDocument.class);
        List<HighlightEntry<SolrDocument>> highlighted = highlightPage.getHighlighted();
        for(HighlightEntry<SolrDocument> item:highlighted){
            SolrDocument entity = item.getEntity();//实体对象，原始数据
            List<HighlightEntry.Highlight> highlights = item.getHighlights();
            //判断该实体对象中是否包含高亮的信息
            if(highlights != null && highlights.size()>0 && highlights.get(0).getSnipplets().size()>0){
                System.out.println("高亮的信息："+highlights.get(0).getSnipplets().get(0));
                //将实体对象中的item_title的值替换为高亮信息
                entity.setItem_title(highlights.get(0).getSnipplets().get(0));
            }
        }
        //返回查询到的符合搜索条件的数据
        List<SolrDocument> content = highlightPage.getContent();
        return content;
    }
}
