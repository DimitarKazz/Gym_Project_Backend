package com.example.gym.repository;

import com.example.gym.entity.User;
import com.example.gym.entity.Program;
import com.example.gym.entity.UserProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgramRepository extends JpaRepository<UserProgram, Long> {

    List<UserProgram> findByUser(User user);

    Optional<UserProgram> findByUserAndProgram(User user, Program program);

    @Query("SELECT up FROM UserProgram up WHERE up.user = :user AND up.program = :program AND up.isActive = true AND up.remainingDays > 0 AND up.expiryDate > CURRENT_TIMESTAMP")
    Optional<UserProgram> findActiveUserProgram(@Param("user") User user, @Param("program") Program program);

    List<UserProgram> findByUserAndIsActiveTrue(User user);

    @Query("SELECT up FROM UserProgram up WHERE up.user = :user AND up.isActive = true AND up.remainingDays > 0 AND up.expiryDate > CURRENT_TIMESTAMP")
    List<UserProgram> findValidUserPrograms(@Param("user") User user);

    @Query("SELECT up FROM UserProgram up WHERE up.isActive = true AND up.remainingDays > 0 AND up.expiryDate > CURRENT_TIMESTAMP")
    List<UserProgram> findAllActivePrograms();

    // 👇 НОВО: Провери дали корисникот ја купил програмата (независно од статусот)
    boolean existsByUserAndProgram(User user, Program program);

    // 👇 НОВО: Провери дали корисникот има ВАЛИДЕН пристап до програмата
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserProgram up " +
            "WHERE up.user = :user AND up.program = :program AND " +
            "up.isActive = true AND up.remainingDays > 0 AND up.expiryDate > CURRENT_TIMESTAMP")
    boolean hasValidAccessToProgram(@Param("user") User user, @Param("program") Program program);

    // 👇 НОВО: Најди по User ID и Program ID
    @Query("SELECT up FROM UserProgram up WHERE up.user.id = :userId AND up.program.id = :programId")
    Optional<UserProgram> findByUserIdAndProgramId(@Param("userId") Long userId, @Param("programId") Long programId);

    // 👇 НОВО: Провери валиден пристап по ID-ја
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserProgram up " +
            "WHERE up.user.id = :userId AND up.program.id = :programId AND " +
            "up.isActive = true AND up.remainingDays > 0 AND up.expiryDate > CURRENT_TIMESTAMP")
    boolean hasValidAccessByIds(@Param("userId") Long userId, @Param("programId") Long programId);
}