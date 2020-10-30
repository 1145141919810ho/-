package com.edu_management.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import com.edu_management.beans.teacher;
import org.apache.ibatis.annotations.Update;

public interface TeacherMapper {
    @Select("select * from CourseSystem.teachers where teaID=#{teaID}")
    public teacher selectInfoById(String teaID);

    //教师被选课情况查询
    @Select("select stuName,stuID from students join sc on sc.sc_stuID=students.stuID join course on course.courseID=sc.sc_courseID where teaID=#{teaID} and pass=\"1\" and courseID=#{courseID}")
    public List<Map<String, Object>> selectMyStudent(String teaID, String courseID);

    //教师查询自己已开通的课程
    @Select("SELECT courseID,courseName,roomID FROM course WHERE pass=1 AND teaID=#{teaID}")
    public List<Map<String, Object>> selectMyCourse(String teaID);

    //开通课程
    @Insert("insert into course(courseName,courseContain,courseContainRest,needMedia,courseFrequency,pass,teaID,roomID) values(#{courseName},#{courseContain},#{courseContainRest},#{needMedia},#{courseFrequency},0,#{teaID},#{roomID})")
    public int courseRegist(String courseName, String courseContain,
                            String courseContainRest, String needMedia, String courseFrequency,
                            String teaID, String roomID);

    //一共需要两步连带删除
    //删除课程第一步
    @Delete("delete from course where courseID=#{courseID} and teaID=#{teaID}")
    public int teacherdeletecourse(int courseID, String teaID);

    //删除课程第二步（连带删除学生选课）
    @Delete("delete from sc where sc_courseID=#{courseID}")
    public int teacherdeletecourse2(int courseID);

    //=========================================
    //根据教师id修改教师密码
    @Update("update teachers set teaPwd=#{newPwd} where teaID=#{teaID}")
    public int updateTeacherPwdbyTeaID(String newPwd, String teaID);

    //教师修改个人信息
    @Update("update teachers set temail=#{email},ttel=#{tel},tadress=#{adress} where teaID=#{teaID}")
    public int updateTeacherInfo(String email, String tel, String adress,String teaID);

    //显示教师个人信息
    @Select("select temail,ttel,tadress from teachers where teaID=#{teaID}")
    public Map<String,Object> selectTeacherInfo(String stuID);
}
