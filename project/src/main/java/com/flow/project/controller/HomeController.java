package com.flow.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.flow.project.dto.BlockedExtensionsDTO;
import com.flow.project.dto.UsersDTO;
import com.flow.project.service.ProjectService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final ProjectService projectService;

    @GetMapping("/")
    public String home(Model model) {

        List<BlockedExtensionsDTO> list = projectService.findAllBlockedExtensions();

        model.addAttribute("list", list);

        return "index";
    }
    
    // 로그인
    @PostMapping("/login")
    public String login(Model model, UsersDTO usersDTO, HttpSession session) {
        
        String id = usersDTO.getId();
        String pwd = usersDTO.getPwd();

        List<UsersDTO> list = projectService.findUser(id,pwd);
        if (list.isEmpty() || list==null || list.size()==0) {
            model.addAttribute("msg","login failed");
            return "redirect:/";
        } else if (list.size()>1) {
            model.addAttribute("msg", "duplicate user");
            return "redirect:/";
        }

        // select 결과가 하나이므로 list에서 index 0의 UsersDTO 정보 가져오기
        UsersDTO loginUser = list.get(0);

        session.setAttribute("loginUserName", loginUser.getName());
        session.setAttribute("loginUserRole", loginUser.getRole());
        model.addAttribute("msg", "login success");

        return "redirect:/";
    }
    
    // 로그아웃
    @GetMapping("/logout")
    public String logout(Model model, HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }


    // 확장자 차단 페이지로 이동
    @GetMapping("/block")
    public String block(Model model, HttpSession session) {

        Object role = session.getAttribute("loginUserRole");

        // role이 없거나 1이 아니면 접근 제한 (role==1 이면 관리자)
        if (role == null || !"1".equals(String.valueOf(role))) {
            model.addAttribute("msg", "접근 권한이 없습니다.");
            model.addAttribute("redirectUrl", "/");
            return "alert-redirect";
        }

        List<String> extensions = projectService.findAllExtensions();
        model.addAttribute("extensions", extensions);

        return "block";
    }


    // (AJAX 호출용) 확장자 중복이면 등록하지 않고, 아니면 DB에 등록
    @PostMapping("/api/extensions")
    @ResponseBody
    public ResponseEntity<?> insertExtension(@RequestParam("name") String name) {

        String ext = normalize(name);
        if (ext.isEmpty()) return ResponseEntity.badRequest().body("invalid");

        if (projectService.findExtension(ext) > 0) {
            return ResponseEntity.ok().body("duplicate");
        }

        projectService.insertExtension(ext);

        return ResponseEntity.ok().body("ok");
    }

    // (AJAX 호출용) 확장자를 DB에서 삭제
    @DeleteMapping("/api/extensions")
    @ResponseBody
    public ResponseEntity<?> deleteExtension(@RequestParam("name") String name) {

        String ext = normalize(name);

        if (ext.isEmpty()) {
            return ResponseEntity.badRequest().body("invalid");
        }

        projectService.deleteExtension(ext);

        return ResponseEntity.ok().body("ok");
    }

    // 화면(block.html)에 있던 normalize 함수 구현
    private String normalize(String v) {
        if (v == null) return "";
        v = v.trim().toLowerCase();
        if (v.startsWith(".")) v = v.substring(1);
        if (!v.matches("^[a-z0-9]+$")) return "";
        return v;
    }



    // 파일 업로드 화면으로 이동
    @GetMapping("/upload")
    public String upload(Model model) {

        List<String> extensions = projectService.findAllExtensions();
        model.addAttribute("extensions", extensions);

        return "upload";
    }
    

}
