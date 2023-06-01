// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingmoqo.dto;

import java.io.Serializable;

public class PaginationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalPages;
    private Long currentPage;
    private Long nextPage;
    private Long prevPage;

    public PaginationDto() {
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Long getNextPage() {
        return nextPage;
    }

    public void setNextPage(Long nextPage) {
        this.nextPage = nextPage;
    }

    public Long getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(Long prevPage) {
        this.prevPage = prevPage;
    }

    @Override
    public String toString() {
        return "Pagination [totalPages=" + totalPages + ", currentPage=" + currentPage + ", nextPage=" + nextPage + ", prevPage=" + prevPage + "]";
    }

}
