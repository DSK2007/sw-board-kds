package idusw.springboot.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "sw_board_b202112045")

@Builder
@AllArgsConstructor
@NoArgsConstructor
// @Data // ==  @ToString, @EqualsAndHashCode, @Getter @Setter @RequiredArgsConstructor
// JPA Auditing 을 활용하여서 생성한사람, 생성일자, 수정한사람, 수정일자 등을 선택하여서 감사
@Getter
@ToString(exclude = "writer")

public class BoardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sw_board_b202112045_seq_gen")
    @SequenceGenerator(sequenceName = "sw_board_b202112045_seq", name = "sw_board_b202112045_seq_gen", allocationSize = 1)
    // Oracle : GenerationType.SEQUENCE, Mysql/MariaDB : GenerationType.IDENTITY, auto_increment
    private Long bno; // 유일키

    @Column(length = 50, nullable = false)
    private String title; // 제목
    @Column(length = 1000, nullable = false)
    private String content; // 내용

    @ManyToOne
    // @JoinColumn(name = "seq")
    private MemberEntity writer;  // BoardEntity : MemberEntity = N : 1
}
