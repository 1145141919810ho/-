package com.edu_management.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.edu_management.mapper.AdminMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edu_management.JWT.JWTUtil;
import com.edu_management.Utils.MyBatisUtil;
import com.edu_management.mapper.StudentMapper;

@CrossOrigin
@RestController
public class StudentServices {
    @Autowired
    HttpServletResponse response;

    //学生登录
    @RequestMapping(value = "/studentLogin", method = {RequestMethod.POST})
    public Map<String, Object> Login(@RequestBody Map<String, String> admin) {
        Map<String, Object> result = new HashMap<String, Object>(); //声明返回数据
        Map<String, Object> data = new HashMap<String, Object>(); //声明返回数据
        String Id = admin.get("id"); //获取id
        String Pwd = admin.get("pwd"); //获取密码
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true); //建立数据库连接
        StudentMapper taskstudentMapper = session.getMapper(StudentMapper.class); //获取mapper接口
        com.edu_management.beans.student resultStudent = taskstudentMapper.selectInfoById(Id); //调用mapper接口查询数据库
        if (resultStudent != null && resultStudent.getIdString().equals(Id) && resultStudent.getPwdString().equals(Pwd)) {
            data.put("stuName", resultStudent.getNameString());
            result.put("code", 0);
            result.put("msg", "登录成功");
            result.put("data", data);
            response.addHeader("Access-Control-Expose-Headers", "token"); //生成token
            response.setHeader("token", JWTUtil.sign(Id));
        } else {
            result.put("code", 1);
            result.put("msg", "账号或密码错误");
        }
        session.close();
        return result; //返回数据
    }

    @RequestMapping(value = "/studentCourse", method = {RequestMethod.POST})
    public Map<String, Object> mycourse(HttpServletRequest request) {
        String token = request.getHeader("token");
        String id = JWTUtil.verify(token); //获取学生id

        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>(); //声明返回数据
        try {//验证身份
            if (id != null) {
                StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class); //获取查询接口
                List<Map<String, Object>> row = new ArrayList<Map<String, Object>>(); //声明储存结果集变量
                row = studentMapper.selectScById(id); //根据学生id查询已选的课程信息

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
            sqlSession.close();//关闭连接
        }
        return return_data;//返回数据
    }

    //待选课程查询
    @RequestMapping(value = "/courseCanChoose", method = {RequestMethod.POST})
    public Map<String, Object> courseCanChoose(HttpServletRequest request) {

        String token = request.getHeader("token");
        String sid = JWTUtil.verify(token); //获取学生id
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>(); //声明返回数据
        try {//验证身份
            if (sid != null) {
                StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class); //获取查询接口
                List<Map<String, Object>> row = new ArrayList<Map<String, Object>>(); //声明储存结果集变量
                row = studentMapper.selectCourseCanChoose(sid); //根据学生id查询可选课程

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
            sqlSession.close(); //关闭连接
        }
        return return_data; //返回数据
    }

    //学生选课
    @RequestMapping(value = "/studentCourseChoose", method = {RequestMethod.POST})
    public Map<String, Object> studentCourseChoose(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true);
        List<Integer> temp = (List<Integer>) in_data.get("courseID");
        Map<String, Object> data = new HashMap<String, Object>();
        String token = request.getHeader("token");
        String stuID = JWTUtil.verify(token);

        StudentMapper studentMapper = session.getMapper(StudentMapper.class);
        for (int i = 0; i < temp.size(); i++) {
            try {
                if (stuID != null) {
                    Map<String, Integer> rest = studentMapper.selectCcrById(temp.get(i));
                    if (rest.get("courseContainRest") != 0) {
                        studentMapper.studentCourseChoose(temp.get(i), stuID);
                        data.put("code", 0);
                        data.put("msg", "选课成功");
                        studentMapper.updateCcrById(rest.get("courseContainRest"), temp.get(i));
                    } else {
                        data.put("code", 1);
                        data.put("msg", "所选课程有余量不足课程，请刷新重试");
                        break;
                    }
                } else {
                    data.put("code", 1);
                    data.put("msg", "身份过期请重新登陆");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        session.close();
        return data;
    }

    //学生退选
    @CrossOrigin
    @RequestMapping(value = "/studentCourseCancel", method = {RequestMethod.POST})
    public Map<String, Object> studentCourseCancel(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        @SuppressWarnings("unchecked")
        List<Integer> temp = (List<Integer>) in_data.get("courseID");
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");
        String stuID = JWTUtil.verify(token);
        for (int i = 0; i < temp.size(); i++) {
            try {
                if (stuID != null) {
                    StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
                    int a = studentMapper.deletecourse(temp.get(i), stuID);
                    return_data.put("code", 0);
                    return_data.put("msg", "退选成功");
                } else {
                    return_data.put("code", 1);
                    return_data.put("msg", "身份过期请重新登陆");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sqlSession.close();
        return return_data;
    }


    //========================================
    //学生修改密码
    @RequestMapping(value = "/studentUpdatePwd", method = {RequestMethod.POST})
    public Map<String, Object> studentUpdatePwd(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true); //建立数据库连接

        Map<String, Object> return_data = new HashMap<String, Object>(); //声明返回数据
        String token = request.getHeader("token"); //获取token
        try {
            if (JWTUtil.verify(token) != null) { //验证token
                StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class); //获取mapper接口
                int a = studentMapper.updateStudentPwdbyStuID((String) in_data.get("stuPwd"), JWTUtil.verify(token)); //调用接口修改密码

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
        return return_data; //返回数据
    }

    //学生修改个人信息
    @RequestMapping(value = "/studentUpdateInfo", method = {RequestMethod.POST})
    public Map<String, Object> studentUpdateInfo(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);

        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
                int a = studentMapper.updateStudentInfo((String) in_data.get("email"), (String) in_data.get("tel"), (String) in_data.get("adress"), JWTUtil.verify(token));

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

    //显示学生个人信息
    @RequestMapping(value = "/showStudentInfo", method = {RequestMethod.POST})
    public Map<String, Object> showStudentInfo(HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
                return_data = studentMapper.selectStudentInfo(JWTUtil.verify(token));

                if (return_data.get("stel") == null || return_data.get("semail") == null || return_data.get("sadress") == null || return_data.get("stel").equals("") || return_data.get("semail").equals("") || return_data.get("sadress").equals("")) {
                    return_data.put("code", 1);
                    return_data.put("msg", "请完善信息");
                } else {
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
