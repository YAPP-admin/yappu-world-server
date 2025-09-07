package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttendanceRepository :
    JpaRepository<AttendanceEntity, UUID>,
    KotlinJdslJpqlExecutor
//    CustomJdslJpqlExecutor
