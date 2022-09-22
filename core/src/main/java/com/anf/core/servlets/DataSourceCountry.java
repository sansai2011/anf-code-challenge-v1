/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.anf.core.servlets;

import org.apache.sling.api.wrappers.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="datasource/countrylist",
        methods="GET"
)
public class DataSourceCountry extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static Logger LOGGER = LoggerFactory.getLogger(DataSourceCountry.class);
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) {
        try {
            ResourceResolver resourceResolver = req.getResourceResolver();
            List<CountryItem> countryList = new ArrayList<>();
            Resource currRes = req.getResource();
            String jsonFilePath = currRes.getChild("datasource").getValueMap().get("damSrcPath", String.class);
            Resource damResource = req.getResourceResolver().getResource(jsonFilePath);
            JSONObject jsonOut = getJsonFromDamFile(damResource);
            jsonOut.keys().forEachRemaining(key -> {
                try {
                    countryList.add(new CountryItem(jsonOut.getString((String) key),(String) key));
                } catch (JSONException e) {
                    LOGGER.error("Error while parsing json", e);
                }
            });
            LOGGER.debug("countryList ::{}", countryList);
            DataSource ds = getCountryListFromDataSource(resourceResolver, countryList);
            req.setAttribute(DataSource.class.getName(), ds);

        } catch (Exception e) {
            LOGGER.error("Error in Get Drop Down Values", e);
        }
    }

    private static DataSource getCountryListFromDataSource(ResourceResolver resourceResolver, List<CountryItem> countryList) {
        DataSource ds = new SimpleDataSource(new TransformIterator(countryList.iterator(), input -> {
            CountryItem CountryItem = (CountryItem) input;
            ValueMap vmap = new ValueMapDecorator(new HashMap<>());
            vmap.put("value", CountryItem.key);
            vmap.put("text", CountryItem.value);
            return new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vmap);
        }));
        return ds;
    }

    private JSONObject getJsonFromDamFile(Resource resource) {
        JSONObject jsonObj = new JSONObject();
        try {
            Node _jcr_content = resource.adaptTo(Node.class).getNode("jcr:content");
            InputStream inputStream = _jcr_content.getProperty("jcr:data").getBinary().getStream();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            jsonObj = new JSONObject(sb.toString());
            LOGGER.debug("getJsonFromDamFile :: {}", jsonObj.toString());
        } catch (RepositoryException | JSONException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jsonObj;
    }
}

