package com.anf.core.services;

import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

public interface GetPageList {
    public List<String> getTopTenPageListUsingQueryBuilder(SlingHttpServletRequest request);

    public List<String> getTopTenPageListUsingSQL2(SlingHttpServletRequest request);
}
