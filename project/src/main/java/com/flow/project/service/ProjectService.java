package com.flow.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flow.project.dto.BlockedExtensionsDTO;
import com.flow.project.dto.UsersDTO;
import com.flow.project.mapper.ProjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectMapper projectMapper;

    public List<BlockedExtensionsDTO> findAllBlockedExtensions() {
        return projectMapper.findAllBlockedExtensions();
    }

    public List<UsersDTO> findUser(String id, String pwd) {
        return projectMapper.findUser(id, pwd);
    }


    public List<String> findAllExtensions() {
        return projectMapper.findAllExtensions();
    }

    public int findExtension(String name) {
        return projectMapper.findExtension(name);
    }

    public int insertExtension(String name) {
        return projectMapper.insertExtension(name);
    }

    public int deleteExtension(String name) {
        return projectMapper.deleteExtension(name);
    }

}
