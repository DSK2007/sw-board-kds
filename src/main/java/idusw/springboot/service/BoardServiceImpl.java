package idusw.springboot.service;

import idusw.springboot.domain.Board;
import idusw.springboot.domain.PageRequestDTO;
import idusw.springboot.domain.PageResultDTO;
import idusw.springboot.entity.BoardEntity;
import idusw.springboot.entity.MemberEntity;
import idusw.springboot.repository.BoardRepository;
import idusw.springboot.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService{
    private BoardRepository boardRepository;
    private ReplyRepository replyRepository;

    @Override
    public int registerBoard(Board board) {
        BoardEntity entity = dtoToEntity(board);

        if(boardRepository.save(entity) != null) {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public Board findBoardById(Board board) {
        Object[] arr = (Object[]) boardRepository.getBoardByBno(board.getBno());
        return entityToDto((BoardEntity) arr[0], (MemberEntity) arr[1], (Long) arr[2]);
    }

    @Override
    public PageResultDTO<Board, Object[]> findBoardAll(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("bno").descending());
        Page<Object[]> result = boardRepository.searchPage(
                pageRequestDTO.getType(),
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getPageable(Sort.by("bno").descending()));
        Function<Object[], Board> fn = (entity -> entityToDto((BoardEntity) entity[0], (MemberEntity) entity[1], (Long) entity[2]));
        return new PageResultDTO<>(result, fn, 5);
    }

    @Override
    public int updateBoard(Board board) {
        return 0;
    }

    @Transactional
    @Override
    public int deleteBoard(Board board) {
        replyRepository.deleteByBno(board.getBno());
        boardRepository.deleteById(board.getBno());
        return 0;
    }
}
