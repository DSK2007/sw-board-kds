package idusw.springboot.controller;

import idusw.springboot.domain.Member;
import idusw.springboot.domain.PageRequestDTO;
import idusw.springboot.domain.PageResultDTO;
import idusw.springboot.entity.MemberEntity;
import idusw.springboot.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/members")
public class MemberController {
    // Constructor DI(Dependency Injection)
    MemberService memberService;
    public MemberController(MemberService memberService) { // Spring Framework이 주입(하도록 요청함)
        this.memberService = memberService;
    }
    HttpSession session = null;

    // @GetMapping("/list/{pn}/{size}")
    // public String listMemberPagination(@PathVariable("pn") int pn, @PathVariable("size") int size, Model model)
    @GetMapping(value = {"", "/"}) // ?page=&perPage=, 주의: 다른 메소드가 이런 링크를 가져서는 안됨. 있을 시 주석으로 만들 것.
    public String listMemberPagination(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "per-Page", required = false, defaultValue = "10") int perPage,
                                       @RequestParam(value = "per-Pagination", required = false, defaultValue = "5") int perPagination,
                                       @RequestParam(value = "type", required = false, defaultValue = "0") String type,
                                       @RequestParam(value = "keyword", required = false, defaultValue = "" + "@") String keyword,
                                       Model model,
                                       HttpServletRequest request) {
        session = request.getSession();
        Member member = (Member) session.getAttribute("mb");
        if (member != null) {
            if (session.getAttribute("admin").equals(1))
            {
                PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                        .page(page)
                        .perPage(perPage)
                        .perPagination(perPagination)
                        .type(type)
                        .keyword(keyword)
                        .build();
                PageResultDTO<Member, MemberEntity> resultDTO = memberService.getList(pageRequestDTO);
                if(resultDTO != null) {
                    // model.addAttribute("list", resultDTO.getDtoList()); // record
                    model.addAttribute("result", resultDTO); // page number list
                    return "/members/list"; // view : template engine - thymeleaf .html
                }
                else
                    return "/errors/404"; // 오류 발생 시
            } else
                return "/errors/404"; // 관리자 계정이 아닌 경우
        } else
            return "redirect:/members/login-form"; // 로그인을 안 한 경우
    }

    @GetMapping("/login-form")
    public String getLoginform(Model model) {
        model.addAttribute("member", Member.builder().build()); // email / pw 전달을 위한 객체
        return "/members/login"; // view : template engine - thymeleaf .html
    }
    @PostMapping("/login")
    public String loginMember(@ModelAttribute("member") Member member, HttpServletRequest request) { // 로그인 처리 -> service -> repository -> service -> controller
        Member result = null;
        if((result = memberService.login(member)) != null ) { // 정상적으로 레코드의 변화가 발생하는 경우 영향받는 레코드 수를 반환
            session = request.getSession();
            session.setAttribute("mb", result);
            if (result.getEmail().equals("root202112045@induk.ac.kr")) {
                session.setAttribute("admin", 1);
            } else {
                session.setAttribute("admin", 0);
            }
            if (result.getAbandon() == 1) {
                session.setAttribute("abandon", 1);
            } else {
                session.setAttribute("abandon", 0);
            }
            return "redirect:/";
        }
        else
            return "/errors/404";
    }
    @GetMapping("/logout")
    public String logoutMember() {
        session.invalidate();
        return "redirect:/";
    }
    /*
    @GetMapping(value = {"", "/"})
    public String listMember(Model model) {
        List<Member> result = null;
        if((result = memberService.readList()) != null) {
            model.addAttribute("list", result);
            return "/members/list";
        }
        else
            return "/errors/404";
    }
     */

    /*
    @GetMapping("/list")
    public String listMember2(Model model) {
        List<Member> result = null;
        if((result = memberService.readList()) != null) {
            model.addAttribute("list", result);
            return "/members/list2";
        }
        else
            return "/errors/404";
    }
     */
    @GetMapping("/register-form")
    public String getRegisterForm(Model model) { // form 요청 -> view (template engine)
        model.addAttribute("member", Member.builder().build());
        return "/members/register";
    }
    @PostMapping("/register")
    public String createMember(@ModelAttribute("member") Member member, Model model) { // 등록 처리 -> service -> repository -> service -> controller
        int cnt = memberService.checkEmail(member);
        if (cnt == 0) { // 기존 계정들과 중복되는 이메일이 아님
            cnt = memberService.checkPhone(member);
            if (cnt == 0) { // 기존 계정들과 중복되는 전화번호가 아님
                if(memberService.create(member) > 0 ) // 정상적으로 레코드의 변화가 발생하는 경우 영향받는 레코드 수를 반환
                    return "redirect:/"; // 성공
                else
                    return "/errors/404"; // 실행 중 오류, 페이지 참조
            } else
                return "/errors/404"; // 폼 전달에서 전화 번호 중복을 막지 못했음
        } else
            return "/errors/404"; // 폼 전달에서 이메일 중복을 막지 못했음
    }
    @GetMapping("/{seq}")
    public String getMember(@PathVariable("seq") Long seq, Model model) {
        Member result = new Member(); // 반환
        Member m = new Member(); // 매개변수로 전달
        m.setSeq(seq);
        result = memberService.read(m);
        // MemberService가 MemberRepository에게 전달
        // MemberRepository는 JpaRepository 인터페이스의 구현체를 활용할 수 있음
        model.addAttribute("member", result);
        return "/members/detail";
    }

    @PutMapping("/{seq}")
    public String updateMember(@ModelAttribute("member") Member member, Model model) { // 수정 처리 -> service -> repository -> service -> controller
        if(memberService.update(member) > 0 ) {
            session.setAttribute("mb", member);
            return "redirect:/";
        }
        else
            return "/errors/404";
    }

    @GetMapping("/admin/{seq}")
    public String getMemberByAdmin(@PathVariable("seq") Long seq, Model model) {
        Member result = new Member(); // 반환
        Member m = new Member(); // 매개변수로 전달
        m.setSeq(seq);
        result = memberService.readByAdmin(m);
        // MemberService가 MemberRepository에게 전달
        // MemberRepository는 JpaRepository 인터페이스의 구현체를 활용할 수 있음
        model.addAttribute("member", result);
        return "/members/admin-detail";
    }

    @PutMapping("/admin/{seq}")
    public String updateMemberByAdmin(@ModelAttribute("member") Member member, Model model) { // 수정 처리 -> service -> repository -> service -> controller
        if(memberService.update(member) > 0 ) {
            session.setAttribute("mb", member);
            return "redirect:/members";
        }
        else
            return "/errors/404";
    }

    @DeleteMapping("/{seq}")
    public String deleteMember(@ModelAttribute("member") Member member) { // 삭제 처리 -> service -> repository -> service -> controller
        if(memberService.delete(member) > 0) {
            session.invalidate();
            return "redirect:/";
        }
        else
            return "/errors/404";
    }
    @GetMapping("/forgot") // 조회 read
    public String getForgotform() { // 분실 비밀번호 처리 요청 -> view
        return "/members/forgot-password"; // view : template engine - thymeleaf .html
    }
    @PostMapping("/forgot") // create vs  update -> @PutMapping, delete -> @DeleteMapping
    public String forgotMemberPassword() { // 비밀번호(갱신) -> service -> repository -> service -> controller
        return "redirect:/"; // 루트로 이동
    }

    @PostMapping("/check-email")
    @ResponseBody
    public int checkEmail(@RequestParam("email") String email) {
        Member member = Member.builder().email(email).build();
        int cnt = memberService.checkEmail(member);
        System.out.println("check-email" + email + " : " + cnt);
        return cnt;
    }

    @PostMapping("/check-phone")
    @ResponseBody
    public int checkPhone(@RequestParam("phone") String phone) {
        Member member = Member.builder().phone(phone).build();
        int cnt = memberService.checkPhone(member);
        System.out.println("check-phone" + phone + " : " + cnt);
        return cnt;
    }
}
