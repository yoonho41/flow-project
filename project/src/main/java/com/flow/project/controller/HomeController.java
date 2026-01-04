package com.flow.project.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;
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
    
    @PostMapping("/login")
    public String login(Model model, UsersDTO usersDTO, HttpSession session) {
        
        String id = usersDTO.getId();
        String pwd = usersDTO.getPwd();

        System.out.println("Login ID : "+id);
        System.out.println("Login PWD : "+pwd);

        List<UsersDTO> list = projectService.findUser(id,pwd);
        if (list.isEmpty() || list==null || list.size()==0) {
            model.addAttribute("msg","login failed");
            System.out.println("검색 결과 없음");
            return "redirect:/";
        } else if (list.size()>1) {
            model.addAttribute("msg", "duplicate user");
            System.out.println("검색 결과 2개 이상");
            return "redirect:/";
        }
        System.out.println("검색 성공");

        UsersDTO loginUser = list.get(0);

        System.out.print("로그인 한 사람 이름 : "+loginUser.getName());

        session.setAttribute("loginUserName", loginUser.getName());
        session.setAttribute("loginUserRole", loginUser.getRole());
        model.addAttribute("msg", "login success");

        return "redirect:/";
    }
    
    @GetMapping("/logout")
    public String logout(Model model, HttpSession session) {

        session.invalidate();

        return "redirect:/";

    }



    @GetMapping("/block")
    public String block(Model model) {

        List<String> extensions = projectService.findAllExtensions();
        model.addAttribute("extensions", extensions);

        return "block";
    }


    // ✅🔧 2) 체크 시: 없으면 insert (중복 방지)
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

    // ✅🔧 3) 해제 시: delete
    @DeleteMapping("/api/extensions")
    @ResponseBody
    public ResponseEntity<?> deleteExtension(@RequestParam("name") String name) {
        String ext = normalize(name);
        if (ext.isEmpty()) return ResponseEntity.badRequest().body("invalid");

        projectService.deleteExtension(ext);
        return ResponseEntity.ok().body("ok");
    }

    // ✅🔧 (화면용 최소 정규화)
    private String normalize(String v) {
        if (v == null) return "";
        v = v.trim().toLowerCase();
        if (v.startsWith(".")) v = v.substring(1);
        if (!v.matches("^[a-z0-9]+$")) return "";
        return v;
    }


}
