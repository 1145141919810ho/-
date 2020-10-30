package com.edu_management.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edu_management.JWT.JWTUtil;
import com.edu_management.Utils.MyBatisUtil;
import com.edu_management.mapper.AdminMapper;

@CrossOrigin
@RestController
public class AdminServices {
    @Autowired
    HttpServletResponse response;

    @RequestMapping(value = "/adminLogin", method = {RequestMethod.POST})
    public Map<String, Object> Login(@RequestBody Map<String, String> admin) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        String Id = admin.get("id");
        String Pwd = admin.get("pwd");
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true);
        AdminMapper taskAdminMapper = session.getMapper(AdminMapper.class);
        com.edu_management.beans.admin resultAdmin = taskAdminMapper.selectInfoById(Id);
        if (resultAdmin != null && resultAdmin.getIdString().equals(Id) && resultAdmin.getPwdString().equals(Pwd)) {
            data.put("adName", resultAdmin.getNameString());
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

    @RequestMapping(value = "/courseRequestWait", method = {RequestMethod.POST})
    public Map<String, Object> courseRequestWait() {

        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        try {
            AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
            List<Map<String, Object>> row = new ArrayList<Map<String, Object>>();
            row = adminMapper.selectCourseRequest();

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("total", row.size());
            data.put("rows", row);

            return_data.put("code", 0);
            return_data.put("msg", "成功");
            return_data.put("data", data);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return return_data;
    }

    //可选课程
    @RequestMapping(value = "/roomCanChoose", method = {RequestMethod.POST})
    public Map<String, Object> roomCanChoose() {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        try {
            AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
            List<Map<String, Object>> row = new ArrayList<Map<String, Object>>();
            row = adminMapper.selectRoomCanChoose();

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("total", row.size());
            data.put("rows", row);

            return_data.put("code", 0);
            return_data.put("msg", "成功");
            return_data.put("data", data);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return return_data;
    }

    //教室输入
    @RequestMapping(value = "/roomInput", method = {RequestMethod.POST})
    public Map<String, Object> roomInput(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        //待改写
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        String roomNum = (String) in_data.get("roomNum");
        String roomContain = (String) in_data.get("roomContain");
        String hasMedia = (String) in_data.get("hasMedia");
        //

        try {
            if (JWTUtil.verify(token) != null) {
                AdminMapper adminMapper = session.getMapper(AdminMapper.class);
                adminMapper.insertRoom(roomNum, roomContain, hasMedia);
                return_data.put("code", 0);
                return_data.put("msg", "操作成功");
            } else {
                return_data.put("code", 1);
                return_data.put("msg", "身份过期请重新登陆");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return return_data;
    }

    //教室信息修改
    @RequestMapping(value = "/roomInfoModify", method = {RequestMethod.POST})
    public Map<String, Object> roomInfoModify(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        String roomID = (String) in_data.get("roomNum");
        String roomContain = (String) in_data.get("roomContain");
        String hasMedia = (String) in_data.get("hasMedia");
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");
        try {
            if (JWTUtil.verify(token) != null) {
                AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
                adminMapper.updateRoom(roomID, roomContain, hasMedia);
//				adminMapper.updateCourse(roomID, roomContain, hasMedia);
                return_data.put("code", 0);
                return_data.put("msg", "操作成功");
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

    //课程审批通过
    @CrossOrigin
    @RequestMapping(value = "/courseRequestPass", method = {RequestMethod.POST})
    public Map<String, Object> courseRequestPass(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        List<Integer> temp = (List<Integer>) in_data.get("courseID");
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                for (int i = 0; i < temp.size(); i++) {
                    AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
                    int a = adminMapper.courseRequestPass(temp.get(i));
                }
                return_data.put("code", 0);
                return_data.put("msg", "已经批准");
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

    //课程审批拒绝
    @RequestMapping(value = "/courseRequestRefuse", method = {RequestMethod.POST})
    public Map<String, Object> courseRequestRefuse(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        List<Integer> temp = (List<Integer>) in_data.get("courseID");
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                for (int i = 0; i < temp.size(); i++) {
                    AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
                    int a = adminMapper.courseDeleteById(temp.get(i));
                }
                return_data.put("code", 0);
                return_data.put("msg", "已经拒绝");
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

    //删除教室
    @RequestMapping(value = "/roomDelete", method = {RequestMethod.POST})
    public Map<String, Object> roomDelete(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        @SuppressWarnings("unchecked")
        List<String> temp = (List<String>) in_data.get("roomID");
        Map<String, Object> return_data = new HashMap<String, Object>();
        List<Map<String, Object>> courseIDList = new ArrayList<Map<String, Object>>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                for (int i = 0; i < temp.size(); i++) {
                    AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
                    //第一步删除教室
                    adminMapper.deleteRoom(temp.get(i));
                    //第二步选出占用教室的课程号
                    courseIDList = adminMapper.findCourseID(temp.get(i));
                    //第三步从选课表中删除带有课程号的选课记录
                    if (courseIDList != null) {
                        for (int j = 0; j < courseIDList.size(); j++) {
                            adminMapper.deleteScCourse(courseIDList.get(j).get("courseID").toString());
                        }
                        //第四步删除占用教室的课程
                        adminMapper.deleteCourseCourse(temp.get(i));
                    }
                }

                return_data.put("code", 0);
                return_data.put("msg", "删除教室成功");
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
    //管理员修改密码
    @RequestMapping(value = "/adminUpdatePwd", method = {RequestMethod.POST})
    public Map<String, Object> adminUpdatePwd(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);

        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
                int a = adminMapper.updateAdminPwdbyAdID((String) in_data.get("adPwd"), JWTUtil.verify(token));

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


    //管理员修改个人信息
    @RequestMapping(value = "/adminUpdateInfo", method = {RequestMethod.POST})
    public Map<String, Object> adminUpdateInfo(@RequestBody Map<String, Object> in_data, HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);

        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
                int a = adminMapper.updateAdminInfo((String) in_data.get("email"), (String) in_data.get("tel"), (String) in_data.get("adress"),JWTUtil.verify(token));

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
    //显示管理员个人信息
    @RequestMapping(value = "/showAdminInfo", method = {RequestMethod.POST})
    public Map<String, Object> showAdminInfo(HttpServletRequest request) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true);
        Map<String, Object> return_data = new HashMap<String, Object>();
        String token = request.getHeader("token");

        try {
            if (JWTUtil.verify(token) != null) {
                AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
                return_data = adminMapper.selectAdminInfo(JWTUtil.verify(token));
                if(return_data.get("atel") == null || return_data.get("aemail") == null || return_data.get("aadress") == null || return_data.get("atel").equals("") || return_data.get("aemail").equals("") || return_data.get("aadress").equals("")){
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
