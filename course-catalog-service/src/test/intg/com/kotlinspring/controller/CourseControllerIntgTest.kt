package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.util.courseEntityList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class CourseControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()

//        val instructor = instructorEntity()
//        instructorRepository.save(instructor)

        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {
        // Given
        val courseDTO = CourseDTO(
            null,
            "Build RestFul APis using Spring Boot and Kotlin", "Dilip Sundarraj"
        )
        // When
        var savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody
        // Then
        assertTrue {
            savedCourseDTO!!.id != null
        }
    }

    @Test
    fun retrieveAllCourses() {


        val courseDTOs = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        println("courseDTOs : $courseDTOs")

        assertEquals(3, courseDTOs!!.size)

    }

    @Test
    fun updateCourse() {

        val courseEntity = Course(
            null,
            "Apache Kafka for Developers using Spring Boot", "Development",
        )
        courseRepository.save(courseEntity)
        val updatedCourseEntity = Course(
            null,
            "Apache Kafka for Developers using Spring Boot1", "Development"
        )

        val updatedCourseDTO = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", courseEntity.id)
            .bodyValue(updatedCourseEntity)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals("Apache Kafka for Developers using Spring Boot1", updatedCourseDTO?.name)

    }

    @Test
    fun deleteCourse() {

        val courseEntity = Course(
            null,
            "Apache Kafka for Developers using Spring Boot", "Development",
        )

        courseRepository.save(courseEntity)
        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", courseEntity.id)
            .exchange()
            .expectStatus().isNoContent

    }
}