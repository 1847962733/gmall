package com.atguigui.gmall.search.repository;

import com.atguigui.gmall.search.bean.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
