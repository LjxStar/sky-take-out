package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anonotation.AutoFill;
import com.sky.enumeration.OperationType;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 保存员工信息
     * @param employee
     */
    @Insert("insert into employee(username, password, name, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "values(#{username}, #{password}, #{name}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Employee employee);

    /**
     * 分页查询员工信息
     * @param name
     * @return
     */
    Page<Employee> pageQuery(String name);

    /**
     * 根据id修改员工信息
     * @param employee
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
