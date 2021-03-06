package ch.bfh.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import ch.bfh.Application;
import ch.bfh.domain.Student;
import ch.bfh.repository.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StudentResource REST controller.
 *
 * @see StudentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StudentResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final String DEFAULT_USERNAME = "SAMPLE_TEXT";
    private static final String UPDATED_USERNAME = "UPDATED_TEXT";

    private static final String DEFAULT_PASSWORD = "SAMPLE_TEXT";
    private static final String UPDATED_PASSWORD = "UPDATED_TEXT";


    @Inject
    private StudentRepository studentRepository;

    private MockMvc restStudentMockMvc;

    private Student student;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StudentResource studentResource = new StudentResource();
        ReflectionTestUtils.setField(studentResource, "studentRepository", studentRepository);
        this.restStudentMockMvc = MockMvcBuilders.standaloneSetup(studentResource).build();
    }

    @Before
    public void initTest() {
        student = new Student();
        student.setName(DEFAULT_NAME);
        student.setUsername(DEFAULT_USERNAME);
        student.setPassword(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void createStudent() throws Exception {
        // Validate the database is empty
        assertThat(studentRepository.findAll()).hasSize(1);

        // Create the Student
        restStudentMockMvc.perform(post("/app/rest/students")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(student)))
                .andExpect(status().isOk());

        // Validate the Student in the database
        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(2);
        Student testStudent = students.get(1);
        assertThat(testStudent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStudent.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testStudent.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void getAllStudents() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the students
        restStudentMockMvc.perform(get("/app/rest/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    public void getStudent() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get the student
        restStudentMockMvc.perform(get("/app/rest/students/{id}", student.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(student.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()));
    }

    @Test
    public void getStudentByName() throws Exception {
        restStudentMockMvc.perform(post("/app/rest/students")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(student)))
            .andExpect(status().isOk());

        assertThat(studentRepository.getStudentByName(DEFAULT_NAME)).isNotNull();
    }

    @Test
    @Transactional
    public void getNonExistingStudent() throws Exception {
        // Get the student
        restStudentMockMvc.perform(get("/app/rest/students/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStudent() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Update the student
        student.setName(UPDATED_NAME);
        student.setUsername(UPDATED_USERNAME);
        student.setPassword(UPDATED_PASSWORD);
        restStudentMockMvc.perform(post("/app/rest/students")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(student)))
                .andExpect(status().isOk());

        // Validate the Student in the database
        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(2);
        Student testStudent = students.get(1);
        assertThat(testStudent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStudent.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testStudent.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void deleteStudent() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get the student
        restStudentMockMvc.perform(delete("/app/rest/students/{id}", student.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(1);
    }
}
