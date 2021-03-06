package com.macgregor.ef.resource;

import com.macgregor.ef.dao.AbstractEFDAO;
import com.macgregor.ef.exceptions.PageinationException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResource<T> {
    public static final String TOTAL_COUNT_HEADER = "X-total-count";
    public static final String LINK_HEADER = "Link";
    public static final int MAX_PAGE_SIZE = 100;

    protected final AbstractEFDAO resourceDAO;

    public AbstractResource(AbstractEFDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }

    public List<T> getPage( HttpServletResponse response,
                           Integer page,
                           Integer size) throws PageinationException {

        int total = resourceDAO.count();
        response.addHeader(TOTAL_COUNT_HEADER, Integer.toString(total));

        sanityCheckPagenationParameters(page, size, total);

        List<Link> links = getPagenationLinks(page, size, total);
        response.addHeader(LINK_HEADER, links.toString());

        return resourceDAO.page(page, size);
    }

    public T get(Integer id){
        return (T) resourceDAO.findById(id);
    }

    public abstract String getPageLinkBaseURI();

    protected void sanityCheckPagenationParameters(int page, int size, int total) throws PageinationException {
        List<String> errors = new ArrayList<>();

        if(size > MAX_PAGE_SIZE){
            errors.add(String.format("Page size must be less than %d", MAX_PAGE_SIZE));
            size = 10;
        }

        if(page < 1 || page > maxPage(size, total)){
            errors.add(String.format("Page but be within 1 and %d (upper bound changes based on the total number of entities and page size you are querying)", maxPage(size, total)));
        }



        if(errors.size() > 0){
            throw new PageinationException(String.format("%s:\n %s", "Pagination parameters out of bounds", errors.toString()));
        }
    }

    protected List<Link> getPagenationLinks(int page, int size, int total){
        List<Link> links = new ArrayList<>(5);
        Link self = Link.fromUri("?page={page}&size={size}")
                .rel("self")
                .type("text/plain")
                .baseUri(getPageLinkBaseURI())
                .build(page, size);
        links.add(self);

        if(page > 1) {
            Link prev = Link.fromUri("?page={page}&size={size}")
                    .rel("prev")
                    .type("text/plain")
                    .baseUri(getPageLinkBaseURI())
                    .build(page-1, size);
            links.add(prev);
        }

        if(page*size < total) {
            Link next = Link.fromUri("?page={page}&size={size}")
                    .rel("next")
                    .type("text/plain")
                    .baseUri(getPageLinkBaseURI())
                    .build( page+1, size);
            links.add(next);
        }

        Link first = Link.fromUri("?page={page}&size={size}")
                .rel("first")
                .type("text/plain")
                .baseUri(getPageLinkBaseURI())
                .build(1, size);
        links.add(first);

        Link last = Link.fromUri("?page={page}&size={size}")
                .rel("last")
                .type("text/plain")
                .baseUri(getPageLinkBaseURI())
                .build(maxPage(size, total), size);
        links.add(last);

        return links;
    }

    protected int maxPage(int size, int total){
        return (int) (Math.ceil(total / size));
    }
}
