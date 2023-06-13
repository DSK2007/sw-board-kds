package idusw.springboot.domain;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@ToString
@EqualsAndHashCode

public class Member {
    private Long seq;
    private String email;
    private String name;
    private String phone;
    private String pw;
    private int abandon;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
