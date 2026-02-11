package com.nsbm.studenthub.controller;

import com.nsbm.studenthub.entity.Student;
import com.nsbm.studenthub.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository repo;

    // ADMIN only: create
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Student create(@RequestBody Student s) {
        return repo.save(s);
    }

    // ADMIN only: update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student s) {
        Student existing = repo.findById(id).orElseThrow();
        existing.setName(s.getName());
        existing.setEmail(s.getEmail());
        existing.setBatch(s.getBatch());
        existing.setGpa(s.getGpa());
        return repo.save(existing);
    }

    // ADMIN only: delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "Deleted";
    }

    // Any logged user: get by id
    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    // Any logged user: pagination + sorting
    // Example: /api/students?page=0&size=5&sortBy=name&dir=asc
    @GetMapping
    public Page<Student> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        Sort sort = dir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return repo.findAll(pageable);
    }
}
