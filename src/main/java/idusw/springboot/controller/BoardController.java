package idusw.springboot.controller;

import idusw.springboot.domain.Board;
import idusw.springboot.domain.Member;
import idusw.springboot.domain.PageRequestDTO;
import idusw.springboot.domain.PageResultDTO;
import idusw.springboot.entity.BoardEntity;
import idusw.springboot.entity.MemberEntity;
import idusw.springboot.service.BoardService;
import idusw.springboot.service.MemberService;
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
        if (member != null) {
            if (session.getAttribute("abandon").equals(0)) {
                model.addAttribute("board", Board.builder().writerSeq(member.getSeq()).writerName(member.getName()).writerEmail(member.getEmail()).build());
                return "/boards/reg-form";
            } else
                return "redirect:/boards"; // 차단된 멤버인 경우
        } else
            return "redirect:/members/login-form"; // 로그인이 안된 상태인 경우
    }

    @PostMapping("")
    public String postBoard(@ModelAttribute("dto") Board dto, Model model, HttpServletRequest request) {
        session = request.getSession();
        Member member = (Member) session.getAttribute("mb");
        if (member != null) {
        // form에서 hidden으로 전송하는 방식으로 변경 (했음)
        // dto.setWriterSeq(member.getSeq());
        // dto.setWriterEmail(member.getEmail());
        // dto.setWriterName(member.getName());

        Long bno = Long.valueOf(boardService.registerBoard(dto));

        return "redirect:/boards"; // 등록 후 목록 보기
        } else {
            return "redirect:/members/login-form";
        }

    }
/*
    @GetMapping("")
    public String getBoards(PageRequestDTO pageRequestDTO, Model model) { // 중간 본 수정
        model.addAttribute("list", boardService.findBoardAll(pageRequestDTO));
        return "/boards/list";
    }
*/

    @GetMapping(value = {"", "/"})
    public String getBoards(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            @RequestParam(value = "per-Page", required = false, defaultValue = "8") int perPage,
                            @RequestParam(value = "per-Pagination", required = false, defaultValue = "5") int perPagination,
                            @RequestParam(value = "type", required = false, defaultValue = "0") String type,
                            @RequestParam(value = "keyword", required = false, defaultValue = "" + "@") String keyword,
                            Model model) { // 중간 본 수정
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .perPage(perPage)
                .perPagination(perPagination)
                .type(type)
                .keyword(keyword)
                .build();
        PageResultDTO<Board, Object[]> resultDTO = boardService.findBoardAll(pageRequestDTO);
        if(resultDTO != null) {
            // model.addAttribute("list", boardService.findBoardAll(pageRequestDTO));
            model.addAttribute("result", resultDTO);
            return "/boards/list";
        }
        else
            return "/errors/404";
    }

    @GetMapping("/{bno}")
    public String getBoardByBno(@PathVariable("bno") Long bno, Model model) {
        Board board = boardService.findBoardById(Board.builder().bno(bno).build());
        boardService.updateBoard(board);
        model.addAttribute("board", boardService.findBoardById(board));
        return "/boards/detail";
    }

    @GetMapping("/{bno}/up-form")
    public String getUpForm(@PathVariable("bno") Long seq, Model model, HttpServletRequest request) {
        session = request.getSession();
        Member member = (Member) session.getAttribute("mb");
        if (member != null) {
            Board board = boardService.findBoardById(Board.builder().bno(seq).build());
            if (member.getSeq().equals(board.getWriterSeq())) {
                if (session.getAttribute("abandon").equals(0)) {
                    model.addAttribute("board", board);
                    return "/boards/up-form";
                } else
                    return "redirect:/boards"; // 차단된 멤버인 경우
            } else
                return "redirect:/boards"; // 로그인이 되어있으나 작성자가 아닌 멤버인 경우
        } else
            return "redirect:/members/login-form"; // 로그인이 안된 상태인 경우
    }

    @PutMapping("/{bno}")
    public String putBoard(@ModelAttribute("board") Board board, Model model) {
        boardService.updateBoard(board);
        model.addAttribute(boardService.findBoardById(board));
        return "redirect:/boards/" + board.getBno();
    }

    @GetMapping("/{bno}/del-form")
    public String getDelForm(@PathVariable("bno") Long bno, Model model, HttpServletRequest request) {
        session = request.getSession();
        Member member = (Member) session.getAttribute("mb");
        if (member != null) {
            Board board = boardService.findBoardById(Board.builder().bno(bno).build());
            if (member.getSeq().equals(board.getWriterSeq())) {
                if (session.getAttribute("abandon").equals(0)) {
                    model.addAttribute("board", board);
                    return "/boards/del-form";
                } else
                    return "redirect:/boards"; // 차단된 멤버인 경우
            } else
                return "redirect:/boards"; // 로그인이 되어있으나 작성자가 아닌 멤버인 경우
        } else
            return "redirect:/members/login-form"; // 로그인이 안된 상태인 경우
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
