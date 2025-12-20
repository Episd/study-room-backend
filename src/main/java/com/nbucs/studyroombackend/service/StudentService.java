package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.StudentUser;

public interface StudentService {
    public StudentUser checkSelfInformation(StudentUser student);
    public boolean modifySelfInformation(StudentUser student);

    /**
     * 扣除学生积分
     * @param studentId 学生ID
     * @param points 要扣除的积分数
     * @return 扣除是否成功
     */
    boolean deductStudentPoints(Integer studentId, Integer points);

    /**
     * 恢复扣除的学生积分
     * @param studentId 学生ID
     * @param points 要恢复的积分数
     * @return 扣除是否成功
     */
    boolean addStudentPoints(Integer studentId, Integer points);

    /**
     * 重置所有学生积分为默认值100
     * @return 重置成功的学生数量
     */
    int resetAllStudentsPoints();
}
