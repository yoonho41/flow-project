package com.flow.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.flow.project.dto.BlockedExtensionsDTO;
import com.flow.project.dto.UsersDTO;

@Mapper
public interface ProjectMapper {
    
    List<BlockedExtensionsDTO> findAllBlockedExtensions();

    List<UsersDTO> findUser(@Param("id")String id, @Param("pwd")String pwd);


    List<String> findAllExtensions();

    int findExtension(String name);

    int insertExtension(String name);

    int deleteExtension(String name);

}
