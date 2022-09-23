package com.anf.core.services.impl;

import com.anf.core.services.GetPageList;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Component(immediate = true, service = GetPageList.class)
public class GetPageListWithPropertyImpl implements GetPageList {

    @Reference
    private QueryBuilder queryBuilder;

    private static Logger LOGGER = LoggerFactory.getLogger(GetPageListWithPropertyImpl.class);


    public List<String> getTopTenPageListUsingQueryBuilder(SlingHttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("path", "/content/anf-code-challenge/us/en");
        params.put("type", "cq:Page");
        params.put("p.offset", "0");
        params.put("p.limit", "10");
        params.put("1_property", JcrConstants.JCR_CONTENT + "/anfCodeChallenge");
        params.put("1_property.value","true");


        Session session = null;
        try {
            session = request.getResource().getResourceResolver().adaptTo(Session.class);
            Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);

            SearchResult searchResult = query.getResult();

            Node resultNode = null;
            List<String> pages = new ArrayList<>();

            // iterate through results and consolidate page paths
            if (null != searchResult) {
                final Iterator<Node> nodeItr = searchResult.getNodes();
                while (nodeItr.hasNext()) {
                    resultNode = nodeItr.next();
                    pages.add(resultNode.getPath());
                }
            }
            return pages;
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (session.isLive() || session != null) {
                session.logout();
            }
        }
        return null;
    }

    public List<String> getTopTenPageListUsingSQL2(SlingHttpServletRequest request) {
        final String sql2Query =
                "SELECT parent.* FROM [cq:Page] AS parent \n"
                + "INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,parent) \n"
                + "WHERE ISDESCENDANTNODE(parent, '/content/anf-code-challenge/us/en') AND child.[anfCodeChallenge] = 'true'";
        ResourceResolver resolver = request.getResourceResolver();
        Iterator<Resource> result = resolver.findResources(sql2Query, javax.jcr.query.Query.JCR_SQL2);
        List<String> pagePaths = new ArrayList<>();
        result.forEachRemaining(resource -> pagePaths.add(resource.getPath()));

        return pagePaths;
    }

}
