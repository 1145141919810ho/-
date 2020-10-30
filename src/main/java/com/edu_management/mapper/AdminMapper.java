package com.edu_management.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.edu_management.beans.admin;

public interface AdminMapper {
	//管理员登录
	@Select("select * from CourseSystem.admins where adID=#{adID}")
	public admin selectInfoById(String adID); 
	//管理员待审核课程
	@Select("SELECT courseID, courseName, courseContain,needMedia, courseFrequency,teaName FROM course JOIN teachers ON course.teaID=teachers.teaID where pass=0;")
	public List<Map<String,Object>> selectCourseRequest();
	//查询已经开通的教室
	@Select("SELECT * FROM rooms")
	public List<Map<String,Object>> selectRoomCanChoose();
	//教室录入
	@Insert("insert into rooms values(#{roomID},#{roomContain},#{hasMedia})")
	public int insertRoom(String roomID,String roomContain,String hasMedia);
	
	//需要两步完成教室信息修改
	//教室信息修改1修改room表信息
	@Update("update rooms set roomContain=#{roomContain},hasMedia=#{hasMedia} where roomID=#{roomID}")
	public int updateRoom (String roomID,String roomContain,String hasMedia);
	//修改course表信息
	@Update("update course set roomContain=#{roomContain},hasMedia=#{hasMedia} where roomID=#{roomID}")
	public int updateCourse (String roomID,String roomContain,String hasMedia);
	
	//课程审批通过
	@Update("update course set pass=1 where courseID=#{courseID}")
	public int courseRequestPass(int courseID);
	//课程审批拒绝
	@Delete("Delete from course where pass=0 and courseID=#{courseID}")
	public int courseDeleteById(int courseID);
	//一共需要四步连带删除
	//删除教室1(连带删除课程和学生选课)
	@Delete("delete from rooms where roomID=#{roomID}")
	public int deleteRoom(String roomID);
	//选出占用教室的课程号2
	@Select("Select courseID from course where roomID=#{roomID}")
	public List<Map<String, Object>> findCourseID(String roomID);
	//删除带有课程号的选课表数据3
	@Delete("Delete from sc where sc_courseID=#{courseID}")
	public int deleteScCourse(String courseID);
	//删除占用教室的课程
	@Delete("Delete from course where roomID=#{roomID}")
	public int deleteCourseCourse(String roomID);

	//=========================================
	//根据管理员id修改管理员密码
	@Update("update admins set adPwd=#{newPwd} where adID=#{adID}")
	public int updateAdminPwdbyAdID(String newPwd,String adID);

	//管理员修改个人信息
	@Update("update admins set aemail=#{email},atel=#{tel},aadress=#{adress} where adID=#{adID}")
	public int updateAdminInfo(String email,String tel,String adress,String adID);

	//显示管理员个人信息
	@Select("select aemail,atel,aadress from admins where adID=#{adID}")
	public Map<String,Object> selectAdminInfo(String adID);

}
