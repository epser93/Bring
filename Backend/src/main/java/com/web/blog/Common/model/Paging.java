package com.web.blog.Common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Paging {

    public static final int COUNT_OF_PAGING_CONTENTS = 8;
    public static final int COUNT_OF_PAGING = 5;

    private int pageNo;
    private int prev;
    private int next;
    private List<Integer> pages;

    public Paging(Long pageNo) {
        this.pageNo = 1;
        if(pageNo != null) {
            this.pageNo = pageNo.intValue();
        }
        this.pages = new ArrayList<>();
        this.prev = 0;
        this.next = 0;
    }

    public Paging of(long countOfContents) {
        int start = obtainStartPage();
        int end = obtainEndPage(countOfContents);
        int total = obtainTotalPage(countOfContents);

        for (int i = start; i <= end; i++) {
            this.pages.add(i);
        }

        if(start > 1) {
            this.prev = start - 1;
        }

        if(end != total) {
            this.next = end + 1;
        }

        return this;
    }

    public int obtainStartPage() {
        return (this.pageNo / (COUNT_OF_PAGING + 1)) * COUNT_OF_PAGING + 1;
    }

    public int obtainEndPage(long countOfContents) {
        int total = obtainTotalPage(countOfContents);
        if(total < obtainStartPage() + COUNT_OF_PAGING - 1) {
            return total;
        }
        return obtainStartPage() + COUNT_OF_PAGING - 1;
    }

    public int obtainTotalPage(long countOfContents) {
        return (int)Math.ceil((double)countOfContents / COUNT_OF_PAGING_CONTENTS);
    }
}