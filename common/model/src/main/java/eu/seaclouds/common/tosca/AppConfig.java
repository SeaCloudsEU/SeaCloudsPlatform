/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.common.tosca;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.mapping.FilterValuesStrategy;
import org.elasticsearch.mapping.QueryHelper;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import alien4cloud.component.ICSARRepositorySearchService;
import alien4cloud.csar.services.CsarService;
import alien4cloud.dao.IGenericSearchDAO;
import alien4cloud.dao.model.FacetedSearchResult;
import alien4cloud.dao.model.GetMultipleDataResult;
import alien4cloud.exception.NotFoundException;
import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.components.IndexedToscaElement;
import alien4cloud.tosca.parser.ToscaParser;

@Configuration
@ComponentScan(basePackages = {"alien4cloud.topology", "alien4cloud.tosca.parser", "alien4cloud.tosca.parser.*" },
               excludeFilters = { @ComponentScan.Filter(pattern = "alien4cloud.topology.TopologyService", type = FilterType.REGEX),
                                  @ComponentScan.Filter(pattern = "alien4cloud.topology.TopologyValidationService", type = FilterType.REGEX)} )
public class AppConfig {

    @Bean(initMethod = "init")
    public ToscaParser toscaParser() {
        return new ToscaParser();
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {return new LocalValidatorFactoryBean(); }

    @Bean(name = "alien-es-dao")
    public IGenericSearchDAO mockGenericSearchDAO() { return new MockGenericSearchDao(); }

    @Bean
    public ICSARRepositorySearchService mockCSARRepositorySearchService() { return new
            MockCSARRepositorySearchService(); }

    @Bean
    public CsarService mockCSARService() { return new
            MockCSARService(); }

    private class MockGenericSearchDao implements IGenericSearchDAO {
        @Override
        public QueryHelper getQueryHelper() {
            return null;
        }

        @Override
        public String getIndexForType(Class<?> aClass) {
            return null;
        }

        @Override
        public <T> long count(Class<T> aClass, QueryBuilder queryBuilder) {
            return 0;
        }

        @Override
        public <T> long count(Class<T> aClass, String s, Map<String, String[]> map) {
            return 0;
        }

        @Override
        public <T> T customFind(Class<T> aClass, QueryBuilder queryBuilder) {
            return null;
        }

        @Override
        public <T> T customFind(Class<T> aClass, QueryBuilder queryBuilder, SortBuilder sortBuilder) {
            return null;
        }

        @Override
        public <T> List<T> customFindAll(Class<T> aClass, QueryBuilder queryBuilder) {
            return null;
        }

        @Override
        public <T> List<T> customFindAll(Class<T> aClass, QueryBuilder queryBuilder, SortBuilder sortBuilder) {
            return null;
        }

        @Override
        public GetMultipleDataResult<Object> search(QueryHelper.SearchQueryHelperBuilder searchQueryHelperBuilder, int i, int i1) {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> search(Class<T> aClass, String s, Map<String, String[]> map, int i) {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> search(Class<T> aClass, String s, Map<String, String[]> map, int i, int i1) {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> search(Class<T> aClass, String s, Map<String, String[]> map, String s1, int i, int i1) {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> search(Class<T> aClass, String s, Map<String, String[]> map, FilterBuilder filterBuilder, String s1, int i, int i1, String s2, boolean b) {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> search(Class<T> aClass, String s, Map<String, String[]> map, FilterBuilder filterBuilder, String s1, int i, int i1) {
            return null;
        }

        @Override
        public GetMultipleDataResult<Object> search(String[] strings, Class<?>[] classes, String s, Map<String, String[]> map, String s1, int i, int i1) {
            return null;
        }

        @Override
        public GetMultipleDataResult<Object> search(String[] strings, Class<?>[] classes, String s, Map<String, String[]> map, FilterBuilder filterBuilder, String s1, int i, int i1) {
            return null;
        }

        @Override
        public <T> FacetedSearchResult facetedSearch(Class<T> aClass, String s, Map<String, String[]> map, int i) {
            return null;
        }

        @Override
        public <T> FacetedSearchResult facetedSearch(Class<T> aClass, String s, Map<String, String[]> map, String s1, int i, int i1) {
            return null;
        }

        @Override
        public <T> FacetedSearchResult facetedSearch(Class<T> aClass, String s, Map<String, String[]> map, FilterBuilder filterBuilder, String s1, int i, int i1) {
            return null;
        }

        @Override
        public <T> FacetedSearchResult facetedSearch(Class<T> aClass, String s, Map<String, String[]> map, FilterBuilder filterBuilder, String s1, int i, int i1, String s2, boolean b) {
            return null;
        }

        @Override
        public GetMultipleDataResult<Object> suggestSearch(String[] strings, Class<?>[] classes, String s, String s1, String s2, int i, int i1) {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> find(Class<T> aClass, Map<String, String[]> map, int i) {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> find(Class<T> aClass, Map<String, String[]> map, int i, int i1) {
            return null;
        }

        @Override
        public Map<String, Class<?>> getTypesToClasses() {
            return null;
        }

        @Override
        public Map<String, String> getTypesToIndices() {
            return null;
        }

        @Override
        public <T> GetMultipleDataResult<T> search(Class<T> aClass, String s, Map<String, String[]> map, Map<String, FilterValuesStrategy> map1, int i) {
            return null;
        }

        @Override
        public <T> List<T> findByIdsWithContext(Class<T> aClass, String s, String... strings) {
            return null;
        }

        @Override
        public String[] selectPath(String s, Class<?>[] classes, QueryBuilder queryBuilder, SortOrder sortOrder, String s1, int i, int i1) {
            return new String[0];
        }

        @Override
        public String[] selectPath(String s, String[] strings, QueryBuilder queryBuilder, SortOrder sortOrder, String s1, int i, int i1) {
            return new String[0];
        }

        @Override
        public <T> void save(T t) {

        }

        @Override
        public <T> void save(T[] ts) {

        }

        @Override
        public <T> T findById(Class<T> aClass, String s) {
            return null;
        }

        @Override
        public <T> List<T> findByIds(Class<T> aClass, String... strings) {
            return null;
        }

        @Override
        public void delete(Class<?> aClass, String s) {

        }

        @Override
        public void delete(Class<?> aClass, QueryBuilder queryBuilder) {

        }
    }

    private class MockCSARRepositorySearchService implements ICSARRepositorySearchService {
        @Override
        public boolean isElementExistInDependencies(Class<? extends IndexedToscaElement> aClass, String s, Collection<CSARDependency> collection) {
            return false;
        }

        @Override
        public <T extends IndexedToscaElement> T getElementInDependencies(Class<T> aClass, String s, Collection<CSARDependency> collection) {
            return null;
        }

        @Override
        public <T extends IndexedToscaElement> T getRequiredElementInDependencies(Class<T> aClass, String s, Collection<CSARDependency> collection) throws NotFoundException {
            return null;
        }

        @Override
        public <T extends IndexedToscaElement> T getParentOfElement(Class<T> aClass, T t, String s) {
            return null;
        }
    }

    private class MockCSARService extends CsarService {
    }

}
