package com.heima.article;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest(classes = ArticleApplication.class)
@ComponentScan(basePackages = "com.heima.article")
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;
    @Autowired
    private FileStorageService fileStorageService;
    @Test
    public void createStaticUrlTest() {
        Long acId=1691284001146327042L;
        //1.获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, acId));
        ApArticle apArticle = apArticleMapper.selectOne(Wrappers.<ApArticle>lambdaQuery().eq(ApArticle::getId, acId));
        articleFreemarkerService.buildArticleToMinIO(apArticle,apArticleContent.getContent());
    }

    @Test
    public void fileUploadTest() {
        //1.获取文章内容
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("D:\\javaDevelop\\SpringCloud\\chapter11\\plugins\\css\\index.css");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String path =  fileStorageService.uploadHtmlFile("plugins/css","index.css",fileInputStream);
        System.out.println(path);
    }
}