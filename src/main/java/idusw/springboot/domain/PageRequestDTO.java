package idusw.springboot.domain;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestDTO {
    private int page; // 요청하는 페이지
    private int size; // 페이지 당 게시물 수

    public PageRequestDTO() {
        this.page = 1;
        this.size = 5;
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
}
