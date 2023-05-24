package idusw.springboot.domain;

import java.time.LocalDateTime;

public class Board {
    private Long bno; // 유일성이 있음

    private String title;
    private String content;

    private Long writerSeq;
    private String writerEmail;
    private String writerName;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
