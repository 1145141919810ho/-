package com.edu_management.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.edu_management.mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edu_management.JWT.JWTUtil;
import com.edu_management.Utils.MyBatisUtil;
import com.edu_management.mapper.TeacherMapper;

@CrossOrigin
@RestController
public class TeacherServices {
    @Autowired
    HttpServletResponse response;

    @RequestMapping(value = "/teacherLogin", method = {RequestMethod.POST})

    public Map<String, Object> Login(@RequestBody Map<String, String> admin) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        String Id = admin.get("id");
        String Pwd = admin.get("pwd");
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true);
        TeacherMapper taskTeacherMapper = session.getMapper(TeacherMapper.class);
        com.edu_management.beans.teacher resultteacher = taskTeacherMapper.selectInfoById(Id);
        if (resultteacher != null && resultteacher.getIdString().equals(Id) && resultteacher.getPwdString().equals(Pwd)) {
            data.put("teaName", resultteacher.getNameString());
            result.put("code", 0);
            result.put("msg", "登录成功");
            result.put("data", data);
            response.addHeader("Access-Control-Expose-Headers", "token");
            response.setHeader("token", JWTUtil.sign(Id));
        } else {
            result.put("code", 1);
            result.put("msg", "账号或密码错误");
        }
        session.close();
        return result;
    }

    @RequestMapping(value = "/myStudents", method = {RequestMethod.POST})
    public Map<String, Object> mystudent(@RequestBody Map<String, String> in_data, HttpServletRequest request) {

        String courseID = in_data.get("courseID");
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");
        String teaID = JWTUtil.verify(token);

        try {
            if (teaID != null) {
                TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
                List<Map<String, Object>> row = new ArrayList<Map<String, Object>>();
                row = teacherMapper.selectMyStudent(teaID, courseID);

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("total", row.size());
                data.put("rows", row);

                return_data.put("code", 0);
                return_data.put("msg", "成功");
                return_data.put("data", data);
            } else {
                return_data.put("code", 1);
                return_data.put("msg", "身份过期请重新登陆");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return return_data;
    }

    //查看自己的课程
    @RequestMapping(value = "/teacherCourse", method = {RequestMethod.POST})
    public Map<String, Object> teacherCourse(HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");
        String teaID = JWTUtil.verify(token);
        try {
            if (teaID != null) {
                TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
                List<Map<String, Object>> row = new ArrayList<Map<String, Object>>();
                row = teacherMapper.selectMyCourse(teaID);

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("total", row.size());
                data.put("rows", row);

                return_data.put("code", 0);
                return_data.put("msg", "成功");
                return_data.put("data", data);
            } else {
                return_data.put("code", 1);
                return_data.put("msg", "身份过期请重新登陆");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return return_data;
    }

    //课程登记
    @RequestMapping(value = "/courseRegist", method = {RequestMethod.POST})
    public Map<String, Object> courseRegist(@RequestBody Map<String, String> in_data, HttpServletRequest request) {

        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> data = new HashMap<String, Object>();
        String token = request.getHeader("token");
        String teaID = JWTUtil.verify(token);

        try {
            if (teaID != null) {
                TeacherMapper teacherMapper = session.getMapper(TeacherMapper.class);
                teacherMapper.courseRegist(in_data.get("courseName"), in_data.get("courseContain"), in_data.get("courseContain"), in_data.get("needMedia"), in_data.get("courseFrequency"), teaID, in_data.get("roomID"));
                data.put("code", "0");
                data.put("msg", "登记成功，等待管理员审核");
            } else {
                data.put("code", 1);
                data.put("msg", "身份过期请重新登陆");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return data;
    }

    //删除课程
    @RequestMapping(value = "/courseDelete", method = {RequestMethod.POST})
    public Map<String, Object> courseDelete(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        @SuppressWarnings("unchecked")
        List<Integer> temp = (List<Integer>) in_data.get("courseID");
        Map<String, Object> return_data = new HashMap<String, Object>();
        String teaID = JWTUtil.verify(request.getHeader("token"));

        try {
            if (teaID != null) {
                for (int i = 0; i < temp.size(); i++) {
                    TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
                    int a = teacherMapper.teacherdeletecourse(temp.get(i), teaID);
                    //连带删除学生选课
                    int b = teacherMapper.teacherdeletecourse2(temp.get(i));
                }
                return_data.put("code", 0);
                return_data.put("msg", "删除课程成功");
            } else {
                return_data.put("code", 1);
                return_data.put("msg", "身份过期请重新登陆");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }

        return return_data;
    }

    //========================================
    //教师修改密码
    @RequestMapping(value = "/teacherUpdatePwd", method = {RequestMethod.POST})
    public Map<String, Object> teacherUpdatePwd(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);

        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
                int a = teacherMapper.updateTeacherPwdbyTeaID((String) in_data.get("teaPwd"), JWTUtil.verify(token));

                return_data.put("code", 0);
                return_data.put("msg", "修改成功");
            } else {
                return_data.put("code", 1);
                return_data.put("msg", "身份过期请重新登陆");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return return_data;
    }

    //教师修改个人信息
    @RequestMapping(value = "/teacherUpdateInfo", method = {RequestMethod.POST})
    public Map<String, Object> teacherUpdateInfo(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);

        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
                int a = teacherMapper.updateTeacherInfo((String) in_data.get("email"), (String) in_data.get("tel"), (String) in_data.get("adress"),JWTUtil.verify(token));

                return_data.put("code", 0);
                return_data.put("msg", "修改成功");
            } else {
                return_data.put("code", 1);
                return_data.put("msg", "身份过期请重新登陆");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return return_data;
    }

    //显示教师个人信息
    @RequestMapping(value = "/showTeacherInfo", method = {RequestMethod.POST})
    public Map<String, Object> showTeacherInfo(HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
                return_data = teacherMapper.selectTeacherInfo(JWTUtil.verify(token));
                if(return_data.get("ttel") == null || return_data.get("temail") == null || return_data.get("tadress") == null || return_data.get("ttel").equals("") || return_data.get("temail").equals("") || return_data.get("tadress").equals("")){
                    return_data.put("code", 1);
                    return_data.put("msg", "请完善信息");
                }else{
                    return_data.put("code", 0);
                    return_data.put("msg", "查询成功");
                }
            } else {
                return_data.put("code", 1);
                return_data.put("msg", "身份过期请重新登陆");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return return_data;
    }

}
