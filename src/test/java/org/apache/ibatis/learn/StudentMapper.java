package org.apache.ibatis.learn;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Student)表数据库访问层
 *
 * @author Cyrus Chen
 * @since 2022-12-17 09:35:43
 */
public interface StudentMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param studentVO 主键
     * @return 实例对象
     */
    StudentDO queryOne(StudentVO studentVO);

    /**
     * 查询指定行数据
     *
     * @return 对象列表
     */
    List<StudentDO> queryAll();

    /**
     * 新增数据
     *
     * @param studentVO 实例对象
     * @return 影响行数
     */
    int insert(StudentVO studentVO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<StudentVO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<StudentVO> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<StudentVO> 实例对象列表
     * @return 影响行数
     * @throws 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<StudentVO> entities);

    /**
     * 修改数据
     *
     * @param studentVO 实例对象
     * @return 影响行数
     */
    int update(StudentVO studentVO);

    /**
     * 通过主键删除数据
     *
     * @param studentVO 主键
     * @return 影响行数
     */
    int deleteOne(StudentVO studentVO);

}

