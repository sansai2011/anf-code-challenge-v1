package com.anf.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class NewsFeedModelTest {

    private static final String RES_JSON = "/dummy-feed.json";
    private static final String DEST_PATH = "/var";
    private static final String CURR_RES = "/var/newsList";

    private final AemContext aemContext = new AemContext();

    @InjectMocks
    NewsFeedModel model;

    @BeforeEach
    void setUp() throws Exception {
        aemContext.addModelsForClasses(NewsFeedModel.class);
        aemContext.load().json(RES_JSON, DEST_PATH);
        model = aemContext.currentResource(CURR_RES).adaptTo(NewsFeedModel.class);
    }

    @Test
    void testDefaultPathSrc() {
        assertEquals("/var/data", model.getNewsFeedPath());
    }

    @Test
    void testNewsFeed() {
        model.newsFeedPath="/var/data";
        assertEquals(5, model.getNewsArticleList().size());
    }
}