package com.edu_management.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.edu_management.beans.student;

public interface StudentMapper {
    @Select("select * from CourseSystem.students where stuID=#{stuID}")
    public student selectInfoById(String stuID);

    //已选课程查询
    @Select("select courseName,courseID,courseContain,needMedia,courseFrequency,teaName from sc,course,teachers where sc.sc_courseID = course.courseID and course.teaID=teachers.teaID and sc.sc_stuID=#{stuID}")
    public List<Map<String, Object>> selectScById(String stuID);

    //可选课程查询
    @Select("SELECT courseName,courseID,courseContain,courseContainRest,needMedia,courseFrequency,teaName\r\n" +
            "FROM course,teachers\r\n" +
            "WHERE course.teaID=teachers.teaID AND pass=1 AND courseID NOT IN(\r\n" +
            "SELECT sc_courseID FROM sc WHERE sc_stuID=#{stuID}\r\n" +
            ")")
    public List<Map<String, Object>> selectCourseCanChoose(String stuID);

    @Select("select courseContainRest from course where courseID=#{courseID}")
    public Map<String, Integer> selectCcrById(int courseID);

    //课程余量-1
    @Update("update course set courseContainRest=#{rest}-1 where courseID=#{courseID}")
    public void updateCcrById(int rest, int courseID);

    //学生选课
    @Insert("insert into sc values(#{sc_courseID},#{sc_stuID})")
    public int studentCourseChoose(int sc_courseID, String sc_stuID);

    //学生退选
    @Delete("delete from sc where sc_courseID=#{sc_courseID} and sc_stuID=#{sc_stuID}")
    public int deletecourse(int sc_courseID, String sc_stuID);

    //=========================================
    //根据学生id修改学生密码
    @Update("update students set stuPwd=#{newPwd} where stuID=#{stuID}")
    public int updateStudentPwdbyStuID(String newPwd, String stuID);

    //学生修改个人信息
    @Update("update students set semail=#{email},stel=#{tel},sadress=#{adress} where stuID=#{stuID}")
    public int updateStudentInfo(String email, String tel, String adress,String stuID);

    //显示学生个人信息
    @Select("select semail,stel,sadress from students where stuID=#{stuID}")
    public Map<String,Object> selectStudentInfo(String stuID);

}  
