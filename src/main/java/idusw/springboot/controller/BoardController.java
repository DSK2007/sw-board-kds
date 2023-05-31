package idusw.springboot.controller;

import idusw.springboot.domain.Board;
import idusw.springboot.domain.Member;
import idusw.springboot.domain.PageRequestDTO;
import idusw.springboot.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/boards")
public class BoardController {
    HttpSession session = null;
    // 생성자 주입 : Spring Framework <- AutoWired (필드 주입)
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        // Spring Framework가 BoardService 객체를 주입, boardService(주입될 객체의 참조변수)
        this.boardService = boardService;
    }

    @GetMapping("/reg-form")
    public String getRegForm(PageRequestDTO pageRequestDTO, Model model, HttpServletRequest request) {
        session = request.getSession();
        Member member = (Member) session.getAttribute("mb");
        if (member != null)
        {
            model.addAttribute("board", Board.builder().build());
            return "/boards/reg-form";
        } else
            return "redirect:/members/login-form"; // 로그인이 안된 상태인 경우
    }

    @PostMapping("")
    public String postBoard(@ModelAttribute("dto") Board dto, Model model, HttpServletRequest request) {
        session = request.getSession();
        Member member = (Member) session.getAttribute("mb");
        // form에서 hidden으로 전송하는 방식으로 변경
        dto.setWriterSeq(member.getSeq());
        dto.setWriterEmail(member.getEmail());
        dto.setWriterName(member.getName());

        // login 처리하면 그냥 관계 없음
        /*
        Long seqLong = Long.valueOf(new Random().nextInt(50));
        seqLong = (seqLong == 0) ? 1L : seqLong;
        dto.setWriterSeq(seqLong);
         */
        Long bno = Long.valueOf(boardService.registerBoard(dto));

        return "redirect:/boards/" + bno; // 등록 후 목록 보기
    }

    @GetMapping("")
    public String getBoards(PageRequestDTO pageRequestDTO, Model model) { // 중간 본 수정
        model.addAttribute("list", boardService.findBoardAll(pageRequestDTO));
        return "/boards/list";
    }

    @GetMapping("/{bno}")
    public String getBoardByBno(@PathVariable("bno") Long bno, Model model) {
        Board board = boardService.findBoardById(Board.builder().bno(bno).build());
        boardService.updateBoard(board);
        model.addAttribute("dto", boardService.findBoardById(board));
        return "/boards/read";
    }

    @GetMapping("/{bno}/up-form")
    public String getUpForm(@PathVariable("bno") Long seq, Model model) {
        Board board = boardService.findBoardById(Board.builder().bno(seq).build());
        model.addAttribute("board", board);
        return "/boards/upform";
    }

    @PutMapping("/{bno}")
    public String putBoard(@ModelAttribute("board") Board board, Model model) {
        boardService.updateBoard(board);
        model.addAttribute(boardService.findBoardById(board));
        return "redirect:/boards/" + board.getBno();
    }

    @GetMapping("/{bno}/del-form")
    public String getDelForm(@PathVariable("bno") Long bno, Model model) {
        Board board = boardService.findBoardById(Board.builder().bno(bno).build());
        model.addAttribute("board", board);
        return "/boards/del-form";
    }

    @DeleteMapping("/{bno}")
    public String deleteBoard(@ModelAttribute("board") Board board, Model model) {
        boardService.deleteBoard(board);
        model.addAttribute(board);
        return "redirect:/boards";
    }

    @GetMapping(value = {"/"})
    public String getBoardList(Model model) {
        model.addAttribute("key", "value");
        return "/boards/list";
    }

}
